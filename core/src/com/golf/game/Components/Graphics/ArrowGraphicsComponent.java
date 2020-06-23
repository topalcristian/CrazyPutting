package com.golf.game.Components.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.GameLogic.GraphicsManager;

public class ArrowGraphicsComponent extends GraphicsComponent {
    private Vector3 _from;
    private Vector3 _to;
    private int sizeScale = 200;

    public ArrowGraphicsComponent(Vector3 from, Vector3 to, Color pColor) {
        color = pColor;
        _from = from;
        _to = to;
        initArrow();
    }

    private void initArrow() {
        GraphicsManager.addGraphicsComponent(this);
        ModelBuilder modelBuilder = new ModelBuilder();
        swapYZ(_from);
        Vector3 v = new Vector3();
        System.out.println(v.x);
        _to.sub(_from);
        //
        model = modelBuilder.createArrow(_to.x, _to.y, _to.z, 0, 0, 0, 0.1f, 0.3f, 10, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
    }

    private void swapYZ(Vector3 v) {
        Vector3 cache = new Vector3(v);
        v.y = cache.z;
        v.z = cache.y;
    }

}
