package com.golf.game.Components.Graphics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class CustomGraphicsComponent extends GraphicsComponent {
    public CustomGraphicsComponent(Model pModel) {
        model = pModel;
        instance = new ModelInstance(model);
    }
}
