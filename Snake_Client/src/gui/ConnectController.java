package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ConnectController {
    @FXML
    private Button btnConnect;

    @FXML
    public void connectToServer(ActionEvent actionEvent) {
        if(true){ //if connect successful
            Main.setScreen(1);
        }
        else
        {

        }
    }
}
