package com.golf.game.Others;

import com.badlogic.gdx.Input.TextInputListener;

public class InputData implements TextInputListener {
    private String text;

    @Override
    public void input(String text) {
        this.text = text;
    }


    @Override
    public void canceled() {

    }

    public String getText() {
        return text;
    }

    public void clearText() {
        text = null;
    }
}
