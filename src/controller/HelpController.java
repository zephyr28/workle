package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HelpController {

    @FXML
    private Button btnClose;

    @FXML
    private void handleClose() {

        btnClose.getScene().getWindow().hide();

    }

}
