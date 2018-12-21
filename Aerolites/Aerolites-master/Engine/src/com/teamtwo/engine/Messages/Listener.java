package com.teamtwo.engine.Messages;

/**
 * An interface to allow an object to post messages to the system
 */
public interface Listener {

    /**
     * Registers an Observer to watch this listener
     * @param observer The observer which will watch
     * @param type The type of message to watch for
     */
    void registerObserver(Observer observer, Message.Type type);

    /**
     * Removes a given observer from the listener
     * @param observer The observer to remove
     * @return True if the observer is successfully removed, otherwise false
     */
    boolean removeObserver(Observer observer, Message.Type type);

    /**
     * Posts a message to all of the observers which are watching for the type given
     * @param message The message to post
     */
    void postMessage(Message message);

}
