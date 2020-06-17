package com.crazy_putting.game.Others;

import java.util.Random;

public class Noise {

    private static Noise instance;
    private static Random random = new Random();


    private Noise() {

    }

    public static Noise getInstance() {
        if (instance == null) {
            instance = new Noise();
        }
        return instance;
    }

    public float nextNormal(float mean, float stDev) {
        if (stDev < 0) {
            System.out.println("Standard deviation should be non-negative");
            return Float.parseFloat(null);
        }
        float result = (float) random.nextGaussian();
        return result * stDev + mean;
    }


    public float nextFloat() {
        return random.nextFloat();
    }


}
