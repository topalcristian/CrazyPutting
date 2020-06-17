
package com.crazy_putting.game.Physics;

import com.badlogic.gdx.math.Vector3;
import com.crazy_putting.game.GameObjects.PhysicsGameObject;


public class Verlet extends Physics {

    public Verlet() {
        Physics.physics = this;
    }

    public void updateComponents(PhysicsGameObject obj) {

        state.update(obj);

        Vector3 a = acceleration(state);

        obj.getPreviousPosition().x = state.getX();
        obj.getPreviousPosition().y = state.getY();


        float newX = state.getX() + (dt * state.getVx()) + (dt * a.x * dt / 2);
        float newY = state.getY() + (dt * state.getVy()) + (dt * a.y * dt / 2);

        float newVelX = state.getVx() + dt * a.x;
        float newVelY = state.getVy() + dt * a.y;

        obj.setPositionXYZ(newX, newY);

        obj.setVelocityComponents(newVelX, newVelY);

    }


}
