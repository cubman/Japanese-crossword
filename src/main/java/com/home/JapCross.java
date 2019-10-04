package com.home;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JapCross {

    private BufferedImage getImage(URL url) throws IOException {
        return ImageIO.read(url);
    }

    private int getColor(BufferedImage image, Drawer drawer) {
        int allPixels = image.getWidth() * image.getHeight();

        int[] imageRGB = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        AtomicReference<Float> br = new AtomicReference<>(0F);
        AtomicReference<Float> s = new AtomicReference<>(0F);

        Arrays.stream(imageRGB).forEach(value -> {
            float[] floats = Color.RGBtoHSB((value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF, null);

            br.updateAndGet(v -> v + floats[2]);
            s.updateAndGet(v -> v + floats[1]);
        });

        return drawer.getColor(br.get() / allPixels);
    }

    private void drawMatrix(int width, int height, List<List<Integer>> matrix) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(600, 820, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();


        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        int a = 5;
        for (int i = 0; i < width; ++i) {

            for (int j = 0; j < height; ++j) {
                graphics.setColor(matrix.get(i).get(j) > 0 ? Color.WHITE : Color.RED);

                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//                graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING , RenderingHints.VALUE_COLOR_RENDER_SPEED);
                graphics.setRenderingHint(RenderingHints.KEY_DITHERING  , RenderingHints.VALUE_DITHER_ENABLE);
//                graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION  , RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);


                graphics.fillRect(i * a, j * a, a, a);

                graphics.setColor(Color.BLACK);
                graphics.drawRect(i * a, j * a, a, a);
            }
        }

        graphics.dispose();

        ImageIO.write(bufferedImage, "jpg", new File("matrix.jpg"));
    }

    public static void main(String[] args) throws IOException {
        JapCross japCross = new JapCross();
        BufferedImage image = japCross.getImage(JapCross.class.getResource("/flover.jpg"));

        BufferedImage targetImage = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
//        AffineTransform transform = new AffineTransform();
//        transform.setToScale(1, -1);
//        transform.translate(-(image.getWidth() - image.getHeight()) / 2, -(image.getWidth() + image.getHeight()) / 2);
//        transform.rotate(Math.toRadians(270), image.getWidth() / 2, image.getHeight() / 2);
//        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
//        op.filter(image, targetImage);
//        image = targetImage;

        int height = 100;
        int width = 90;

        float squareWidth = image.getWidth() / (float) width;
        float squareHeight = image.getHeight() / (float) height;

        Drawer drawer = new TwoSymbolDrawer();

        List<List<Integer>> matrix = new ArrayList<>();
        List<List<String>> matrixDraw = new ArrayList<>();

        for (int i = 0; i < width; ++i) {
            matrix.add(new ArrayList<>());
            matrixDraw.add(new ArrayList<>());

            for (int j = 0; j < height; ++j) {
                float x = i * squareWidth;
                float y = j * squareHeight;
                if (x >= image.getWidth() || y >= image.getHeight()) {
                    break;
                }

                float subWidth = Math.min(squareWidth, image.getWidth() - x);
                float subHeight = Math.min(squareHeight, image.getHeight() - y);
                BufferedImage subImage = image.getSubimage((int) x, (int) y, (int) subWidth, (int) subHeight);

                int color = japCross.getColor(subImage, drawer);

                matrix.get(i).add(color);
                matrixDraw.get(i).add(drawer.colorToSymbol(color));
            }
        }

        matrixDraw.forEach(strings -> {
            strings.forEach(System.out::print);
            System.out.println();
        });

        japCross.drawMatrix(width, height, matrix);
    }
}
