package br.zestski.owlvintage.fragment.login;

import static br.zestski.owlvintage.OwlerApplication.setSpBoolean;
import static br.zestski.owlvintage.OwlerApplication.setSpString;
import static br.zestski.owlvintage.common.utils.CoroutineUtil.execute;
import static br.zestski.owlvintage.util.ResponseUtil.responseFallback;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.common.Constants;
import br.zestski.owlvintage.databinding.FragmentLoginBinding;
import br.zestski.owlvintage.fragment.login.watcher.EmailPasswordWatcher;
import br.zestski.owlvintage.fragment.splash.SplashFragment;
import br.zestski.owlvintage.models.response.APIResponse;
import br.zestski.owlvintage.services.OwlerApiService;
import br.zestski.owlvintage.services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Zestski
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding fragmentLoginBinding;

    private TextInputLayout tilEtEmail, tilEtPassword;
    private ExtendedFloatingActionButton eFabLogin;

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        tilEtEmail = fragmentLoginBinding.fragmentLoginTilEmail;
        tilEtPassword = fragmentLoginBinding.fragmentLoginTilPassword;
        eFabLogin = fragmentLoginBinding.fragmentWizardFab;

        eFabLogin.shrink();

        setTilWatchers();
        setOnClickListener();
    }

    private void setTilWatchers() {
        var emailPasswordWatcher = new EmailPasswordWatcher(tilEtEmail, tilEtPassword, eFabLogin);

        Objects.requireNonNull(tilEtEmail.getEditText()).addTextChangedListener(emailPasswordWatcher);
        Objects.requireNonNull(tilEtPassword.getEditText()).addTextChangedListener(emailPasswordWatcher);
    }

    private void setOnClickListener() {
        eFabLogin.setOnClickListener(v -> execute(() -> {
            shrinkAndDisableFab();

            var apiService = RetrofitClient.getClientForOwlerCloud().create(OwlerApiService.class);

            var authorizationKey = String.format("Basic %s",
                    encodeBase64(String.format("%s:%s",
                            Objects.requireNonNull(tilEtEmail.getEditText()).getText().toString(),
                            Objects.requireNonNull(tilEtPassword.getEditText()).getText().toString()
                    ))
            );

            var authApiCall = apiService.verifyCredentials(new String(authorizationKey.getBytes(StandardCharsets.UTF_8)));

            authApiCall.enqueue(new Callback<>() {
                @Override
                public void onResponse(
                        @NonNull Call<APIResponse> call,
                        @NonNull Response<APIResponse> response
                ) {
                    var authenticationResponse = responseFallback(response, APIResponse.class);

                    if (response.isSuccessful()) {
                        setSpString(Constants.SP_AUTHORIZATION_KEY, authorizationKey);
                        setSpBoolean(Constants.SP_IS_USER_AUTHENTICATED, true);

                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.activity_main_fragment_container, new SplashFragment())
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    } else {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.common_error_title))
                                .setMessage(Objects.requireNonNull(authenticationResponse).getError())
                                .setPositiveButton(getString(R.string.common_ok_button), null)
                                .create().show();

                        extendAndEnableFab();
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<APIResponse> call,
                        @NonNull Throwable t
                ) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(R.string.common_error_title))
                            .setMessage(getString(R.string.common_error_message))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.common_ok_button), (d, w) -> requireActivity().finishAffinity())
                            .create().show();

                    extendAndEnableFab();
                }
            });
        }));
    }

    private void shrinkAndDisableFab() {
        disableTILs();
        requireActivity().runOnUiThread(() -> {
            eFabLogin.shrink();
            eFabLogin.setEnabled(false);
        });
    }

    private void extendAndEnableFab() {
        enableTILs();
        requireActivity().runOnUiThread(() -> {
            eFabLogin.extend();
            eFabLogin.setEnabled(true);
        });
    }

    private void disableTILs() {
        requireActivity().runOnUiThread(() -> {
            tilEtEmail.setEnabled(false);
            tilEtPassword.setEnabled(false);
        });
    }

    private void enableTILs() {
        requireActivity().runOnUiThread(() -> {
            tilEtEmail.setEnabled(true);
            tilEtPassword.setEnabled(true);
        });
    }

    private String encodeBase64(String input) {
        var bytes = input.getBytes();
        var encodedBytes = Base64.encode(bytes, Base64.NO_WRAP);
        return new String(encodedBytes);
    }
}