package com.home;

public class SixSymbolDrawer implements Drawer {
    @Override
    public int getColor(float brightness) {
//        return brightness > 0.8f ? 2 : brightness < 0.2f ? 0 : 1;
        if (brightness > 0.8f) {
            return 0;
        } else if (brightness > 0.64f) {
            return 1;
        } else if (brightness > 0.48f) {
            return 2;
        } else if (brightness > 0.32f) {
            return 3;
        }else if (brightness > 0.16f) {
            return 4;
        } else {
            return 5;
        }
    }

    @Override
    public String colorToSymbol(int color) {
        switch (color) {
            case 0: return "◻";
            case 1: return "▢";
            case 2: return "▣";
            case 3: return "▧";
            case 4: return "▦";
            case 5: return "■";
            default:
                throw new RuntimeException();
        }
    }
}
