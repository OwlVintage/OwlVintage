package br.zestski.owlvintage.fragment.home.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.fragment.home.HomeFragment;
import br.zestski.owlvintage.fragment.home.pages.timeline.TimelineFragment;

/**
 * Adapter for the ViewPager in the HomeFragment.
 *
 * @author Zestski
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    private final Fragment[] fragments;

    private final Context context;

    public ViewPagerAdapter(
            @NonNull Context context,
            @NonNull HomeFragment homeFragment,
            @NonNull FragmentActivity fragmentActivity,
            @NonNull RecyclerViewAdapter.ScrollAwareFloatingActionButtonBehavior behavior
    ) {
        super(fragmentActivity);

        this.context = context;

        fragments = new Fragment[]{
                createTimelineFragment(homeFragment, behavior, true),
                createTimelineFragment(homeFragment, behavior, false)
        };
    }

    @NonNull
    @Override
    public Fragment createFragment(
            int position
    ) {
        return fragments[position];
    }

    public String getTabName(
            int position
    ) {
        switch (position) {
            case 0 -> {
                return context.getString(R.string.friends_fragment_title);
            }
            case 1 -> {
                return context.getString(R.string.timeline_fragment_title);
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    private Fragment createTimelineFragment(
            @NonNull HomeFragment homeFragment,
            @NonNull RecyclerViewAdapter.ScrollAwareFloatingActionButtonBehavior behavior,
            boolean isFriendsEndpoint
    ) {
        var timelineFragment = new TimelineFragment(
                homeFragment,
                behavior,
                isFriendsEndpoint
        );

        timelineFragment.registerTimelineLoadObserver(homeFragment);

        return timelineFragment;
    }
}