package com.golf.game.Physics;

import com.badlogic.gdx.math.Vector3;
import com.golf.game.GameObjects.PhysicsGameObject;


public class Euler extends Physics {

    public Euler() {
        Physics.physics = this;
    }

    public void updateComponents(PhysicsGameObject obj) {

        state.update(obj);

        // x(t+h) = x(t) + h*Vx(t) + h^2/2 * Ax;
        // y(t+h) = y(t) + h*Vy(t) + h^2/2 * Ay;
        float newX = state.getX() + (dt * state.getVx());
        float newY = state.getY() + (dt * state.getVy());

        //v(t+h) = v(t) + h*a
        Vector3 a = acceleration(state);

        obj.getPreviousPosition().x = state.getX();
        obj.getPreviousPosition().y = state.getY();

        float newVelX = state.getVx() + dt * a.x;
        float newVelY = state.getVy() + dt * a.y;

        obj.setPositionXYZ(newX, newY);
        obj.setVelocityComponents(newVelX, newVelY);
    }


}
