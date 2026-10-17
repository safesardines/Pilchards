package com.safesardines.pilchards.sprite;

public final class RotSprite {
    public static final int STEPS = 36;
    public static final int UPSCALE_PASSES = 3;
    public static final int SCALE = 1 << UPSCALE_PASSES;
    public static final PixelScaler SCALER = RotSprite::scale2x;

    private RotSprite() {}

    public static int fitSize(int slot, double maxDegrees) {
        double peak = 0.0;
        for (double d = 0.0; d <= Math.min(45.0, Math.abs(maxDegrees)); d += 1.0) {
            double rad = Math.toRadians(d);
            peak = Math.max(peak, Math.cos(rad) + Math.sin(rad));
        }
        return Math.max(1, Math.min(slot, (int) (slot / peak)));
    }

    public static int[] rotate(int[] source, int size, double degrees) {
        return rotate(source, size, degrees, SCALER, UPSCALE_PASSES);
    }

    public static int[] rotate(int[] source, int size, double degrees, PixelScaler scaler, int upscalePasses) {
        long snapped = Math.round((((degrees % 360.0) + 360.0) % 360.0));
        if (snapped % 90 == 0) {
            return switch ((int) (snapped / 90)) {
                case 1 -> rot90(source, size);
                case 2 -> rot180(source, size);
                case 3 -> rot270(source, size);
                default -> source.clone();
            };
        }
        int[] up = source;
        int big = size;
        for (int i = 0; i < upscalePasses; i++) {
            up = scaler.scale(up, big);
            big *= 2;
        }
        return downscale(up, big, size, Math.toRadians(snapped), 1 << upscalePasses);
    }

    public static int[] strip(int[] source, int size, int steps, double stepDegrees) {
        return strip(source, size, steps, stepDegrees, SCALER, UPSCALE_PASSES);
    }

    public static int[] strip(int[] source, int size, int steps, double stepDegrees, PixelScaler scaler, int upscalePasses) {
        int[] frames = new int[size * size * steps];
        for (int f = 0; f < steps; f++) {
            int[] frame = rotate(source, size, f * stepDegrees, scaler, upscalePasses);
            System.arraycopy(frame, 0, frames, f * size * size, frame.length);
        }
        return frames;
    }

    public static int[] frame(int[] strip, int size, int index) {
        int[] frame = new int[size * size];
        System.arraycopy(strip, index * size * size, frame, 0, frame.length);
        return frame;
    }

    public static int[] rot90(int[] source, int size) {
        int[] dst = new int[source.length];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                dst[y * size + x] = source[(size - 1 - x) * size + y];
            }
        }
        return dst;
    }

    public static int[] rot180(int[] source, int size) {
        int[] dst = new int[source.length];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                dst[y * size + x] = source[(size - 1 - y) * size + (size - 1 - x)];
            }
        }
        return dst;
    }

    public static int[] rot270(int[] source, int size) {
        int[] dst = new int[source.length];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                dst[y * size + x] = source[x * size + (size - 1 - y)];
            }
        }
        return dst;
    }

    public static int[] scale2x(int[] source, int size) {
        int doubled = size * 2;
        int[] dst = new int[doubled * doubled];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int p = at(source, size, x, y);
                int n = at(source, size, x, y - 1);
                int w = at(source, size, x - 1, y);
                int s = at(source, size, x, y + 1);
                int e = at(source, size, x + 1, y);
                int o = y * 2 * doubled + x * 2;
                dst[o] = (w == n && w != s && n != e) ? n : p;
                dst[o + 1] = (n == e && n != w && e != s) ? e : p;
                dst[o + doubled] = (w == s && w != n && s != e) ? w : p;
                dst[o + doubled + 1] = (e == s && e != n && s != w) ? s : p;
            }
        }
        return dst;
    }

    public static int at(int[] source, int size, int x, int y) {
        x = Math.min(size - 1, Math.max(0, x));
        y = Math.min(size - 1, Math.max(0, y));
        return source[y * size + x];
    }

    public static int[] downscale(int[] up, int big, int size, double radians, int factor) {
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        double centerUp = (big - 1) / 2.0;
        double centerOut = (size - 1) / 2.0;
        int[] dst = new int[size * size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - centerOut;
                double dy = y - centerOut;
                int ux = (int) Math.floor((dx * cos + dy * sin) * factor + centerUp);
                int uy = (int) Math.floor((-dx * sin + dy * cos) * factor + centerUp);
                dst[y * size + x] = (ux < 0 || uy < 0 || ux >= big || uy >= big)
                        ? 0 : up[uy * big + ux];
            }
        }
        return dst;
    }
}
