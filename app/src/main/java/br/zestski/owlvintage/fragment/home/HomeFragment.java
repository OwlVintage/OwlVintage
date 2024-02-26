package br.zestski.owlvintage.fragment.home;

import static br.zestski.owlvintage.OwlerApplication.getAccountManager;
import static br.zestski.owlvintage.util.ResponseUtil.responseFallback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.databinding.FragmentHomeBinding;
import br.zestski.owlvintage.fragment.home.adapter.RecyclerViewAdapter;
import br.zestski.owlvintage.fragment.home.adapter.ViewPagerAdapter;
import br.zestski.owlvintage.fragment.home.pages.timeline.observer.TimelineLoadObserver;
import br.zestski.owlvintage.fragment.home.pages.timeline.observer.TimelineUpdateObserver;
import br.zestski.owlvintage.fragment.home.watcher.CharacterCountWatcher;
import br.zestski.owlvintage.models.response.APIResponse;
import br.zestski.owlvintage.services.OwlerApiService;
import br.zestski.owlvintage.services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Zestski
 */
public class HomeFragment extends Fragment implements TimelineLoadObserver {

    private final List<TimelineUpdateObserver> timelineUpdateObservers = new ArrayList<>();
    private FragmentHomeBinding fragmentHomeBinding;
    private TabLayout tabs;
    private ViewPager2 viewPager2;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        tabs = fragmentHomeBinding.fragmentHomeTabs;
        viewPager2 = fragmentHomeBinding.fragmentHomeViewpager2;
        fab = fragmentHomeBinding.fragmentHomeFab;

        setupTabAndVP2();
        setupOnClickListeners();
    }

    private void setupTabAndVP2() {
        var viewPagerAdapter = new ViewPagerAdapter(requireContext(), this, requireActivity(), new RecyclerViewAdapter.ScrollAwareFloatingActionButtonBehavior(fab));

        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabs, viewPager2, (tab, position) -> tab.setText(viewPagerAdapter.getTabName(position))).attach();
    }

    @SuppressWarnings("ConstantConditions")
    private void setupOnClickListeners() {
        fab.setOnClickListener(v -> {
            var bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(R.layout.layout_send_status);
            bottomSheetDialog.setCancelable(false);

            var closeButton = bottomSheetDialog.findViewById(R.id.layout_status_close_button);
            MaterialButton publishButton = bottomSheetDialog.findViewById(R.id.layout_status_publish_button);
            TextInputLayout tilStatus = bottomSheetDialog.findViewById(R.id.layout_status_input_layout);

            tilStatus.getEditText().addTextChangedListener(new CharacterCountWatcher(publishButton, tilStatus, 1, 280));

            closeButton.setOnClickListener(v1 -> bottomSheetDialog.dismiss());
            publishButton.setOnClickListener(v1 -> {
                var apiService = RetrofitClient.getClientForOwlerCloud().create(OwlerApiService.class);

                var accountManager = getAccountManager();
                var defaultAccount = accountManager.getDefaultAccount();

                var authorizationKey = defaultAccount.getEncryptedAuth();

                var sendStatus = apiService.sendStatus(authorizationKey, tilStatus.getEditText().getText().toString(), requireActivity().getString(R.string.app_name));

                sendStatus.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<APIResponse> call,
                            @NonNull Response<APIResponse> response
                    ) {
                        var statusResponse = responseFallback(response, APIResponse.class);

                        if (response.isSuccessful()) {
                            bottomSheetDialog.dismiss();
                            notifyStatusPublished();
                        } else {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(getString(R.string.common_error_title))
                                    .setMessage(Objects.requireNonNull(statusResponse).getError())
                                    .setPositiveButton(getString(R.string.common_ok_button), null)
                                    .create().show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<APIResponse> call,
                            @NonNull Throwable t
                    ) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.common_error_title))
                                .setMessage(getString(R.string.layout_status_publish_error_message))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.common_ok_button), null)
                                .create().show();
                    }
                });
            });

            bottomSheetDialog.show();
        });
    }

    @Override
    public void onTimelineLoadStarted() {
        fab.setEnabled(false);
    }

    @Override
    public void onTimelineLoadFinished() {
        fab.setEnabled(true);
    }

    public void registerStatusPublishListener(TimelineUpdateObserver listener) {
        timelineUpdateObservers.add(listener);
    }

    private void notifyStatusPublished() {
        for (TimelineUpdateObserver listener : timelineUpdateObservers) {
            listener.onStatusPublished();
        }
    }
}