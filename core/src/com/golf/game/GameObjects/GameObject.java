package com.golf.game.GameObjects;


import com.badlogic.gdx.math.Vector3;
import com.golf.game.Components.Colliders.ColliderComponent;
import com.golf.game.Components.Colliders.CollisionManager;
import com.golf.game.Components.Graphics.GraphicsComponent;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.GameLogic.GraphicsManager;

public class GameObject {
    public boolean enabled = true;
    protected Vector3 _position = new Vector3();
    private GraphicsComponent _graphicComponent;
    private ColliderComponent _colliderComponent;

    public GameObject(Vector3 pPosition) {
        _position = pPosition;
    }

    public GameObject() {
    }

    public void addGraphicComponent(GraphicsComponent pGC) {
        _graphicComponent = pGC;
        _graphicComponent.setOwner(this);
    }

    public GraphicsComponent getGraphicComponent() {
        return _graphicComponent;
    }

    public Vector3 getPosition() {
        return _position;
    }

    public void setPosition(Vector3 position) {
        this._position = new Vector3(position);
        if (_colliderComponent != null) {
            _colliderComponent.setPosition(new Vector3(position));
        }
    }

    public void updateHeight() {
        _position.z = CourseManager.calculateHeight(_position.x, _position.y);
    }

    @Override
    public String toString() {
        return _position.toString();
    }

    public void addColliderComponent(ColliderComponent pCollider) {

        _colliderComponent = pCollider;
        _colliderComponent.setOwner(this);
    }

    public void deleteColliderComponent() {
        if (_colliderComponent != null) {
            _colliderComponent = null;
        }
    }

    public void deleteGraphicsComponent() {
        _graphicComponent = null;
    }

    public ColliderComponent getColliderComponent() {
        return _colliderComponent;
    }

    public void destroy() {
        enabled = false;
        if (_colliderComponent != null)
            CollisionManager.deleteCollider(_colliderComponent);
        if (_graphicComponent != null)
            GraphicsManager.deleteGraphicsComponent(_graphicComponent);
    }


}
