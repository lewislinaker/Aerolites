package com.teamtwo.engine.Messages;

/**
 * An interface to allow an object to receive messages
 */
public interface Observer {

    /**
     * Receives a message from the listener
     * @param message The message received
     */
    void receiveMessage(Message message);

}