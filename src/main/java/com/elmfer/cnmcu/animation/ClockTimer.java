package com.elmfer.cnmcu.animation;

import org.lwjgl.glfw.GLFW;

public class ClockTimer {
    public final double tps;
    private double nextTick;
    
    public ClockTimer(double tps) {
        this.tps = tps;
        this.nextTick = GLFW.glfwGetTime();
    }
    
    public int ticksPassed() {
        double currentTime = GLFW.glfwGetTime();
        int ticks = 0;

        while (currentTime > nextTick) {
            nextTick += 1.0 / tps;
            ticks++;
        }

        return ticks;
    }
    
    public double partialTicks() {
        double currentTime = GLFW.glfwGetTime();
        return (currentTime + 1.0 / tps - nextTick) * tps;
    }
    
    public double lerp(double start, double end) {
        return start + (end - start) * partialTicks();
    }
}
