package com.home;

import java.awt.*;
import java.util.List;

public interface Drawer {
    int getColor(float brightness);

    Color getColor(int color);

    String colorToSymbol(int color);

    List<List<RectangleValue>> filterMatrix(List<List<RectangleValue>> splittedByColorMatrix);
}
