package nl.dukesolutions.picasa.fxml;

/**
 * Created by bart on 27/06/15.
 */
public class UserInfo {

    private String email;
    private String token;

    public UserInfo(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}
