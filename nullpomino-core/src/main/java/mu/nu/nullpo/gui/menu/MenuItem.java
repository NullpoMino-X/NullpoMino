package mu.nu.nullpo.gui.menu;

abstract class MenuItem {

    String name;
    private String description;
    int color;
    int state;

    MenuItem(String name, String description) {
        this.name = name;
        this.description = description;

    }

    MenuItem(String name) {
        this(name, "");
    }

    /**
     * Changes the state of the MenuItem.
     *
     * @param change the amount to change the internal state.
     */
    public abstract void changeState(int change);

    /**
     * Gets the current state of the MenuItem.
     */
    public abstract int getState();
}
