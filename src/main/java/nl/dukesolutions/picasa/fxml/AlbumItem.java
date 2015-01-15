package nl.dukesolutions.picasa.fxml;

/**
 * Created by bart on 19/03/14.
 */
public class AlbumItem {
    private final String name;
    private final String id;

    public AlbumItem(String plainText, String gphotoId) {
        this.name = plainText;
        this.id = gphotoId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
