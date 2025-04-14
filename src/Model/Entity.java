package Model;


import javax.swing.*;

public abstract class Entity {
    private int currentX;
    private int currentY;
    private int visualX;
    private int visualY;

    public Entity() {
    }

    public int getCurrentX() {
        return currentX;
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

    //    public void animateMovement(int targetX, int targetY) {
//        // Implement animation logic here
//        // you can use a timer to update the position gradually
//        Timer timer = new Timer(1000/60, e -> {
//            if (Math.abs(visualX - targetX*32) > 1 || Math.abs(visualY - targetY*32) > 1) {
//                if (visualX < targetX *32) {
//                    visualX += (int) (32.0/16.66);
//                } else if (visualX > targetX*32) {
//                    visualX -= (int) (32.0/16.66);
//                }
//                if (visualY < targetY*32) {
//                    visualY += (int) (32.0/16.66);
//                } else if (visualY > targetY*32) {
//                    visualY -= (int) (32.0/(16.66));
//                }
//            } else {
//                ((Timer) e.getSource()).stop();
//            }
//        });
//        timer.start();
//    }
    private Timer currentAnimation;
    private final int TILE_SIZE = 32;
    private final double ANIMATION_SPEED = TILE_SIZE / (1000.0/60.0);
    public void animateMovement(int targetX, int targetY) {
        // Stop any existing animation
        if (currentAnimation != null && currentAnimation.isRunning()) {
            currentAnimation.stop();
        }

        // Create new animation timer
        currentAnimation = new Timer(1000/60, e -> {
            // Calculate distance to target
            double dx = targetX*32 - visualX;
            double dy = targetY*32 - visualY;
            double distance = Math.sqrt(dx*dx + dy*dy);

            if (distance > 0.1) {
                // Normalize direction and apply speed
                double moveX = Math.min(ANIMATION_SPEED, Math.abs(dx)) * Math.signum(dx);
                double moveY = Math.min(ANIMATION_SPEED, Math.abs(dy)) * Math.signum(dy);

                visualX += moveX;
                visualY += moveY;
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
