package Model;

public class Road extends Land {
    private int price = 10;
    private boolean entrance = false;
    private boolean exit = false;
    private String imageKey = "road1"; // Default to straight road

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setEntrance(boolean entrance) {
        this.entrance = entrance;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public boolean isEntrance() {
        return entrance;
    }

    public int getPrice() {
        return price;
    }

    public boolean isExit() {
        return exit;
    }
    
    public int getCurrentX() {
        return getX();
    }
    
    public int getCurrentY() {
        return getY();
    }

    public Road(int x, int y, boolean entrance, boolean exit) {
        super(x,y);
        this.entrance = entrance;
        this.exit = exit;
    }
}