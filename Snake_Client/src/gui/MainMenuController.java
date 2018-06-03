package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainMenuController {
    @FXML
    private Button btnCreateRoom;
    @FXML
    private Button btnJoinRoom;
    @FXML
    private Button btnQuit;


    public void quitGame(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void createRoom(ActionEvent actionEvent) {
    }

    public void joinRoom(ActionEvent actionEvent) {
    }
}
