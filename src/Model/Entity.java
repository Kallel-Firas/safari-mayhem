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

    private Timer currentAnimation;
    private final int TILE_SIZE = 32;
    private final double ANIMATION_SPEED = TILE_SIZE / (1000.0/32.0);
    public void animateMovement(int targetX, int targetY) {
        // Stop any existing animation
        if (currentAnimation != null && currentAnimation.isRunning()) {
            currentAnimation.stop();
        }

        // Create new animation timer
        currentAnimation = new Timer(1000/32, e -> {
            // Calculate distance to target
            double dx = targetX*32 - visualX;
            double dy = targetY*32 - visualY;
            double distance = Math.sqrt(dx*dx + dy*dy);

            if (distance > 0.1) {
                // Normalize direction and apply speed
                double moveX = Math.min(ANIMATION_SPEED, Math.abs(dx)) * Math.signum(dx);
                double moveY = Math.min(ANIMATION_SPEED, Math.abs(dy)) * Math.signum(dy);

                visualX += (int) moveX;
                visualY += (int) moveY;
            } else {
                // Snap to exact position and stop animation
                visualX = targetX*32;
                visualY = targetY*32;
                ((Timer) e.getSource()).stop();
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
