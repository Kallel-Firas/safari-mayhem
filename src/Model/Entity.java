package Model;


import javax.swing.*;
import java.io.Serializable;

public abstract class Entity implements Serializable {
    protected int currentX;
    protected int currentY;
    protected int visualX;
    protected int visualY;

    public Entity() {
    }

    public int getCurrentX() {
        return currentX;
    }

    public void MoveX(int x) {
        this.currentX = x;
    }
    public void MoveY(int y) {
        this.currentY = y;
    }

    public void setCurrentX(int x) {
        this.currentX = x;
        this.visualX = x*32;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int y) {
        this.currentY = y;
        this.visualY = y*32;
    }

    public void setAnimationDuration(int duration) {
        this.ANIMATION_DURATION_MS = duration;
    }

    private Timer currentAnimation;
    private final int TILE_SIZE = 32;
    private int ANIMATION_DURATION_MS = 1000; // Fixed animation duration

    public void animateMovement(int targetX, int targetY) {
        // Stop any existing animation
        if (currentAnimation != null && currentAnimation.isRunning()) {
            currentAnimation.stop();
        }

        // Store starting position and calculate total distance
        final double startX = visualX;
        final double startY = visualY;
        final double endX = targetX * TILE_SIZE;
        final double endY = targetY * TILE_SIZE;

        final long startTime = System.currentTimeMillis();

        // Create new animation timer - always use 60 FPS for smooth animation
        currentAnimation = new Timer(1000/60, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = Math.min(1.0, (double) elapsed / ANIMATION_DURATION_MS);

            if (progress >= 1.0) {
                // Animation complete - snap to exact position
                visualX = (int) endX;
                visualY = (int) endY;
                ((Timer) e.getSource()).stop();
            } else {
                visualX = (int) (startX + (endX - startX) * progress);
                visualY = (int) (startY + (endY - startY) * progress);
            }
        });

        currentAnimation.start();
    }



    public int getVisualX() {
        return visualX;
    }
    public int getVisualY() {
        return visualY;
    }
}
