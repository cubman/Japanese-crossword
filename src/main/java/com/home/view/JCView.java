package com.home.view;

import com.home.JapCross;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JCView extends JFrame {
    private JButton loadPhoto;
    private JSpinner brightness;
    private JPanel mainPane;
    private JSpinner rowAm;
    private JPanel originPhoto;
    private JPanel japPhoto;
    private JSpinner amWidth;
    private JSpinner amHeight;
    private JPanel historamPhoto;

    private JapCross japCross;
    private BufferedImage image;

    public JCView() {
        japCross = new JapCross();

        setContentPane(mainPane);

        brightness.setValue(50);
        rowAm.setValue(4);
        amWidth.setValue(70);
        amHeight.setValue(80);

        brightness.addChangeListener(new EventChangeSpinnerListener());
        rowAm.addChangeListener(new EventChangeSpinnerListener());
        amWidth.addChangeListener(new EventChangeSpinnerListener());
        amHeight.addChangeListener(new EventChangeSpinnerListener());

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
                        image = ImageIO.read(file);


//                        BufferedImage bufferedImage = toBufferedImage(image);
                        BufferedImage image1 = japCross.drawJapCrossword(image, (Integer) brightness.getValue(), (Integer) rowAm.getValue(), (Integer) amWidth.getValue(), (Integer) amHeight.getValue());

                        ((PaintPanel)japPhoto).updateImage(image1);
                        japPhoto.repaint();

//                        japPhoto.getGraphics().drawImage(image1, 0, 0, Color.BLACK, null);
                        ((PaintPanel)originPhoto).updateImage(image);
                        originPhoto.repaint();

                        BufferedImage buildHistorgram = japCross.buildHistorgram(image);
                        ((PaintPanel)historamPhoto).updateImage(buildHistorgram);
                        historamPhoto.repaint();
//                        originPhoto.repaint();
//                        originPhoto.validate();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        originPhoto = new PaintPanel();
        japPhoto = new PaintPanel();
        historamPhoto = new PaintPanel();
    }

    private class EventChangeSpinnerListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            BufferedImage image1 = null;
            try {
                image1 = japCross.drawJapCrossword(image, (Integer) brightness.getValue(), (Integer) rowAm.getValue(), (Integer) amWidth.getValue(), (Integer) amHeight.getValue());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            ((PaintPanel)japPhoto).updateImage(image1);
            japPhoto.repaint();
        }
    }
}
