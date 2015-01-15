package nl.dukesolutions.picasa.fxml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bart on 19/03/14.
 */
@Component
public class ImageProcessor {

    private Set<String> imageMimeTypes = new HashSet<String>(Arrays.asList("image/bmp", "image/gif", "image/jpeg", "image/png"));

    @Autowired
    private ImageResizer resizer;

    @Autowired
    private MetaDataCopier metaDataCopier;

    public ImageItem process(File file) throws IOException {
        String mimetype = new Tika().detect(file);
        ImageItem item = new ImageItem();
        if (imageMimeTypes.contains(mimetype)) {
            BufferedImage image = ImageIO.read(file);
            image = resizer.resize(image);
            byte[] imageDataBytes = toByteArray(image, mimetype);
            imageDataBytes = metaDataCopier.addMetadata(imageDataBytes, file);
            item.imageBuffer = new ByteArrayInputStream(imageDataBytes);
            item.size = imageDataBytes.length;
        } else {
            item.imageBuffer = FileUtils.openInputStream(file);
            item.size = file.length();
        }

        item.imageName = file.getName();
        item.imageType = mimetype;

        return item;
    }

    private byte[] toByteArray(BufferedImage image, String mimetype) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Assert.isTrue(ImageIO.write(image, mimetype.split("/")[1], baos));
            baos.flush();
        } finally {
            IOUtils.closeQuietly(baos);
        }
        return baos.toByteArray();

    }
}
