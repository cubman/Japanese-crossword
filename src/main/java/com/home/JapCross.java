package com.home;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

    private BufferedImage drawMatrix(Drawer drawer,
                                     List<List<Integer>> matrix,
                                     List<List<RectangleValue>> horizontal,
                                     List<List<RectangleValue>> vertical) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1700, 1700, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//                graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING , RenderingHints.VALUE_COLOR_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 18));

        int a = 28;
        int moveX = 0;
        int moveY = 0;

        graphics.setColor(Color.BLACK);
        for (int i = 0; i < horizontal.size(); ++i) {
            for (int j = 0; j < horizontal.get(i).size(); ++j) {
                graphics.drawRect(moveX + i * a, moveY + j * a, a, a);

                RectangleValue value = horizontal.get(i).get(j);
                int moveXAdd = value.getValue() > 9 ? 1 : 5;
                graphics.drawString(value.getColor() == -1 ? "" : String.valueOf(value.getValue()), moveX + i * a + moveXAdd, moveY + (j + 1) * a - 4);
            }
        }

        moveX = 0;
        moveY = a * horizontal.get(0).size();

        for (int i = 0; i < vertical.size(); ++i) {
            for (int j = 0; j < vertical.get(i).size(); ++j) {
                graphics.drawRect(moveX + i * a, moveY + j * a, a, a);

                RectangleValue value = vertical.get(i).get(j);
                int moveXAdd = value.getValue() > 9 ? 1 : 5;
                graphics.drawString(value.getColor() == -1 ? "" : String.valueOf(value.getValue()), moveX + i * a + moveXAdd, moveY + (j + 1) * a - 4);
            }
        }

        moveX = a * vertical.size();
        moveY = a * horizontal.get(0).size();



        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(i).size(); ++j) {
                graphics.setColor(drawer.getColor(matrix.get(i).get(j)));
//                graphics.setColor(Color.WHITE);

                graphics.fillRect(moveX + i * a, moveY + j * a, a, a);

                graphics.setColor(Color.BLACK);
                graphics.drawRect(moveX + i * a, moveY + j * a, a, a);
            }
        }

        int lineWidth = 2;
        drawLine(graphics, new Line2D.Float(0, moveY, moveX + matrix.size() * a - lineWidth, moveY), lineWidth);
        drawLine(graphics, new Line2D.Float(moveX, 0, moveX, moveY + matrix.get(0).size() * a - lineWidth), lineWidth);

        int split = 5;
        for (int i = 0; i < matrix.size()  / split; ++i) {
            for (int j = 0; j < matrix.get(i).size() / split; ++j) {
                drawLine(graphics, new Line2D.Float(0, moveY + j * split * a, moveX + matrix.size() * a - lineWidth, moveY + j * split * a), lineWidth);
                drawLine(graphics, new Line2D.Float(moveX + i * split * a, 0, moveX + i * split * a, moveY + matrix.get(i).size() * a - lineWidth), lineWidth);
            }
        }

        graphics.dispose();

        ImageIO.write(bufferedImage, "jpg", new File("matrix.jpg"));
        return bufferedImage;
    }

    private void drawLine(Graphics2D graphics, Shape shape, int width) {
        Stroke startStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(width * 2));
        graphics.draw(shape);

        graphics.setStroke(startStroke);
    }

    public static void main(String[] args) throws IOException {
        JapCross japCross = new JapCross();
        BufferedImage image = japCross.getImage(JapCross.class.getResource("/merlin.jpg"));

        BufferedImage targetImage = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
//        AffineTransform transform = new AffineTransform();
//        transform.setToScale(1, -1);
//        transform.translate(-(image.getWidth() - image.getHeight()) / 2, -(image.getWidth() + image.getHeight()) / 2);
//        transform.rotate(Math.toRadians(270), image.getWidth() / 2, image.getHeight() / 2);
//        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
//        op.filter(image, targetImage);
//        image = targetImage;

        japCross.drawJapCrossword(image);
    }

    public BufferedImage drawJapCrossword(BufferedImage image) throws IOException {
        int height = 50;
        int width = 40;

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

                int color = getColor(subImage, drawer);

                matrix.get(i).add(color);
                matrixDraw.get(i).add(drawer.colorToSymbol(color));
            }
        }

        matrixDraw.forEach(strings -> {
            strings.forEach(System.out::print);
            System.out.println();
        });

        List<List<RectangleValue>> horizontalNumbers = countVerticalNumbers(matrix, drawer);

        List<List<RectangleValue>> verticalNumbers = countVerticalNumbers(transpose(matrix), drawer);

        addStartEmptyMatrix(horizontalNumbers, verticalNumbers.get(0).size());

        return drawMatrix(drawer, matrix, horizontalNumbers, transpose(verticalNumbers));
    }

    private void addStartEmptyMatrix(List<List<RectangleValue>> horizontalNumbers, int size) {
        RectangleValue[] rectangleValues = new RectangleValue[horizontalNumbers.get(0).size()];
        Arrays.fill(rectangleValues, new RectangleValue(-1, -1));
        List<RectangleValue> values = Arrays.asList(rectangleValues);

        for (int i = 0; i < size; ++i) {
            horizontalNumbers.add(0, values);
        }
    }

    private List<List<RectangleValue>> countVerticalNumbers(List<List<Integer>> picture, Drawer drawer) {
        List<List<RectangleValue>> verticalCounter = new ArrayList<>();

        for (int i = 0; i < picture.size(); ++i) {
            int startColor = picture.get(i).get(0);
            int count = 0;
            verticalCounter.add(new ArrayList<>());

            for (int j = 0; j < picture.get(i).size(); ++j) {
                Integer newColor = picture.get(i).get(j);
                if (startColor == newColor) {
                    ++count;
                } else {
                    verticalCounter.get(i).add(new RectangleValue(startColor, count));
                    startColor = newColor;
                    count = 1;
                }
            }

            if (count > 0) {
                verticalCounter.get(i).add(new RectangleValue(startColor, count));
            }
        }

        verticalCounter = drawer.filterMatrix(verticalCounter);

        List<RectangleValue> rectangleValues = verticalCounter.stream().max(Comparator.comparingInt(List::size)).get();

        int maxSize = rectangleValues.size();

        for (List<RectangleValue> rectangleValues2 : verticalCounter) {
            int needToAdd = maxSize - rectangleValues2.size();
            for (int j = 0; j < needToAdd; ++j) {
                rectangleValues2.add(0, new RectangleValue(-1, -1));
            }
        }

        if (verticalCounter.stream().anyMatch(rectangleValues1 -> rectangleValues1.size() != maxSize)) {
            throw new RuntimeException("Есть запиь с нреправильным размером");
        }

        return verticalCounter;
    }

    private <T> List<List<T>> transpose(List<List<T>> matrix) {
        List<List<T>> transMatrix = new ArrayList<>();

        for (int i = 0; i < matrix.get(0).size(); ++i) {
            transMatrix.add(new ArrayList<>());
        }

        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(0).size(); ++j) {
                transMatrix.get(j).add(matrix.get(i).get(j));
            }
        }

        return transMatrix;
    }
}
