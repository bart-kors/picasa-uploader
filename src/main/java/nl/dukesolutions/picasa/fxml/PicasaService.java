package nl.dukesolutions.picasa.fxml;


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
    private OAuth2 auth2;
    private UserInfo userInfo;

    public synchronized PicasawebService getService() throws AuthenticationException {
        if (picasawebService == null) {

            picasawebService = new PicasawebService("picasa-uploader-app");
        }

        picasawebService.setProtocolVersion(PicasawebService.Versions.V3);

        picasawebService.setHeader("Authorization", "Bearer " + userInfo.getToken());
        return picasawebService;
    }

    public String getUserName() throws AuthenticationException {

        userInfo = auth2.login();
        String username = userInfo.getEmail();
        if (username.contains("@")) {
            return username.substring(0, username.indexOf('@'));
        }
        throw new AuthenticationException("wrong google username");
    }


}
