package com.teamtwo.engine.Messages;

public interface Message {

    enum Type {
        Collision,
        LevelOver
    }

    Type getType();
}
