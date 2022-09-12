package com.tkoyat.bubblecloudlauncher;

public class ComputeZ {
    float Z;
    double d;
    int dx;
    int dy;
    double r;
    double r1;

    public ComputeZ(float f, float f2, int i, int i2, int i3) {
        double sqrt = Math.sqrt(Math.pow(i, 2.0d) + Math.pow(i2, 2.0d));
        this.r = sqrt;
        this.r1 = sqrt * 0.25d;
        float f3 = f - i;
        float f4 = f2 - i2;
        double sqrt2 = Math.sqrt(Math.pow(f3, 2.0d) + Math.pow(f4, 2.0d));
        this.d = sqrt2;
        if (sqrt2 >= this.r1) {
            this.Z = (float) (1.0d - (sqrt2 / this.r));
        } else {
            this.Z = 0.75f;
        }
        float f5 = i3 * (-0.8f);
        double d = this.r;
        this.dx = (int) ((f3 * f5) / d);
        this.dy = (int) ((f5 * f4) / d);
    }
}