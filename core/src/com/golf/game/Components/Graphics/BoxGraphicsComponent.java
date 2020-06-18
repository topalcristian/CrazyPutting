package com.golf.game.Components.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.GameLogic.GraphicsManager;

public class BoxGraphicsComponent extends GraphicsComponent {
    private Vector3 _dimensions;


    public BoxGraphicsComponent(Vector3 pDimensions, Color pColor) {
        _color = pColor;
        _dimensions = pDimensions;
        initBox();
    }


    private void initBox() {
        GraphicsManager.addGraphics3DComponent(this);
        ModelBuilder modelBuilder = new ModelBuilder();
        _model = modelBuilder.createBox(_dimensions.x, _dimensions.z, _dimensions.y, new Material(ColorAttribute.createDiffuse(_color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        _instance = new ModelInstance(_model);
    }
}