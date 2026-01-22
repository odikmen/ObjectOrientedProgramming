package com.example.chess;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class ChessUI {

    private final GridPane boardGrid = new GridPane();
    private final StackPane root = new StackPane();

    private Board board = new Board();
    private final Map<Position, StackPane> cellMap = new HashMap<>();
    private Position selected = null;

    private Label whiteClock, blackClock;
    private Timeline clock;

    private int baseSeconds = 600;
    private int incrementSeconds = 0;
    private int whiteSeconds;
    private int blackSeconds;

    private Runnable gameEndCallback;

    public ChessUI() {
        setupBoardUI();
    }

    public Pane getRoot() {
        return root;
    }



    public void setTimeControl(int baseSeconds, int incrementSeconds) {
        this.baseSeconds = baseSeconds;
        this.incrementSeconds = incrementSeconds;
        startNewGame();
    }

    public void bindClocks(Label white, Label black) {
        this.whiteClock = white;
        this.blackClock = black;
        updateClocks();
    }

    public void setOnGameEnd(Runnable r) {
        this.gameEndCallback = r;
    }

    public void startNewGame() {
        board = new Board();
        selected = null;
        whiteSeconds = baseSeconds;
        blackSeconds = baseSeconds;
        startClock();
        rebuildBoard();
    }


    private void startClock() {
        if (clock != null) clock.stop();

        clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (board.currentTurn == Piece.Color.WHITE) whiteSeconds--;
            else blackSeconds--;

            updateClocks();

            if (whiteSeconds <= 0 || blackSeconds <= 0) {
                clock.stop();
                showGameOver("Time out");
            }
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void addIncrement(Piece.Color color) {
        if (color == Piece.Color.WHITE) whiteSeconds += incrementSeconds;
        else blackSeconds += incrementSeconds;
    }

    private void updateClocks() {
        if (whiteClock != null)
            whiteClock.setText(format(whiteSeconds));
        if (blackClock != null)
            blackClock.setText(format(blackSeconds));
    }

    private String format(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }


    private void setupBoardUI() {

        boardGrid.getChildren().clear();
        boardGrid.getRowConstraints().clear();
        boardGrid.getColumnConstraints().clear();

        for (int i = 0; i < 8; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(12.5);
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(12.5);
            boardGrid.getRowConstraints().add(r);
            boardGrid.getColumnConstraints().add(c);
        }

        root.getChildren().clear();
        root.getChildren().add(boardGrid);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        rebuildBoard();
    }


    private void rebuildBoard() {
        boardGrid.getChildren().clear();
        cellMap.clear();

        boolean whiteBottom = board.currentTurn == Piece.Color.WHITE;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                int br = whiteBottom ? 7 - r : r;
                int bc = whiteBottom ? c : 7 - c;

                Position pos = new Position(br, bc);

                StackPane cell = new StackPane();
                Color base = (br + bc) % 2 == 0
                        ? Color.web("#EEEED2")
                        : Color.web("#769656");

                cell.setBackground(new Background(
                        new BackgroundFill(base, null, null)));

                cell.prefWidthProperty().bind(boardGrid.widthProperty().divide(8));
                cell.prefHeightProperty().bind(boardGrid.heightProperty().divide(8));

                cell.setOnMouseClicked(e -> onCellClick(pos));

                cellMap.put(pos, cell);
                boardGrid.add(cell, c, r);
            }
        }
        refresh();
    }


    private void onCellClick(Position pos) {

        resetBoardColors();

        Piece p = board.getPiece(pos);

        if (selected == null) {
            if (p == null || p.color != board.currentTurn) return;
            selected = pos;
            highlightLegal(pos);
        } else {
            Move move = new Move(selected, pos);
            var legal = board.generateLegalMovesForPiece(selected);

            Optional<Move> chosen = legal.stream()
                    .filter(m -> m.equalsIgnorePromotion(move))
                    .findFirst();

            if (chosen.isPresent()) {
                Piece.Color mover = board.currentTurn;
                board.makeMove(chosen.get());
                addIncrement(mover);
                selected = null;
                rebuildBoard();
                checkGameState();
            } else {
                selected = null;
                refresh();
            }
        }
    }

    private void refresh() {
        for (Position pos : cellMap.keySet()) {
            StackPane cell = cellMap.get(pos);
            cell.getChildren().clear();

            Piece p = board.getPiece(pos);
            if (p != null) {
                Text t = new Text(p.getSymbol());
                t.setFont(Font.font(48));
                cell.getChildren().add(t);
            }
        }
    }

    private void highlightLegal(Position from) {
        refresh();
        for (Move m : board.generateLegalMovesForPiece(from)) {
            cellMap.get(m.to).setBackground(
                    new Background(new BackgroundFill(Color.web("#BACA2B88"), null, null)));
        }
    }

    private void checkGameState() {
        if (board.isInCheckmate(board.currentTurn) || board.isInStalemate(board.currentTurn)) {
            showGameOver("Game Over");
        }
    }

    private void showGameOver(String msg) {
        clock.stop();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(msg);
        a.showAndWait();
        if (gameEndCallback != null) gameEndCallback.run();
    }

    private void resetBoardColors() {
        for (Map.Entry<Position, StackPane> entry : cellMap.entrySet()) {
            Position pos = entry.getKey();
            StackPane cell = entry.getValue();

            Color base = (pos.row + pos.col) % 2 == 0
                    ? Color.web("#EEEED2")
                    : Color.web("#769656");

            cell.setBackground(new Background(
                    new BackgroundFill(base, null, null)));
        }
    }

}
