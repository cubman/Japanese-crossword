package com.home.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PaintPanel extends JPanel {

    private BufferedImage image;
    private boolean isScaled;

    public PaintPanel(boolean isScaled) {
        this.isScaled = isScaled;
    }

    public void updateImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isScaled && image != null) {
            g.drawImage(image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST), 0, 0, Color.BLACK, null);
        } else {
            g.drawImage(image, 0, 0, Color.BLACK, null);
        }
    }
}
