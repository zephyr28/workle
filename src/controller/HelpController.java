package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HelpController {

    @FXML
    private Button btnClose;

    @FXML
    private void handleClose() {

        btnClose.getScene().getWindow().hide();

    }

}
