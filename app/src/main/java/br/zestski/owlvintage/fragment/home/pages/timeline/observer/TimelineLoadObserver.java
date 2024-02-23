package br.zestski.owlvintage.fragment.home.pages.timeline.observer;

/**
 * Interface to watch the timeline load.
 *
 * @author Zestski
 */
public interface TimelineLoadObserver {
    void onTimelineLoadStarted();

    void onTimelineLoadFinished();
}