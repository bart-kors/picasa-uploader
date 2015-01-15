package nl.dukesolutions.picasa.fxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.ProgressListenerCallbackExecutor;
import com.amazonaws.event.ProgressReportingInputStream;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.data.photos.PhotoEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bart on 19/03/14.
 */
@Component
public class PhotoUploadService extends Service<UploadStatus> {

    @Autowired
    private PicasaService picasaService;

    @Autowired
    private ImageProcessor imageProcessor;

    private AlbumItem albumItem;

    private List<File> files;

    @Override
    protected Task<UploadStatus> createTask() {
        return new UploadTask(albumItem, files);
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public void setAlbumItem(AlbumItem albumItem) {
        this.albumItem = albumItem;
    }


    private class UploadTask extends Task<UploadStatus> {
        private final AlbumItem albumItem;
        private final List<File> files;

        public UploadTask(AlbumItem albumItem, List<File> files) {
            this.albumItem = albumItem;
            this.files = files;
        }

        @Override
        protected UploadStatus call() throws Exception {
            URL albumPostUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + picasaService.getUserName()
                    + "/albumid/" + albumItem.getId());
            List<Link> linkes = new ArrayList<Link>();
            for (final File file : files) {
                createThumbNail(file);

                updateProgress(0, 100);
                updateMessage("uploading : " + file.getName());
                ImageItem imageItem = imageProcessor.process(file);
                PhotoEntry myPhoto = new PhotoEntry();
                myPhoto.setTitle(new PlainTextConstruct(file.getName()));
                myPhoto.setClient("myClientName");

                long length = imageItem.size;

                MyProgressListerner progressListerner = new MyProgressListerner(this, length);
                ProgressListenerCallbackExecutor eventMonitor = new ProgressListenerCallbackExecutor(progressListerner);
                ProgressReportingInputStream progressReportingInputStream = new ProgressReportingInputStream(imageItem.imageBuffer, eventMonitor);
                try {
                    MediaStreamSource myMedia = new MediaStreamSource(progressReportingInputStream, imageItem.imageType, new DateTime(), length);
                    myPhoto.setMediaSource(myMedia);
                    PhotoEntry returnedPhoto = picasaService.getService().insert(albumPostUrl, myPhoto);
                    updateValue(new UploadStatus(null, returnedPhoto.getHtmlLink()));
                } finally {
                    IOUtils.closeQuietly(progressReportingInputStream);
                }
            }
            updateMessage("done");
            return null;

        }

        private void createThumbNail(final File file) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    try {
                        inputStream = FileUtils.openInputStream(file);
                        updateValue(new UploadStatus(new Image(inputStream, 343, 0, true, true), null));
                    } catch (IOException e) {
                        //igore
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                    }
                }
            }
            ).start();
        }

        @Override
        public void updateProgress(long l, long l2) {
            super.updateProgress(l, l2);
        }
    }

    private class MyProgressListerner implements ProgressListener {
        private final long length;
        private final UploadTask uploadTask;
        private long progress = 0;

        public MyProgressListerner(PhotoUploadService.UploadTask uploadTask, long length) {
            this.length = length;
            this.uploadTask = uploadTask;
        }

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            progress += progressEvent.getBytesTransferred();
            uploadTask.updateProgress(progress, length);
        }
    }


}

