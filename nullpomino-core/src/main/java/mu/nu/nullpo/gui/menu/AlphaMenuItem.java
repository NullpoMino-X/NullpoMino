package mu.nu.nullpo.gui.menu;

import java.util.Arrays;
import java.util.Vector;

class AlphaMenuItem extends NumericMenuItem {
    @SuppressWarnings("unused")
    private int index;
    @SuppressWarnings("unused")
    private Vector<String> choiceList;

    private AlphaMenuItem(String name, int color, Vector<String> choiceList) {
        super(name, color, 0, 0, choiceList.size(), -1, ARITHSTYLE_MODULAR);
        this.choiceList = choiceList;
        state = 0;
    }

    public AlphaMenuItem(String name, int color, String[] choiceList) {
        this(name, color, new Vector<>(Arrays.asList(choiceList)));
    }
}
