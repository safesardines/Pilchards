package com.safesardines.pilchards.sprite;

@FunctionalInterface
public interface PixelScaler {
    int[] scale(int[] source, int size);
}
