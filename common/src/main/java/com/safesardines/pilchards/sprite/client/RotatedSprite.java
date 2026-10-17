package com.safesardines.pilchards.sprite.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record RotatedSprite(ResourceLocation id, int frameSize, int steps) {

    public int frameCount() {
        return this.steps;
    }

    public int frameIndex(double degrees) {
        int index = (int) Math.floor(degrees / (360.0 / this.steps));
        return Math.floorMod(index, this.steps);
    }

    public void blit(GuiGraphics gui, int x, int y, int frame) {
        blit(gui, x, y, frame, this.frameSize, this.frameSize);
    }

    public void blit(GuiGraphics gui, int x, int y, int frame, int width, int height) {
        int index = Math.floorMod(frame, this.steps);
        gui.blit(this.id, x, y, 0, index * this.frameSize, width, height, this.frameSize, this.frameSize * this.steps);
    }
}
