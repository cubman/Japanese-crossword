package com.home;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class JapCross {

    private BufferedImage getImage(URL url) throws IOException {
        return ImageIO.read(url);
    }

    private List<Float> getHSBList(BufferedImage image) {
        int[] imageRGB = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        List<Float> floatList = new ArrayList<>();

        for (int i = 0; i < imageRGB.length; ++i) {
            float[] floats = Color.RGBtoHSB((imageRGB[i] >> 16) & 0xFF, (imageRGB[i] >> 8) & 0xFF, imageRGB[i] & 0xFF, null);

            floatList.add(floats[2]);
        }

        return floatList;
    }

    private int getColor(BufferedImage image, Drawer drawer) {
        int allPixels = image.getWidth() * image.getHeight();

        List<Float> hsbList = getHSBList(image);
        float sum = 0f;

        for (Float f : hsbList) {
            sum += f;
        }

        return drawer.getColor(sum / allPixels);
    }

    private BufferedImage drawMatrix(Drawer drawer, List<List<Integer>> matrix, Integer recWidth, List<List<RectangleValue>> horizontal,
                                     List<List<RectangleValue>> vertical) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(horizontal.size() * recWidth,
                (matrix.get(0).size() + vertical.size()) * recWidth, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//                graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING , RenderingHints.VALUE_COLOR_RENDER_SPEED);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, recWidth / 2));

        int moveX = 0;
        int moveY = 0;

        graphics.setColor(Color.BLACK);
        for (int i = 0; i < horizontal.size(); ++i) {
            for (int j = 0; j < horizontal.get(i).size(); ++j) {
                graphics.drawRect(moveX + i * recWidth, moveY + j * recWidth, recWidth, recWidth);

                RectangleValue value = horizontal.get(i).get(j);
                int moveXAdd = value.getValue() > 9 ? 1 : 5;
                graphics.drawString(value.getColor() == -1 ? "" : String.valueOf(value.getValue()), moveX + i * recWidth + moveXAdd, moveY + (j + 1) * recWidth - 4);
            }
        }

        moveX = 0;
        moveY = recWidth * horizontal.get(0).size();

        for (int i = 0; i < vertical.size(); ++i) {
            for (int j = 0; j < vertical.get(i).size(); ++j) {
                graphics.drawRect(moveX + i * recWidth, moveY + j * recWidth, recWidth, recWidth);

                RectangleValue value = vertical.get(i).get(j);
                int moveXAdd = value.getValue() > 9 ? 1 : 5;
                graphics.drawString(value.getColor() == -1 ? "" : String.valueOf(value.getValue()), moveX + i * recWidth + moveXAdd, moveY + (j + 1) * recWidth - 4);
            }
        }

        moveX = recWidth * vertical.size();
        moveY = recWidth * horizontal.get(0).size();



        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = 0; j < matrix.get(i).size(); ++j) {
                graphics.setColor(drawer.getColor(matrix.get(i).get(j)));
//                graphics.setColor(Color.WHITE);

                graphics.fillRect(moveX + i * recWidth, moveY + j * recWidth, recWidth, recWidth);

                graphics.setColor(Color.BLACK);
                graphics.drawRect(moveX + i * recWidth, moveY + j * recWidth, recWidth, recWidth);
            }
        }

        int lineWidth = 2;
        drawLine(graphics, new Line2D.Float(0, moveY, moveX + matrix.size() * recWidth - lineWidth, moveY), lineWidth);
        drawLine(graphics, new Line2D.Float(moveX, 0, moveX, moveY + matrix.get(0).size() * recWidth - lineWidth), lineWidth);

        int split = 5;
        for (int i = 0; i < matrix.size()  / split; ++i) {
            for (int j = 0; j < matrix.get(i).size() / split; ++j) {
                drawLine(graphics, new Line2D.Float(0, moveY + j * split * recWidth, moveX + matrix.size() * recWidth - lineWidth, moveY + j * split * recWidth), lineWidth);
                drawLine(graphics, new Line2D.Float(moveX + i * split * recWidth, 0, moveX + i * split * recWidth, moveY + matrix.get(i).size() * recWidth - lineWidth), lineWidth);
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

        japCross.drawJapCrossword(image, 20, 6, 1000, 1000);
    }

    public BufferedImage drawJapCrossword(BufferedImage image, Integer brightness, Integer recWidth, Integer widthAmount, Integer heightAmount) throws IOException {
        int height = widthAmount;
        int width = heightAmount;

        float squareWidth = image.getWidth() / (float) width;
        float squareHeight = image.getHeight() / (float) height;

        Drawer drawer = new TwoSymbolDrawer(brightness);

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

//        matrixDraw.forEach(strings -> {
//            strings.forEach(System.out::print);
//            System.out.println();
//        });

        List<List<RectangleValue>> horizontalNumbers = countVerticalNumbers(matrix, drawer);

        List<List<RectangleValue>> verticalNumbers = countVerticalNumbers(transpose(matrix), drawer);

        addStartEmptyMatrix(horizontalNumbers, verticalNumbers.get(0).size());

        return drawMatrix(drawer, matrix, recWidth, horizontalNumbers, transpose(verticalNumbers));

    }

    public BufferedImage buildHistorgram(BufferedImage image) {
        List<Float> hsbList = getHSBList(image);

        List<Long> result = new ArrayList<>(100);

        for (int i = 0; i < 100; ++i) {
            int finalI = i;
            result.add(hsbList.stream().filter(aFloat -> aFloat >= finalI && aFloat < finalI + 1).count());
        }

        int widthRec = 2;
        int max = result.stream().max(Comparator.comparingLong(o -> o)).get().intValue();

        BufferedImage bufferedImage = new BufferedImage(widthRec * result.size(), max, BufferedImage.TYPE_3BYTE_BGR);
        Graphics graphics = bufferedImage.getGraphics();

        for (int i = 0; i < result.size(); ++i) {
            graphics.drawRect(i * widthRec, 0, widthRec, result.get(i).intValue());
        }

        return bufferedImage;
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
