package com.example.chess;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        ChoiceDialog<String> timeDialog = new ChoiceDialog<>("10+0",
                "1+0", "3+2", "5+0", "10+0");
        timeDialog.setHeaderText("Select Time Control");
        String choice = timeDialog.showAndWait().orElse("10+0");

        int base = Integer.parseInt(choice.split("\\+")[0]) * 60;
        int inc = Integer.parseInt(choice.split("\\+")[1]);

        ChessUI ui = new ChessUI();
        ui.setTimeControl(base, inc);

        Label whiteClock = new Label();
        Label blackClock = new Label();
        ui.bindClocks(whiteClock, blackClock);

        Button playAgain = new Button("Play Again");
        playAgain.setVisible(false);
        playAgain.setOnAction(e -> {
            playAgain.setVisible(false);
            ui.startNewGame();
        });

        ui.setOnGameEnd(() -> playAgain.setVisible(true));

        HBox top = new HBox(20, blackClock, playAgain, whiteClock);
        top.setStyle("-fx-padding:10; -fx-alignment:center");

        BorderPane root = new BorderPane();
        root.setTop(top);
        StackPane center = new StackPane(ui.getRoot());
        center.setStyle("-fx-padding:20;");
        center.setAlignment(javafx.geometry.Pos.CENTER);

        ui.getRoot().prefWidthProperty().bind(
                center.widthProperty().subtract(40));
        ui.getRoot().prefHeightProperty().bind(
                center.heightProperty().subtract(40));

        root.setCenter(center);


        stage.setScene(new Scene(root, 800, 800));
        stage.setTitle("Chess");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
