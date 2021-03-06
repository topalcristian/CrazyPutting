package com.golf.game.Others;

public class Velocity {
    private static Velocity instance;
    public float Vx;
    public float Vy;
    public float speed;
    public float angle;


    public Velocity() {
        setAngle(0);
        setSpeed(0);

    }

    public Velocity(float speed, float angle) {
        setAngle(angle);
        setSpeed(speed);
        updateVelocityComponents();
    }

    public static Velocity instance() {
        if (instance == null) {
            instance = new Velocity();
        }
        return instance;
    }

    public void setVelocity(Velocity velocity) {
        this.Vx = velocity.Vx;
        this.Vy = velocity.Vy;
        this.angle = velocity.angle;
        this.speed = (float) Math.sqrt(Math.pow(Vx, 2) + Math.pow(Vy, 2));
    }

    public void updateVelocityComponents() {
        this.Vx = (float) (speed * Math.cos(Math.toRadians(angle)));
        this.Vy = (float) (speed * Math.sin(Math.toRadians(angle)));

    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        updateVelocityComponents();
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getActualAngle() {
        return (float) Math.toDegrees(Math.acos(Vx / getActualSpeed()));
    }

    public float getActualSpeed() {
        return (float) Math.sqrt(Math.pow(Vx, 2) + Math.pow(Vy, 2));
    }


    public void add(Velocity velocity) {
        this.Vx += velocity.Vx;
        this.Vy += velocity.Vy;
    }


    public void sub(Velocity velocity) {
        this.Vx -= velocity.Vx;
        this.Vy -= velocity.Vy;
    }

    public String toString() {
        return "[" + this.Vx + ";" + this.Vy + "] angle :" + angle;
    }

    public Velocity clone() {
        Velocity output = new Velocity();
        output.Vx = this.Vx;
        output.Vy = this.Vy;
        output.angle = this.angle;
        output.speed = this.speed;
        return output;
    }
}
