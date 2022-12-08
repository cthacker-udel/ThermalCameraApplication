package repository.firestore.datamodel;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class User {

    @DocumentId
    private String userId;

    private String username;
    private String password;

    private String doesHaveProfilePicture;
    private String profilePictureUrl;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private Date lastLogin;

    public User() {}
    public User(String username, String password, String profilePictureUrl) {
        this.username = username;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDoesHaveProfilePicture() {
        return doesHaveProfilePicture;
    }

    public void setDoesHaveProfilePicture(String doesHaveProfilePicture) {
        this.doesHaveProfilePicture = doesHaveProfilePicture;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
