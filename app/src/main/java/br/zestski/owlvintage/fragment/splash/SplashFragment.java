package br.zestski.owlvintage.fragment.splash;

import static br.zestski.owlvintage.OwlerApplication.getAccountManager;
import static br.zestski.owlvintage.common.utils.CoroutineUtil.execute;
import static br.zestski.owlvintage.util.ResponseUtil.responseFallback;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.databinding.FragmentSplashBinding;
import br.zestski.owlvintage.fragment.home.HomeFragment;
import br.zestski.owlvintage.fragment.login.LoginFragment;
import br.zestski.owlvintage.models.response.OverTheAirResponse;
import br.zestski.owlvintage.services.OwlerApiService;
import br.zestski.owlvintage.services.RetrofitClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Zestski
 */
@SuppressWarnings("SpellCheckingInspection")
public class SplashFragment extends Fragment {

    public static final String TAG = SplashFragment.class.getSimpleName();

    private BottomSheetDialog bottomSheetDialog;

    private LinearProgressIndicator linearProgressIndicator;
    private AppCompatTextView title, message;

    private boolean isTimerRunning;

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.Y, true));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        var fragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false);
        return fragmentSplashBinding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setContentView(R.layout.layout_progress_dialog_ota);

        title = bottomSheetDialog.findViewById(R.id.layout_progress_dialog_ota_title);
        message = bottomSheetDialog.findViewById(R.id.layout_progress_dialog_ota_message);
        linearProgressIndicator = bottomSheetDialog.findViewById(R.id.layout_progress_dialog_ota_progress_linear);

        Objects.requireNonNull(linearProgressIndicator).setIndeterminate(false);
        Objects.requireNonNull(linearProgressIndicator).setMax(100);

        handleSplash();
    }

    /**
     * Starts the splash screen process.
     * <p>
     * This method initiates the splash screen process by displaying a bottom sheet dialog with a progress indicator,
     * making an API call to check for over-the-air updates, and handling the response accordingly.
     * If an update is available, it downloads the update APK file in a separate thread using OkHttp,
     * displays the download progress, installs the APK, and dismisses the bottom sheet dialog.
     * If no update is available or if an error occurs during the API call, it displays an error message in the bottom sheet dialog.
     * </p>
     */
    @SuppressLint("SetTextI18n")
    private void startSplash() {
        execute(() -> {
            requireActivity().runOnUiThread(() -> bottomSheetDialog.show());

            var apiService = RetrofitClient.getClientForOwlVintage().create(OwlerApiService.class);

            var otaApiCall = apiService.getOverTheAir(getVersionCode());

            otaApiCall.enqueue(new Callback<>() {
                @Override
                public void onResponse(
                        @NonNull Call<OverTheAirResponse> call,
                        @NonNull Response<OverTheAirResponse> response
                ) {
                    var otaResponse = responseFallback(response, OverTheAirResponse.class);

                    if (response.code() == 200) {
                        requireActivity().runOnUiThread(() -> bottomSheetDialog.dismiss());
                        switchFragment();
                    } else if (response.code() == 201) {
                        requireActivity().runOnUiThread(() -> {
                            title.setText(otaResponse.getVersionCodename());
                            message.setText(otaResponse.getChangelog());
                            linearProgressIndicator.setVisibility(View.VISIBLE);
                        });

                        /* Seperate thread for OkHttp */

                        execute(() -> {
                            var file = new File(requireContext().getExternalCacheDir() + "/update.apk");
                            var apkUri = Uri.fromFile(file);

                            BufferedSink bufferedSink = null;

                            var request = new Request.Builder()
                                    .url(otaResponse.getDownloadUrl())
                                    .get()
                                    .build();

                            var okHttpClient = new OkHttpClient.Builder()
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .callTimeout(120, TimeUnit.SECONDS)
                                    .build();

                            try (okhttp3.Response responseOkHttp = okHttpClient.newCall(request).execute()) {
                                ResponseBody requestBody = responseOkHttp.body();
                                long contentLength = Objects.requireNonNull(requestBody).contentLength();
                                BufferedSource source = requestBody.source();

                                bufferedSink = Okio.buffer(Okio.sink(file));

                                var totalRead = 0;
                                long read;
                                int progress;

                                while ((read = source.read(bufferedSink.getBuffer(), 8192)) != -1) {
                                    totalRead += (int) read;
                                    progress = (int) ((totalRead * 100) / contentLength);

                                    var finalProgress = progress;

                                    requireActivity().runOnUiThread(() -> Objects.requireNonNull(linearProgressIndicator).setProgress(finalProgress, true));
                                }

                                Thread.sleep(1250);

                                bufferedSink.writeAll(source);
                                bufferedSink.flush();

                                apkUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);

                                var intent = new Intent(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                                requireActivity().startActivity(intent);

                                dismissBottomSheet();
                            } catch (IOException | InterruptedException exception) {
                                requireActivity().runOnUiThread(() -> new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle(getString(R.string.common_error_title))
                                        .setMessage(getString(R.string.ota_an_unknown_error))
                                        .setCancelable(false)
                                        .setNegativeButton(getString(R.string.common_close_button), (dialog, which) -> requireActivity().finishAffinity()).create().show());

                                Log.e(TAG, "OTA Response: ", exception);

                                dismissBottomSheet();
                            } finally {
                                try {
                                    if (bufferedSink != null)
                                        bufferedSink.close();
                                } catch (IOException exception) {
                                    requireActivity().runOnUiThread(() -> new MaterialAlertDialogBuilder(requireContext())
                                            .setTitle(getString(R.string.common_error_title))
                                            .setMessage(getString(R.string.common_error_message))
                                            .setCancelable(false)
                                            .setNegativeButton(getString(R.string.common_close_button), (dialog, which) -> requireActivity().finishAffinity()).create().show());

                                    Log.e(TAG, "OTA try-catch (finally): ", exception);

                                    dismissBottomSheet();
                                }
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            title.setText(getString(R.string.common_error_title));
                            message.setText(
                                    (otaResponse != null && otaResponse.getError() != null)
                                            ? otaResponse.getError()
                                            : getString(R.string.common_error_message)
                            );
                        });
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<OverTheAirResponse> call,
                        @NonNull Throwable t
                ) {
                    requireActivity().runOnUiThread(() -> new MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(R.string.common_error_title))
                            .setMessage(getString(R.string.common_error_message))
                            .setCancelable(false)
                            .setNegativeButton(getString(R.string.common_close_button), (dialog, which) -> requireActivity().finishAffinity()).create().show());

                    Log.e(TAG, "OTA Failure: ", t);

                    dismissBottomSheet();
                }
            });
        });
    }

    private void switchFragment() {
        var accountManager = getAccountManager();

        Fragment newFragment = (accountManager.getDefaultAccount() == null)
                ? new LoginFragment()
                : new HomeFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.activity_main_fragment_container, newFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @SuppressWarnings("BusyWait")
    private void handleSplash() {
        execute(() -> {
            try {
                Thread.sleep(5000);
                while (isTimerRunning) {
                    Thread.sleep(100);
                }
                startSplash();
            } catch (InterruptedException ignored) {
            }
        });
    }

    private void dismissBottomSheet() {
        requireActivity().runOnUiThread(() -> bottomSheetDialog.dismiss());
    }

    private int getVersionCode() {
        try {
            var pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            return (int) PackageInfoCompat.getLongVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isTimerRunning = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isTimerRunning = true;
    }
}