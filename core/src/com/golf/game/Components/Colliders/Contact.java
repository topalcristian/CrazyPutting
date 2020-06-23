package com.golf.game.Components.Colliders;

import com.badlogic.gdx.math.Vector3;

public class Contact {

    public Vector3 contactPoint;
    public Vector3 contactNormal;
    public float penetration;

    public ColliderComponent object1;
    public ColliderComponent object2;


    public Contact(Vector3 point, Vector3 normal, float penetration, ColliderComponent obj1, ColliderComponent obj2) {
        this.contactNormal = normal;
        this.contactPoint = point;
        this.penetration = penetration;
        this.object1 = obj1;
        this.object2 = obj2;
    }

    public Contact() {

    }

    public String toString() {
        return "ContactPoint: " + contactPoint + "\nContactNormal: " + contactNormal + "\nPenetration: " + penetration;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Contact) {
            Contact cont = (Contact) o;

            if (this.object1.equals(cont.object1) && this.object2.equals(cont.object2)) {
                return true;
            }
            return this.object1.equals(cont.object2) && this.object2.equals(cont.object1);
        }
        return false;
    }

}
