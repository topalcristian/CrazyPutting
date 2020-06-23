package com.golf.game.Components.Colliders;

import com.badlogic.gdx.math.Vector3;
import com.golf.game.Components.AComponent;
import com.golf.game.GameObjects.PhysicsGameObject;
import com.golf.game.Others.Velocity;

public abstract class ColliderComponent extends AComponent {

    //
    protected Vector3 position;
    protected boolean isStatic;
    protected Vector3 dimensions;


    public ColliderComponent() {
        CollisionManager.addCollider(this);
    }

    public void synchronize() {
        if (owner == null) {
            System.out.println("owner is null");
        }
        if (position == null) {
            System.out.println("position is null");
        }
        position = owner.getPosition();


    }

    public boolean isEnabled() {
        if (owner == null) return true;
        else return owner.enabled;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    protected boolean isStatic() {
        return this.isStatic;
    }

    public void setStatic(boolean tf) {
        this.isStatic = tf;
    }

    protected Velocity getVelocity() {
        if (owner instanceof PhysicsGameObject) {
            PhysicsGameObject obj = (PhysicsGameObject) owner;
            return obj.getVelocity();
        } else {
            return Velocity.instance();
        }
    }


    protected float getInverseMass() {
        if (owner instanceof PhysicsGameObject) {
            PhysicsGameObject obj = (PhysicsGameObject) owner;
            return obj.getInverseMass();
        }
        return 1.0f / 500;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }

    public abstract boolean containsPoint(Vector3 pPoint);

}
