package gui;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.Grid;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static int currentScreen = 0;
    static List<AnchorPane> anchorPanes =  new ArrayList<>();
    private Grid grid;
    private GraphicsContext context;
    static AnchorPane root;
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            root = (AnchorPane)FXMLLoader.load(getClass().getResource("Anchor.fxml"));
            anchorPanes.add((AnchorPane)FXMLLoader.load(getClass().getResource("Connect.fxml")));
            anchorPanes.add((AnchorPane)FXMLLoader.load(getClass().getResource("MainMenu.fxml")));
            root.getChildren().add(anchorPanes.get(0));
            Scene scene = new Scene(root,670,500);
            String css = this.getClass().getResource("/css/application.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        StackPane root = new StackPane();
//        Canvas canvas = new Canvas(WIDTH, HEIGHT);
//        context = canvas.getGraphicsContext2D();
//
//        canvas.setFocusTraversable(true);
//        canvas.setOnKeyPressed(e -> {
//            switch (e.getCode()) {
//                case UP:
//
//                    break;
//                case DOWN:
//
//                    break;
//                case LEFT:
//
//                    break;
//                case RIGHT:
//
//                    break;
//            }
//        });
//
//        root.getChildren().add(canvas);
//
//        Scene scene = new Scene(root);
//
//        primaryStage.setResizable(false);
//        primaryStage.setTitle("Snake");
//        primaryStage.setOnCloseRequest(e -> System.exit(0));
//        primaryStage.setScene(scene);
//        primaryStage.show();
    }
    public static AnchorPane getScreen(int index){
        return anchorPanes.get(index);
    }
    public  static void setScreen(int index){
        root.getChildren().remove(anchorPanes.get(currentScreen));
        root.getChildren().add(anchorPanes.get(index));
        currentScreen = index;
    }
}
