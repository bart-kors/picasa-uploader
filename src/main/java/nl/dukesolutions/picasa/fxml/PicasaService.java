package nl.dukesolutions.picasa.fxml;

import static nl.dukesolutions.picasa.fxml.SettingName.GOOGLE_PASSWORD;
import static nl.dukesolutions.picasa.fxml.SettingName.GOOGLE_USERNAME;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.util.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bart on 19/03/14.
 */

@Component
public class PicasaService {

    private PicasawebService picasawebService;

    @Autowired
    private SettingsService settingsService;

    public synchronized PicasawebService getService() throws AuthenticationException {
        if (picasawebService == null) {
            picasawebService = new PicasawebService("picasa-uploader-app");
        }
        picasawebService.setUserCredentials(settingsService.retrieveSetting(GOOGLE_USERNAME),
                settingsService.retrieveSetting(GOOGLE_PASSWORD));
        return picasawebService;
    }

    public String getUserName() throws AuthenticationException {

        String username = settingsService.retrieveSetting(GOOGLE_USERNAME);
        if (username.contains("@")) {
            return username.substring(0, username.indexOf('@'));
        }
        throw new AuthenticationException("wrong google username");
    }


}
