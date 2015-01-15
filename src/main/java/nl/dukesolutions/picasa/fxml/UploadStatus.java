package nl.dukesolutions.picasa.fxml;

import com.google.gdata.data.Link;
import javafx.scene.image.Image;

/**
 * Created by bart on 22/03/14.
 */
public class UploadStatus {

    private Image image;

    private Link link;

    public UploadStatus(Image image, Link link) {
        this.image = image;
        this.link = link;
    }

    public Image getImage() {
        return image;
    }

    public Link getLink() {
        return link;
    }
}
