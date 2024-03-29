package com.home;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TwoSymbolDrawer implements Drawer{
    private float mainBrightness;

    public TwoSymbolDrawer(Integer brightness) {
        this.mainBrightness = brightness / 100f;
    }
    @Override
    public int getColor(float brightness) {
        return Math.abs(mainBrightness - brightness) < 0.2f ? 1 : 0;
    }

    @Override
    public Color getColor(int color) {
        return color > 0 ? Color.WHITE : Color.GRAY;
    }

    @Override
    public String colorToSymbol(int color) {
        return color == 1 ? "◻" : "■";
    }

    @Override
    public List<List<RectangleValue>> filterMatrix(List<List<RectangleValue>> splittedByColorMatrix) {
        List<List<RectangleValue>> resMatrix = new ArrayList<>();

        splittedByColorMatrix.forEach(rectangleValues -> {
            ArrayList<RectangleValue> values = new ArrayList<>();
            resMatrix.add(values);

            rectangleValues.forEach(rectangleValue -> {
                if (rectangleValue.getColor() == 0) {
                    values.add(rectangleValue);
                }
            });
        });

        return resMatrix;
    }
}
