package br.zestski.owlvintage.fragment.home.pages.timeline;

import static br.zestski.owlvintage.OwlerApplication.getAccountManager;
import static br.zestski.owlvintage.common.utils.CoroutineUtil.execute;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.databinding.FragmentTimelineBinding;
import br.zestski.owlvintage.fragment.home.HomeFragment;
import br.zestski.owlvintage.fragment.home.adapter.RecyclerViewAdapter;
import br.zestski.owlvintage.fragment.home.pages.timeline.observer.TimelineLoadObserver;
import br.zestski.owlvintage.fragment.home.pages.timeline.observer.TimelineUpdateObserver;
import br.zestski.owlvintage.models.status.StatusModel;
import br.zestski.owlvintage.services.OwlerApiService;
import br.zestski.owlvintage.services.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Zestski
 */
public class TimelineFragment extends Fragment implements TimelineUpdateObserver {

    private final HomeFragment homeFragment;
    private final RecyclerViewAdapter.ScrollAwareFloatingActionButtonBehavior behavior;
    private final boolean isFriendsEndpoint;
    private FragmentTimelineBinding fragmentTimelineBinding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TimelineLoadObserver timelineLoadObserver;

    public TimelineFragment(
            @NonNull HomeFragment homeFragment,
            @NonNull RecyclerViewAdapter.ScrollAwareFloatingActionButtonBehavior behavior,
            boolean isFriendsEndpoint
    ) {
        this.homeFragment = homeFragment;
        this.behavior = behavior;
        this.isFriendsEndpoint = isFriendsEndpoint;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        fragmentTimelineBinding = FragmentTimelineBinding.inflate(inflater, container, false);
        return fragmentTimelineBinding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        homeFragment.registerStatusPublishListener(this);

        recyclerView = fragmentTimelineBinding.fragmentTimelineRecyclerView;
        swipeRefreshLayout = fragmentTimelineBinding.fragmentTimelineSwipeRefreshLayout;

        setupSwipeRefreshLayout();
        setupRecyclerView();
        loadTimeline();
    }

    @Override
    public void onStatusPublished() {
        loadTimeline();
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(this::loadTimeline);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addOnScrollListener(behavior);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTimeline() {
        List<StatusModel> statusModelList = new ArrayList<>();

        recyclerViewAdapter = new RecyclerViewAdapter(statusModelList);
        recyclerView.setAdapter(recyclerViewAdapter);

        execute(() -> {
            showSwipeRefreshLayout();

            var apiService = RetrofitClient.getClientForOwlerCloud().create(OwlerApiService.class);

            var accountManager = getAccountManager();
            var defaultAccount = accountManager.getDefaultAccount();

            var authorizationKey = Objects.requireNonNull(defaultAccount).getEncryptedAuth();

            Call<List<StatusModel>> timelineCall;

            if (isFriendsEndpoint) {
                timelineCall = apiService.getHomeTimeline(authorizationKey, 1, 50, true);
            } else {
                timelineCall = apiService.getPublicTimeline(authorizationKey, 1, 50, true);
            }

            timelineCall.enqueue(new Callback<>() {
                @Override
                public void onResponse(
                        @NonNull Call<List<StatusModel>> call,
                        @NonNull Response<List<StatusModel>> response
                ) {
                    if (response.isSuccessful()) {
                        var statusModelList = response.body();
                        recyclerViewAdapter.updateStatusList(Objects.requireNonNull(statusModelList));
                        hideSwipeRefreshLayout(true);
                    } else {
                        showErrorSnackBar();
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<List<StatusModel>> call,
                        @NonNull Throwable t
                ) {
                    showErrorSnackBar();
                    hideSwipeRefreshLayout(false);
                }
            });
        });
    }

    private void showErrorSnackBar() {
        requireActivity().runOnUiThread(() -> {
            Snackbar.make(requireView(), getString(R.string.timeline_fragment_error_message), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.timeline_fragment_error_try_again), v -> loadTimeline()).show();
            handleLoadObserver(true);
        });
    }

    private void showSwipeRefreshLayout() {
        requireActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.setRefreshing(true);
            handleLoadObserver(true);
        });
    }

    private void hideSwipeRefreshLayout(
            boolean enforceDisable
    ) {
        requireActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);

            if (enforceDisable)
                handleLoadObserver(false);
        });
    }

    private void handleLoadObserver(
            boolean disableIt
    ) {
        if (disableIt) {
            timelineLoadObserver.onTimelineLoadStarted();
        } else {
            timelineLoadObserver.onTimelineLoadFinished();
        }
    }

    public void registerTimelineLoadObserver(
            @NonNull TimelineLoadObserver observer
    ) {
        this.timelineLoadObserver = observer;
    }
}