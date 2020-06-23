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


    public SphereGraphicsComponent(float radius, Color color) {
        this.color = color;
        _width = radius;
        _deep = radius;
        _height = radius;
        initSphere();
    }


    private void initSphere() {
        GraphicsManager.addGraphicsComponent(this);
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(_width, _height, _deep, 24, 24, new Material(ColorAttribute.createDiffuse(color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
    }

}
