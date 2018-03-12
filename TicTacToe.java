package Main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TicTacToe extends Application {

    private Stage primaryStage;
    private boolean turnX = true;
    private boolean won = false;
    private Tile[][] board = new Tile[3][3];
    private List<winCondition> winConditions = new ArrayList<>();
    private Pane root;

    private boolean checkState() {
        for (winCondition winCondition : winConditions) {
            if (winCondition.gameFinish()) {
                won = true;
                playLineAnimation(winCondition);
                break;
            }
        }
        return false;
    }

    private void playLineAnimation(winCondition winCondition){
        Line line = new Line();
        line.setStartX(winCondition.tiles[0].getCenterX());
        line.setStartY(winCondition.tiles[0].getCenterY());
        line.setEndX(winCondition.tiles[2].getCenterX());
        line.setEndY(winCondition.tiles[2].getCenterY());

        line.setStrokeWidth(6);
        root.getChildren().add(line);
    }

    private class winCondition {

        private Tile[] tiles;

        public winCondition(Tile... tiles) {
            this.tiles = tiles;
        }

        public boolean gameFinish() {

            if (tiles[0].getText().isEmpty())
                return false;

            return tiles[0].getText().equals(tiles[1].getText())
                    && tiles[0].getText().equals(tiles[2].getText());
        }
    }

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(600, 600);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = new Tile();
                tile.setTranslateX(i * 200);
                tile.setTranslateY(j * 200);

                board[i][j] = tile;

                root.getChildren().add(tile);
            }
        }

        //vertical
        for (int i = 0; i < 3; i++) {
            winConditions.add(new winCondition(board[0][i], board[1][i], board[2][i]));
        }

        //horizontal
        for (int i = 0; i < 3; i++) {
            winConditions.add(new winCondition(board[i][0], board[i][1], board[i][2]));
        }

        //diagonals
        winConditions.add(new winCondition(board[0][0], board[1][1], board[2][2]));
        winConditions.add(new winCondition(board[2][0], board[1][1], board[0][2]));
        this.root = root;
        return root;
    }

    private Parent createOpener() {
        Stage primaryStage = this.primaryStage;

        GridPane pane = new GridPane();
        pane.setPrefSize(600, 600);

        Text title = new Text("TicTacToe");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 40));

        Button btn = new Button("Start Game");

        pane.setAlignment(Pos.CENTER);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                primaryStage.setScene(new Scene(createContent()));
            }
        });
        pane.add(title, 0, 0);
        pane.setHalignment(btn, HPos.CENTER);
        pane.add(btn, 0, 1);

        return pane;
    }

    private Popup createPopup() {
        Popup pop = new Popup();

        GridPane grid = new GridPane();
        grid.setPrefSize(300,200);
        grid.setStyle("-fx-background-color: rgba(64, 224, 208, 0.7);");

        Button replay = new Button("Play Again");

        replay.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                clearGame();
                primaryStage.setScene(new Scene(createContent()));
                pop.hide();
            }
        });

        grid.add(replay, 0, 0);
        grid.setAlignment(Pos.CENTER);

        pop.getContent().add(grid);

        pop.show(primaryStage);

        return pop;
    }

    private void clearGame() {
        won = false;
        turnX = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = board[i][j];
                tile.getTileText().setText(null);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(new Scene(createOpener()));
        primaryStage.show();
    }

    private class Tile extends StackPane {
        private Text text = new Text();

        public Tile() {
            Rectangle border = new Rectangle(200, 200);
            AtomicBoolean filled = new AtomicBoolean();
            filled.set(false);

            border.setFill(null);
            border.setStroke(Color.BLACK);

            setAlignment(Pos.CENTER);
            text.setFont(Font.font(80));
            text.setFill(Color.RED);
            getChildren().addAll(border, text);

            setOnMouseClicked(event -> {

                if (won) {
                    createPopup();
                }

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (turnX && !filled.get()) {
                        drawX();
                        filled.set(true);
                        turnX = false;
                        checkState();
                    }
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    if (!turnX && !filled.get()) {
                        drawO();
                        filled.set(true);
                        turnX = true;
                        checkState();
                    }
                }

            });
        }

        private void drawX() {
            text.setText("X");
        }

        private void drawO() {
            text.setText("O");
        }

        private Text getTileText() {
            return this.text;
        }

        private String getText() {
            return text.getText();
        }

        private double getCenterX(){
            return getTranslateX() + 100;
        }

        private double getCenterY(){
            return getTranslateY() + 100;
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
