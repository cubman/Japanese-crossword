package com.home;

import java.awt.*;
import java.util.List;

public class ThreeSymbolDrawer implements Drawer{
    @Override
    public int getColor(float brightness) {
        return brightness > 0.75f ? 2 : brightness < 0.35f ? 0 : 1;
    }

    @Override
    public Color getColor(int color) {
        return color == 2 ? Color.BLUE : color == 0 ? Color.GRAY : Color.GREEN;
    }

    @Override
    public String colorToSymbol(int color) {
        return color == 2 ? "◻" : color == 0 ? "■" : "▩";
    }

    @Override
    public List<List<RectangleValue>> filterMatrix(List<List<RectangleValue>> splittedByColorMatrix) {
        return splittedByColorMatrix;
    }
}
