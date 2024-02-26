package br.zestski.owlvintage.fragment.login;

import static br.zestski.owlvintage.OwlerApplication.getAccountManager;
import static br.zestski.owlvintage.common.utils.CoroutineUtil.execute;
import static br.zestski.owlvintage.util.B64Util.encodeBase64;
import static br.zestski.owlvintage.util.ResponseUtil.responseFallback;

import android.os.Bundle;
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
import br.zestski.owlvintage.account.UserAccountImpl;
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

            var email = Objects.requireNonNull(tilEtEmail.getEditText()).getText().toString();
            var password = Objects.requireNonNull(tilEtPassword.getEditText()).getText().toString();

            var authorizationKey = String.format("Basic %s", encodeBase64(String.format("%s:%s", email, password)));

            var authApiCall = apiService.verifyCredentials(new String(authorizationKey.getBytes(StandardCharsets.UTF_8)));

            authApiCall.enqueue(new Callback<>() {
                @Override
                public void onResponse(
                        @NonNull Call<APIResponse> call,
                        @NonNull Response<APIResponse> response
                ) {
                    var authenticationResponse = responseFallback(response, APIResponse.class);

                    if (response.isSuccessful()) {
                        var accountManager = getAccountManager();

                        var newUserAccount = new UserAccountImpl("", email, password);

                        for (var account : accountManager.getAccounts()) {
                            if (account.getEmail().equals(newUserAccount.getEmail())) {
                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle(getString(R.string.login_fragment_already_have_this_account_title))
                                        .setMessage(getString(R.string.login_fragment_already_have_this_account_message))
                                        .setPositiveButton(getString(R.string.common_yes_button), (d, w) -> {
                                            accountManager.removeAccount(account);
                                            accountManager.addAccount(newUserAccount);
                                            switchToSplashFragment();
                                        })
                                        .setNegativeButton(getString(R.string.common_no_button), null)
                                        .setCancelable(false)
                                        .create().show();
                                return;
                            }
                        }

                        accountManager.addAccount(newUserAccount);
                        accountManager.setDefaultAccount(newUserAccount);

                        switchToSplashFragment();
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

    private void switchToSplashFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_fragment_container, new SplashFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss();
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
}