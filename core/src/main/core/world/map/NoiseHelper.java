package core.world.map;

import java.util.Random;

public class NoiseHelper {

    private NoiseHelper() {}

    public static float noise2D(long seed, float x, float y) {
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        float tx = x - x0;
        float ty = y - y0;
        float sx = smooth(tx);
        float sy = smooth(ty);

        float n00 = hash(seed, x0, y0);
        float n10 = hash(seed, x1, y0);
        float n01 = hash(seed, x0, y1);
        float n11 = hash(seed, x1, y1);

        float ix0 = lerp(n00, n10, sx);
        float ix1 = lerp(n01, n11, sx);
        return lerp(ix0, ix1, sy);
    }

    public static float fractalNoise2D(long seed, float x, float y, int octave, float persistence) {
        float total = 0.0f;
        float amplitude = 1f;
        float max = 0f;
        float frequency = 1f;

        for (int i = 0; i <= octave; i++) {
            total += noise2D(seed + i * 1315423911L, x * frequency, y * frequency) * amplitude;
            max += amplitude;
            amplitude *= persistence;
            frequency *= 2f;
        }
        return total/max;

    }

    private static float hash(long seed, int x, int y) {
        long value = seed;

        value ^= 0x9E3779B97F4A7C15L * x;
        value ^= 0xC2B2AE3D27D4EB4FL * y;

        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        value ^= value >>> 31;

        return ((value >>> 40) & 0xFFFFFF) / 16777216f;
    }

    private static float smooth(float t) {
        return t * t * (3f - 2f * t);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
