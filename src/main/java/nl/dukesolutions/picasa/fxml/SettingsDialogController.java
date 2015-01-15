package nl.dukesolutions.picasa.fxml;

import static javafx.stage.Modality.WINDOW_MODAL;

import static nl.dukesolutions.picasa.fxml.SettingName.GOOGLE_PASSWORD;
import static nl.dukesolutions.picasa.fxml.SettingName.GOOGLE_USERNAME;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SettingsDialogController {

    @Autowired
    private SettingsService settingsService;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;


    public void showMessageBox(Stage parentStage) {


        try {
            Stage messageBoxStage = new Stage();
            AnchorPane page = (AnchorPane) SpringFxmlLoader.load("/dialog.fxml");
            Scene scene = new Scene(page);
            messageBoxStage.setScene(scene);
            messageBoxStage.setTitle("Settings");
            messageBoxStage.initOwner(parentStage);
            messageBoxStage.initModality(WINDOW_MODAL);
            messageBoxStage.showAndWait();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        username.setText(settingsService.retrieveSetting(GOOGLE_USERNAME));
        password.setText(settingsService.retrieveSetting(GOOGLE_PASSWORD));
    }

    @FXML
    public void onButtonOkay(ActionEvent event) {
        settingsService.storeSetting(GOOGLE_USERNAME, username.getText());
        settingsService.storeSetting(GOOGLE_PASSWORD, password.getText());
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onButtonCancel(ActionEvent event) {
        closeDialog(event);
    }
}