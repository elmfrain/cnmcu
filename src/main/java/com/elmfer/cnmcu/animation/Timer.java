package com.elmfer.cnmcu.animation;

import org.lwjgl.glfw.GLFW;

public class Timer {
    public final double time;
    private double startTime;
    private boolean hasExpired = false;
    
    public Timer(double time) {
        this.time = time;
        this.startTime = GLFW.glfwGetTime();
    }
    
    public Timer expire() {
        hasExpired = true;
        return this;
    }
    
    public boolean hasExpired() {
        if(hasExpired) return true;
        return (hasExpired = GLFW.glfwGetTime() - startTime >= time);
    }
    
    public double getProgress() {
        return (GLFW.glfwGetTime() - startTime) / time;
    }
    
    public void reset() {
        hasExpired = false;
        startTime = GLFW.glfwGetTime();
    }
}
