package com.golf.game.Components.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.Components.AComponent;
import com.golf.game.GameLogic.GraphicsManager;
import com.golf.game.GameObjects.Ball;

public abstract class GraphicsComponent extends AComponent {
    private static Quaternion emptyQuaternion = new Quaternion();
    protected Model model;
    protected ModelInstance instance;
    protected Color color;

    public GraphicsComponent() {
        GraphicsManager.addGraphicsComponent(this);
    }

    public void setColor(Color CustomColor) {
        color = CustomColor;
        if (instance != null)
            instance.materials.get(0).set(ColorAttribute.createDiffuse(color));
    }

    public ModelInstance getInstance() {
        return instance;
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        if (!owner.enabled) return;
        Vector3 pos2d = owner.getPosition();
        Vector3 pos = new Vector3(pos2d.x, pos2d.z, pos2d.y);
        if (owner instanceof Ball)//here change color for owner ball
            pos.y += 20f;
        instance.transform.set(pos, emptyQuaternion);
        modelBatch.render(instance, environment);
    }

    public void dispose() {
        model = null;
        instance = null;
    }
}
