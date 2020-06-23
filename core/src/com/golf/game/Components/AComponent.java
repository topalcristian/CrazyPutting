package com.golf.game.Components;

import com.golf.game.GameObjects.GameObject;

public abstract class AComponent {
    protected GameObject owner;

    public GameObject getOwner() {
        return owner;
    }

    public void setOwner(GameObject gameObject) {
        owner = gameObject;
    }
}
