package com.manage.library.ui.viewer.video;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javafx.event.EventHandler;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import net.miginfocom.swing.MigLayout;

public class VideoPlayer extends JFrame {

    private final String videoFilePath;
    private JFXPanel fXPanel;
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private Media media;
    private JButton playPauseButton;
    private JSlider timeSlider;
    private JPanel controlPanel;
    private JPanel mediaControl;
    private JButton volumeIcon;
    private JSlider volume;
    private JLabel volumeStatus;
    private JButton fullScreeen;
    private JLabel durationLabel;
    private JLabel endTime;
    private JButton prevButton;
    private JButton nextButton;

    public VideoPlayer(String videoFilePath) {
        this.videoFilePath = videoFilePath;
        initLayout();
    }

    public void initLayout() {

        setSize(1248, 723);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Control panel
        controlPanel = new JPanel(new MigLayout("fillx, insets 10 50 10 50", "[center]", "[center]"));
        controlPanel.setBackground(new Color(26, 33, 43));

        // Play/Pause button
        playPauseButton = new JButton(new FlatSVGIcon("logos/icons/icon/media/pause.svg"));
        playPauseButton.setBackground(new Color(26, 33, 43));
        playPauseButton.addActionListener(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.setIcon(new FlatSVGIcon("logos/icons/icon/media/play.svg"));
            } else {
                mediaPlayer.play();
                playPauseButton.setIcon(new FlatSVGIcon("logos/icons/icon/media/pause.svg"));
            }
        });

        // Labels for duration
        durationLabel = new JLabel("00:00");
        durationLabel.setForeground(Color.WHITE);
        endTime = new JLabel("00:00");
        endTime.setForeground(Color.WHITE);

        // Volume control
        volumeIcon = new JButton(new FlatSVGIcon("logos/icons/icon/media/volume.svg"));
        volumeIcon.setBackground(new Color(26, 33, 43));
        volumeIcon.setBorder(null);
        volumeIcon.addActionListener((e) -> {
            int volumeOld = (int) mediaPlayer.getVolume();
            if (volumeOld == 0) {
                volume.setValue(50);
            } else {
                volume.setValue(0);
            }
        });
        volumeStatus = new JLabel("100%");
        volumeStatus.setForeground(Color.white);

        //Full screen Button
        fullScreeen = new JButton(new FlatSVGIcon("logos/icons/icon/media/full_video.svg"));
        fullScreeen.setBackground(new Color(26, 33, 43));
        fullScreeen.setBorder(null);

        fullScreeen.addActionListener((e) -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
                dispose();
                setUndecorated(false);
                setSize(new Dimension(1248, 723));
                setLocationRelativeTo(null);
                setVisible(true);

            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                dispose();
                setUndecorated(true);
                setVisible(true);

            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Kiểm tra nếu cửa sổ đang ở chế độ toàn màn hình
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    fullScreeen.setIcon(new FlatSVGIcon("logos/icons/icon/media/small_video.svg"));
                    fullScreeen.setToolTipText("Cửa sổ mặc định");
                } else {
                    fullScreeen.setIcon(new FlatSVGIcon("logos/icons/icon/media/full_video.svg"));
                    fullScreeen.setToolTipText("Toàn màn hình");
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // Xóa file tạm
                deleteTempFile();
            }
        });

        // Previous and Next buttons
        prevButton = new JButton(new FlatSVGIcon("logos/icons/icon/media/prev.svg"));
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
            }
        });
        nextButton = new JButton(new FlatSVGIcon("logos/icons/icon/media/next.svg"));
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
            }
        });
        prevButton.setBackground(new Color(26, 33, 43));
        nextButton.setBackground(new Color(26, 33, 43));

        // Media control panel
        mediaControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        mediaControl.setBackground(new Color(26, 33, 43));
        mediaControl.add(prevButton);
        mediaControl.add(playPauseButton);
        mediaControl.add(nextButton);

    }

    public void playVideo() {
        SwingUtilities.invokeLater(() -> {
            fXPanel = new JFXPanel();
            fXPanel.setBackground(new Color(26, 33, 43));
            add(fXPanel, BorderLayout.CENTER);

            Platform.setImplicitExit(false);
            Platform.runLater(() -> { // Initialize JavaFX on the EDT
                File videoFile = new File(videoFilePath);
                videoFile.deleteOnExit();
                String uri = videoFile.toURI().toString();
                media = new Media(uri);
                mediaPlayer = new MediaPlayer(media);
                mediaView = new MediaView(mediaPlayer);

                StackPane root = new StackPane(mediaView);
                root.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, CornerRadii.EMPTY, null)));

                Scene scene = new Scene(root);

                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());

                timeSlider = new JSlider();
                timeSlider.setMinimum(0);

                mediaView.setOnMouseClicked(new EventHandler() {
                    @Override
                    public void handle(javafx.event.Event t) {
                        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            mediaPlayer.pause();
                            playPauseButton.setIcon(new FlatSVGIcon("logos/icons/icon/media/play.svg"));
                        } else {
                            mediaPlayer.play();
                            playPauseButton.setIcon(new FlatSVGIcon("logos/icons/icon/media/pause.svg"));
                        }
                    }
                });
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    Duration totalDuration = mediaPlayer.getTotalDuration();
                    String totalDurationStr = formatDuration(totalDuration);
                    endTime.setText(totalDurationStr);

                    timeSlider.setMaximum((int) totalDuration.toSeconds());

                    Duration currentDuration = mediaPlayer.getCurrentTime();
                    String currentDurationStr = formatDuration(currentDuration);
                    durationLabel.setText(currentDurationStr);
                    int currentTime = (int) newValue.toSeconds();
                    timeSlider.setValue(currentTime);
                });

                timeSlider.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent evt) {
                        int value = timeSlider.getValue();
                        mediaPlayer.seek(Duration.seconds(value));
                    }
                });

                timeSlider.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        int value = timeSlider.getValue();
                        mediaPlayer.seek(Duration.seconds(value));
                    }
                });

                JPanel pnlVolume = new JPanel(new FlowLayout());
                pnlVolume.setBackground(new Color(26, 33, 43));

                volume = new JSlider(0, 100);
                pnlVolume.add(volumeIcon);
                pnlVolume.add(volume);
                pnlVolume.add(volumeStatus);
                volume.setValue((int) (mediaPlayer.getVolume() * 100));
                volume.addChangeListener(e -> {
                    int value = volume.getValue();
                    if (value == 0) {
                        volumeIcon.setIcon(new FlatSVGIcon("logos/icons/icon/media/no_volume.svg"));
                    } else {
                        volumeIcon.setIcon(new FlatSVGIcon("logos/icons/icon/media/volume.svg"));
                    }
                    volumeStatus.setText(String.valueOf(value) + "%");
                    mediaPlayer.setVolume(value / 100.0);
                });

                pnlVolume.add(fullScreeen);
                controlPanel.add(pnlVolume, "span, align right");

                JPanel pnlControlMedia = new JPanel(new BorderLayout());
                pnlControlMedia.setBackground(new Color(26, 33, 43));

                pnlControlMedia.add(durationLabel, BorderLayout.WEST); // Place durationLabel on the left
                pnlControlMedia.add(endTime, BorderLayout.EAST); // Place endTime on the right
                pnlControlMedia.add(timeSlider, BorderLayout.CENTER);

                controlPanel.add(pnlControlMedia, "span, grow");
                controlPanel.add(mediaControl, "span, grow");
                fXPanel.setScene(scene);
                add(controlPanel, BorderLayout.SOUTH);

                mediaPlayer.setOnReady(() -> {
                    Platform.runLater(() -> {
                        this.setVisible(true);
                    });
                });

                mediaPlayer.play();

            });
        });
    }

    private String formatDuration(Duration duration) {
        int seconds = (int) Math.floor(duration.toSeconds());
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Phương thức để xóa file tạm
    private void deleteTempFile() {
        File videoFile = new File(videoFilePath);
        if (videoFile.exists()) {
            videoFile.delete();
        }
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.registerCustomDefaultsSource("styles");
        FlatIntelliJLaf.setup();
        VideoPlayer videoPlayer = new VideoPlayer("C:/Users/rifud/Downloads/hello.mp4");
        videoPlayer.playVideo();
    }

}
