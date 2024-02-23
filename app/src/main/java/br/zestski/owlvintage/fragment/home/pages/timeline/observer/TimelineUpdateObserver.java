package br.zestski.owlvintage.fragment.home.pages.timeline.observer;

/**
 * Interface to watch if the user sends a new status.
 *
 * @author Zestski
 */
public interface TimelineUpdateObserver {
    void onStatusPublished();
}