package com.golf.game.Components.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.golf.game.GameLogic.GraphicsManager;

public class SphereGraphicsComponent extends GraphicsComponent {

    private float _width;
    private float _deep;
    private float _height;


    public SphereGraphicsComponent(float pRadius, Color pColor) {
        _color = pColor;
        _width = pRadius;
        _deep = pRadius;
        _height = pRadius;
        initSphere();
    }


    private void initSphere() {
        GraphicsManager.addGraphics3DComponent(this);
        ModelBuilder modelBuilder = new ModelBuilder();
        _model = modelBuilder.createSphere(_width, _height, _deep, 24, 24, new Material(ColorAttribute.createDiffuse(_color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        _instance = new ModelInstance(_model);
    }

}
