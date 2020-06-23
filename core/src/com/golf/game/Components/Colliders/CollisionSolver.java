package com.golf.game.Components.Colliders;

import com.badlogic.gdx.math.Vector3;

public final class CollisionSolver {
    private static final float RESTITUTION = 0.9f;


    public static void dealCollision(Contact contact) {
        if (contact != null) {
            resolveVelocity(contact);
            resolvePenetration(contact);
        }
    }


    private static void resolveVelocity(Contact contact) {
        float separatingVelocity = calculateSeparatingVelocity(contact);

        if (separatingVelocity > 0) {
            return;
        }


        float realSeparatingVelocity = -RESTITUTION * separatingVelocity;

        float deltaVelocity = realSeparatingVelocity - separatingVelocity;

        float totalInverseMass = contact.object1.getInverseMass() + contact.object2.getInverseMass();

        float impulse = deltaVelocity / totalInverseMass;

        Vector3 impulsePerMass = contact.contactNormal.cpy().scl(impulse);

        contact.object1.getVelocity().Vx += impulsePerMass.x * contact.object1.getInverseMass();
        contact.object1.getVelocity().Vy += impulsePerMass.y * contact.object1.getInverseMass();

        contact.object2.getVelocity().Vx += impulsePerMass.x * (-1 * contact.object2.getInverseMass());
        contact.object2.getVelocity().Vy += impulsePerMass.y * (-1 * contact.object2.getInverseMass());


    }

    private static float calculateSeparatingVelocity(Contact contact) {
        Vector3 relativeVelocity = new Vector3(contact.object1.getVelocity().Vx, contact.object1.getVelocity().Vy, 0);
        Vector3 secondVelocity = new Vector3(contact.object2.getVelocity().Vx, contact.object2.getVelocity().Vy, 0);

        Vector3 intermediateResult = relativeVelocity.cpy().sub(secondVelocity);

        return intermediateResult.cpy().dot(contact.contactNormal);


    }

    private static void resolvePenetration(Contact contact) {

        float totalInverseMass = contact.object1.getInverseMass() + contact.object2.getInverseMass();
        Vector3 movePerInverseMass = contact.contactNormal.cpy().scl(contact.penetration / totalInverseMass);

        Vector3 changeInPosition1 = movePerInverseMass.cpy().scl(contact.object1.getInverseMass());
        Vector3 changeInPosition2 = movePerInverseMass.cpy().scl(contact.object2.getInverseMass());

        if (contact.object1 instanceof SphereCollide) {
            contact.object1.getOwner().setPosition(contact.object1.getPosition().cpy().add(changeInPosition1));
        }
        if (contact.object2 instanceof SphereCollide) {
            contact.object2.getOwner().setPosition(contact.object2.getPosition().cpy().sub(changeInPosition2));
        }
    }
}
