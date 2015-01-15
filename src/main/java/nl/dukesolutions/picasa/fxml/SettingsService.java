package nl.dukesolutions.picasa.fxml;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import net.east301.keyring.BackendNotSupportedException;
import net.east301.keyring.Keyring;
import net.east301.keyring.PasswordRetrievalException;
import net.east301.keyring.PasswordSaveException;
import net.east301.keyring.util.LockException;

import org.springframework.stereotype.Service;

/**
 * Created by bart on 15/01/15.
 */
@Service
public class SettingsService {

    public static final String SERVICE = "picasauploader";
    private Keyring keyring;

    @PostConstruct
    public void init() throws BackendNotSupportedException, IOException {
        keyring = Keyring.create();
        if (keyring.isKeyStorePathRequired()) {
            File keyStoreFile = File.createTempFile("picasauploader-keystore", ".keystore");
            keyring.setKeyStorePath(keyStoreFile.getPath());
        }

        for (SettingName settingName : SettingName.values()) {
            try {
                retrieveSetting(settingName);
            } catch (RuntimeException e) {
                storeSetting(settingName, "");
            }
        }
    }

    public void storeSetting(SettingName name, String value) {
        try {
            keyring.setPassword(SERVICE, name.name(), value);
        } catch (LockException e) {
            throw new RuntimeException(e);
        } catch (PasswordSaveException e) {
            throw new RuntimeException(e);
        }

    }

    public String retrieveSetting(SettingName name) {
        try {
            return keyring.getPassword("picasauploader", name.name());
        } catch (LockException e) {
            throw new RuntimeException(e);
        } catch (PasswordRetrievalException e) {
            throw new RuntimeException(e);
        }
    }

}
