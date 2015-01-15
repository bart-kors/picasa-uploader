package nl.dukesolutions.picasa.fxml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.UserFeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAlbumNamesService extends Service<List<AlbumItem>> {

    @Autowired
    private PicasaService picasaService;


    @Override
    protected Task<List<AlbumItem>> createTask() {
        return new Task() {
            @Override
            protected List<AlbumItem> call() throws Exception {
                List<AlbumItem> list = new ArrayList<AlbumItem>();


                URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + picasaService.getUserName()
                        + "?kind=album");

                UserFeed myUserFeed = picasaService.getService().getFeed(feedUrl, UserFeed.class);


                for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {

                    list.add(new AlbumItem(myAlbum.getTitle().getPlainText(), myAlbum.getGphotoId()));
                }
                return list;
            }
        };

    }

}