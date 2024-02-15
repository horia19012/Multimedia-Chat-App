

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class UserModel {
    private String userName;
    private String password;
    private String email;
    private byte[] pic;//BLOB type in sql
    private List<UserModel> friends;


    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public UserModel(String userName, String password, String email, byte[] pic) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.pic = pic;
    }

    public UserModel() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public UserModel(Object json) {
        JSONObject obj = (JSONObject) json;
        try {
            userName = obj.getString("userName");
            password = obj.getString("password");
            email = obj.getString("email");
            pic = obj.getString("pic").getBytes();
        } catch (JSONException e) {
            System.err.println(e);
        }
    }

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("userName", userName);
            json.put("password", password);
            json.put("email", email);
            json.put("pic", pic);
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
    public String toString() {
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", pic=" + Arrays.toString(pic) +
                ", friends=" + friends +
                '}';
    }


}
