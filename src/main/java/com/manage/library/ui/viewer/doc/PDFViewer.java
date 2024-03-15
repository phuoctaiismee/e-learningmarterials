package com.manage.library.ui.viewer.doc;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import com.dansoftware.pdfdisplayer.PdfJSVersion;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class PDFViewer extends JFrame {

    private JFXPanel jfxPanel;
    private PDFDisplayer displayer;

    public PDFViewer() {
        initFX();
    }

    private void initFX() {
        setTitle("PDF Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1248, 723);
        setLocationRelativeTo(null);

    }

    public void loadPDF(String filePath) {
        SwingUtilities.invokeLater(() -> {
            jfxPanel = new JFXPanel();
            add(jfxPanel, BorderLayout.CENTER);

            Platform.setImplicitExit(false);

            Platform.runLater(() -> {
                try {
                    displayer = new PDFDisplayer(PdfJSVersion._2_2_228);
                    Scene scene = new Scene(displayer.toNode());
                    jfxPanel.setScene(scene);

                    File file = new File(filePath);
                    file.deleteOnExit();
                    if (file.exists()) {
                        displayer.loadPDF(file);
                    } else {
                        JOptionPane.showMessageDialog(null, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (displayer != null) {
                    setVisible(true);
                }
            });
        });
    }

}
