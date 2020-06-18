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

public class ArrowGraphics3DComponent extends Graphics3DComponent {
    private Vector3 _from;
    private Vector3 _to;
    private int sizeScale = 200;

    public ArrowGraphics3DComponent(Vector3 from, Vector3 to, Color pColor) {
        _color = pColor;
        _from = from;
        _to = to;
        initArrow();
    }

    public ArrowGraphics3DComponent(Vector3 from, Vector3 to, Color pColor, int sizeScale) {
        _color = pColor;
        _from = from;
        _to = to;
        initArrow();
    }

    private void initArrow() {
        GraphicsManager.addGraphics3DComponent(this);
        ModelBuilder modelBuilder = new ModelBuilder();
        swapYZ(_from);
        Vector3 v = new Vector3();
        System.out.println(v.x);
        _to.sub(_from);
        //_to.nor().scl(sizeScale);
        _model = modelBuilder.createArrow(_to.x, _to.y, _to.z, 0, 0, 0, 0.1f, 0.3f, 10, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(_color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        _instance = new ModelInstance(_model);
    }

    private void swapYZ(Vector3 v) {
        Vector3 cache = new Vector3(v);
        v.y = cache.z;
        v.z = cache.y;
    }

}
