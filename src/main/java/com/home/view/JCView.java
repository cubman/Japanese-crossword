package com.home.view;

import com.home.JapCross;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JCView extends JFrame {
    private JButton loadPhoto;
    private JSpinner recSize;
    private JPanel mainPane;
    private JSpinner whiteToBlack;
    private JPanel originPhoto;
    private JPanel japPhoto;

    private JapCross japCross;

    public JCView() {
        japCross = new JapCross();

        setContentPane(mainPane);

        recSize.setValue(4);
        whiteToBlack.setValue(50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        loadPhoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setCurrentDirectory(new File("D:\\Japanese-crossword2\\src\\main\\resources"));
                int ret = jFileChooser.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    try {
                        BufferedImage image = ImageIO.read(file);


//                        BufferedImage bufferedImage = toBufferedImage(image);
                        BufferedImage image1 = japCross.drawJapCrossword(image);
                        japPhoto.getGraphics().drawImage(image1, 0, 0, Color.BLACK, null);

                        Graphics photoGraphics = originPhoto.getGraphics();
                        photoGraphics.drawImage(image, 0, 0, Color.BLACK, null);
//                        originPhoto.repaint();
//                        originPhoto.validate();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
