package core.world.map;

import java.util.Random;

public final class SampleGenerator {

    public final double[] values;
    private final int width;
    private final int height;

    public SampleGenerator(int width, int height, int stepSize, long seed) {
        Random random = new Random(seed);
        this.width = width;
        this.height = height;
        values = new double[width * height];

        for (int y = 0; y < height; y += stepSize) {
            for (int x = 0; x < width; x += stepSize) {
                setSample(x, y, random.nextFloat() * 2 - 1);
            }
        }

        double scale = 1.0 / width;
        double scaleMod = 1;
        int s = stepSize;

        while (s > 1) {
            int half = s / 2;

            for (int y = 0; y < height; y += s) {
                for (int x = 0; x < width; x += s) {
                    double a = getSample(x, y);
                    double b = getSample(x + s, y);
                    double c = getSample(x, y + s);
                    double d = getSample(x + s, y + s);
                    double e = (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * s * scale;
                    setSample(x + half, y + half, e);
                }
            }

            for (int y = 0; y < height; y += s) {
                for (int x = 0; x < width; x += s) {
                    double a = getSample(x, y);
                    double b = getSample(x + s, y);
                    double c = getSample(x, y + s);
                    double d = getSample(x + half, y + half);
                    double e = getSample(x + half, y - half);
                    double f = getSample(x - half, y + half);

                    double hVal = (a + b + d + e) / 4.0 + (random.nextFloat() * 2 - 1) * s * scale * 0.5;
                    double gVal = (a + c + d + f) / 4.0 + (random.nextFloat() * 2 - 1) * s * scale * 0.5;
                    setSample(x + half, y, hVal);
                    setSample(x, y + half, gVal);
                }
            }

            s /= 2;
            scale *= (scaleMod + 0.8);
            scaleMod *= 0.3;
        }
    }

    private double getSample(int x, int y) {
        return values[(x & (width - 1)) + (y & (height - 1)) * width];
    }

    private void setSample(int x, int y, double value) {
        values[(x & (width - 1)) + (y & (height - 1)) * width] = value;
    }
}
