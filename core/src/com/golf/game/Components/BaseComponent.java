package com.golf.game.Components;

import com.golf.game.GameObjects.GameObject;

public abstract class BaseComponent {
    protected GameObject _owner;

    public void setOwner(GameObject pGameObj) {
        _owner = pGameObj;
    }

    public GameObject get_owner() {
        return _owner;
    }
}
