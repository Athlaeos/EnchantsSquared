package me.athlaeos.enchantssquared.managers;

import java.util.Random;

public class RandomNumberGenerator {
    private static final Random random = new Random();

    public static Random getRandom(){
        return random;
    }
}
