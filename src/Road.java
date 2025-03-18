public class Road extends Land {
    private int price = 250;
    private boolean entrance = false;
    private boolean exit = false;



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

    public Road(boolean entrance, boolean exit) {
        this.entrance = entrance;
        this.exit = exit;
    }


}