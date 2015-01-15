package nl.dukesolutions.picasa.fxml;

import java.util.List;

import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import com.google.gdata.util.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainScreenController {

    private final static Logger LOGGER = LoggerFactory.getLogger(MainScreenController.class);

    public static final ObservableList names =
            FXCollections.observableArrayList();

    public static HostServices hostedService;
    public static Stage primaryStage;

    @Autowired
    private GetAlbumNamesService getAlbumNamesService;

    @Autowired
    private PhotoUploadService photoUploadService;

    @FXML
    private ListView<AlbumItem> albumList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private ImageView thumbNail;

    @FXML
    private Hyperlink imageLink;

    @FXML
    private MenuItem preferencesMenuItem;


    @FXML
    protected void handleDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {

            success = true;
            photoUploadService.setFiles(db.getFiles());
            photoUploadService.setAlbumItem(albumList.getSelectionModel().getSelectedItem());

            photoUploadService.start();

        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    protected void handleDetect(DragEvent event) {
        // actiontarget.setText("Sign in button pressed");
    }

    @FXML
    protected void handleDragOver(DragEvent event) {

        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }

    }

    @FXML
    void initialize() {

        preferencesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showSettingsDialog();
            }
        });

        albumList.setItems(names);

        getAlbumNamesService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                List<AlbumItem> list = (List<AlbumItem>) t.getSource().getValue();
                for (AlbumItem name : list) {
                    names.add(name);
                }
            }
        });
        getAlbumNamesService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (workerStateEvent.getSource().getException() instanceof AuthenticationException) {
                    showSettingsDialog();
                } else {
                    LOGGER.error("getalbums failed", workerStateEvent.getSource().getException());
                }

            }
        });

        fillAlbumList();

        photoUploadService.setOnScheduled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                progressLabel.textProperty().bind(photoUploadService.messageProperty());
                progressBar.progressProperty().bind(photoUploadService.progressProperty());
            }
        });


        photoUploadService.valueProperty().addListener(new ChangeListener<UploadStatus>() {
            @Override
            public void changed(ObservableValue<? extends UploadStatus> observableValue, UploadStatus uploadStatus, UploadStatus uploadStatus2) {
                if (uploadStatus2 != null && uploadStatus2.getImage() != null) {
                    thumbNail.setImage(uploadStatus2.getImage());
                }
                if (uploadStatus2 != null && uploadStatus2.getLink() != null) {
                    imageLink.setText(uploadStatus2.getLink().getHref());
                }
            }
        });

        imageLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hostedService.showDocument(imageLink.getText());
            }
        });

        photoUploadService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                LOGGER.info("uploaded {}", t.getSource().getValue());
                progressBar.progressProperty().unbind();
                progressLabel.textProperty().unbind();
                progressBar.setProgress(0);
                progressLabel.setText("done");
                photoUploadService.reset();


            }
        });
        photoUploadService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                LOGGER.error("upload failed", workerStateEvent.getSource().getException());
                progressBar.progressProperty().unbind();
                progressLabel.textProperty().unbind();
                progressBar.setProgress(0);
                progressLabel.setText("error : " + workerStateEvent.getSource().getException().getMessage());
                photoUploadService.reset();

            }
        });
    }

    private void fillAlbumList() {
        names.clear();
        getAlbumNamesService.reset();
        getAlbumNamesService.start();
    }

    private void showSettingsDialog() {
        SettingsDialogController msgBox = new SettingsDialogController();
        msgBox.showMessageBox(primaryStage);
        fillAlbumList();

    }


}