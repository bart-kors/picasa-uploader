package nl.dukesolutions.picasa.fxml;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by bart on 22/03/14.
 */

@Component
public class ThumbNailService extends Service<Image> {


    private File file;

    @Override
    protected Task<Image> createTask() {
        return new Task<Image>() {
            @Override
            protected Image call() throws Exception {

                return new Image(file.getAbsolutePath(), 343, 0, false, false);
            }
        };
    }
}
