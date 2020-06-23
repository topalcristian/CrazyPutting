package com.golf.game.Components.Colliders;

import com.badlogic.gdx.math.Vector3;

public class CollisionDetector {


    public static Contact detectCollision(ColliderComponent comp1, ColliderComponent comp2) {

        if (comp1 instanceof BoxCollide && comp2 instanceof SphereCollide) {
            BoxCollide box = (BoxCollide) comp1;
            SphereCollide sphere = (SphereCollide) comp2;
            return SphereToColl(sphere, box);
        } else {
            return null;
        }
    }


    public static Contact SphereToColl(SphereCollide sphereCollide, BoxCollide bBox) {


        Vector3 pPosition = sphereCollide.getPosition();

        Vector3 max = bBox.getPosition().cpy().add(bBox.getHalfSizes());
        Vector3 min = bBox.getPosition().cpy().sub(bBox.getHalfSizes());

        Vector3 closestPoint = new Vector3(0, 0, 0);

        float distance = pPosition.x;

        if (distance < min.x) {
            distance = min.x;
        }
        if (distance > max.x) {
            distance = max.x;
        }
        closestPoint.x = distance;

        distance = pPosition.y;
        if (distance < min.y) {
            distance = min.y;
        }
        if (distance > max.y) {
            distance = max.y;
        }
        closestPoint.y = distance;

        distance = pPosition.z;
        if (distance < min.z) {
            distance = min.z;
        }
        if (distance > max.z) {
            distance = max.z;
        }
        closestPoint.z = distance;

        distance = closestPoint.cpy().sub(pPosition).len2();

        if (distance <= sphereCollide.getRadius() * sphereCollide.getRadius()) {
            Contact contact = new Contact();

            contact.contactNormal = (pPosition.cpy().sub(closestPoint)).cpy().nor();
            contact.contactPoint = closestPoint;
            contact.penetration = (float) (sphereCollide.getRadius() - Math.sqrt(distance));
            contact.object1 = sphereCollide;
            contact.object2 = bBox;
            return contact;
        } else {
            return null;
        }
    }


}

