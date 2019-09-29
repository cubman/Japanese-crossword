package com.home;

public class TwoSymbolDrawer implements Drawer{
    @Override
    public int getColor(float brightness) {
        return brightness > 0.68f ? 1 : 0;
    }

    @Override
    public String colorToSymbol(int color) {
        return color == 1 ? "◻" : "■";
    }
}
