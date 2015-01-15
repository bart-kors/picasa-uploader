package nl.dukesolutions.picasa.fxml;


import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ImageResizer {
    public BufferedImage resize(BufferedImage image) {
        return
                Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                        2048, 1151, Scalr.OP_ANTIALIAS);

    }
}
