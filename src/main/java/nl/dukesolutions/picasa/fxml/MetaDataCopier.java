package nl.dukesolutions.picasa.fxml;


import org.apache.commons.io.IOUtils;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;


@Component
public class MetaDataCopier {
    public byte[] addMetadata(byte[] imageData, File file) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream stream = new ByteArrayInputStream(imageData);
        try {
            IImageMetadata metadata = Sanselan.getMetadata(file);
            if (!(metadata instanceof JpegImageMetadata)) {
                return imageData;
            }

            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            TiffOutputSet outputSet = jpegMetadata.getExif().getOutputSet();


            new ExifRewriter().updateExifMetadataLossless(stream, baos,
                    outputSet);
            baos.flush();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(stream);
        }
        return baos.toByteArray();

    }
}
