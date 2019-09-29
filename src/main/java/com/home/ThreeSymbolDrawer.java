package com.home;

public class ThreeSymbolDrawer implements Drawer{
    @Override
    public int getColor(float brightness) {
        return brightness > 0.75f ? 2 : brightness < 0.35f ? 0 : 1;
    }

    @Override
    public String colorToSymbol(int color) {
        return color == 2 ? "◻" : color == 0 ? "■" : "▩";
    }
}
