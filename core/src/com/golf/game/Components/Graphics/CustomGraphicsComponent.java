package com.golf.game.Components.Graphics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class CustomGraphicsComponent extends GraphicsComponent {
    public CustomGraphicsComponent(Model pModel) {
        _model = pModel;
        _instance = new ModelInstance(_model);
    }
}
