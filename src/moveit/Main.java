package moveit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.TrayIcon;
import java.awt.SystemTray;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private boolean firstTime;
    private TrayIcon trayIcon;

    public void start(Stage primaryStage) {
        Mover mover = new Mover();
        mover.setMove(false);
        firstTime = true;

        // center
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Text scenetitle = new Text("I like to move it - move it!");
        scenetitle.setFont(Font.font("Helvetica", FontWeight.BLACK, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label secondsLabel = new Label("Seconds: ");
        grid.add(secondsLabel, 0, 1);

        TextField secondsField = new TextField();
        secondsField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,4}?")) {
                    secondsField.setText(oldValue);
                }
            }
        });
        secondsField.setText("30");
        grid.add(secondsField, 1, 1);

        Label pixelsLabel = new Label("Pixels: ");
        grid.add(pixelsLabel, 0, 2);

        TextField pixelsField = new TextField();
        pixelsField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,4}?")) {
                    pixelsField.setText(oldValue);
                }
            }
        });
        pixelsField.setText("1");
        grid.add(pixelsField, 1, 2);

        HBox textBox = new HBox();
        textBox.setAlignment(Pos.CENTER);

        Text action = new Text();
        action.setTextAlignment(TextAlignment.CENTER);
        action.setFont(Font.font("Helvetica", FontWeight.BLACK, 13));
        textBox.getChildren().add(action);
        grid.add(textBox, 0,3,2,1);

        // bottom respectively "button area"
        HBox buttonBox = new HBox();

        Button startButton = new Button();
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);
        Button minimizeButton = new Button();

        minimizeButton.setText("Minimize to tray");
        minimizeButton.setOnAction(event -> {
            System.out.println("Minimize to tray button pressed.");
            if(firstTime){
                createTrayIcon(primaryStage);
                Platform.setImplicitExit(false);
                firstTime = false;
            }
            hide(primaryStage);
        });

        startButton.setText("Start");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            Thread moverThread;
            public void handle(ActionEvent event) {
                try {
                    if(pixelsField.getText().length() > 0 && secondsField.getText().length() > 0) {
                        int pixels = Integer.parseInt(pixelsField.getText());
                        int seconds = Integer.parseInt(secondsField.getText());

                        if (mover.getMove()) {
                            mover.setMove(false);
                            moverThread.interrupt();
                            startButton.setText("Start");
                            action.setText("Process stopped.");
                            action.setFill(Color.FIREBRICK);
                        } else {
                            moverThread = new Thread(mover);
                            mover.setPixels(pixels);
                            mover.setSeconds(seconds);
                            mover.setMove(true);
                            moverThread.start();
                            startButton.setText("Stop");
                            action.setText("Process started.");
                            action.setFill(Color.DARKGREEN);
                        }
                    }else{
                        action.setText("Fill in the seconds and pixels.");
                        action.setFill(Color.RED);
                    }
                }catch (Exception e){
                    System.out.println("Exception occurred: " + e.toString());
                }
            }
        });

        buttonBox.getChildren().addAll(startButton, spacer, minimizeButton);
        grid.add(buttonBox, 0, 4, 2, 1);

        // root
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20)); // space between elements and window border
        root.setCenter(grid);

        Scene scene = new Scene(root, 280, 210);

        primaryStage.setTitle("Hopper");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                System.out.println("Closing app");
                if (mover.getMove()) {
                    startButton.fire();
                }
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void createTrayIcon(final Stage stage) {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            SystemTray tray = SystemTray.getSystemTray();
            // load an image
            java.awt.Image image = null;
            try {
                URL url = getClass().getClassLoader().getResource("hand.png");
                image = ImageIO.read(url).getScaledInstance(18,18, Image.SCALE_SMOOTH);
            } catch (IOException ex) {
                System.out.println(ex);
            }

            /*stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    hide(stage);
                }
            });*/
            // create a action listener to listen for default action executed on the tray icon
            final ActionListener closeListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            };

            ActionListener showListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                            stage.toFront();
                        }
                    });
                }
            };
            // create a popup menu
            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);
            trayIcon = new TrayIcon(image, "Hopper", popup);
            // set the TrayIcon properties
            //trayIcon.addActionListener(showListener);
            // ...
            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
            // ...
        }
    }

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Hopper",
                    "is now running in the background.",
                    TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }

    private void hide(final Stage stage) {
        System.out.println("hiding..");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                    stage.hide();
                    //showProgramIsMinimizedMsg();
                } else {
                    System.exit(0);
                }
            }
        });
    }
}