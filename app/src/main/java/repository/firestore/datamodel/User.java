package repository.firestore.datamodel;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import repository.firestore.contracts.UserFirestoreDbContract;

public class User {

    @DocumentId
    private String userId;

    private String username;
    private String password;
    private String passwordSalt;
    private Integer passwordIterations;

    private boolean doesHaveProfilePicture;
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
    public User(DocumentSnapshot document) {
        this.userId = document.get(UserFirestoreDbContract.DOCUMENT_ID, String.class);
        this.username = document.get(UserFirestoreDbContract.USERNAME_ID, String.class);
        this.doesHaveProfilePicture = Boolean.TRUE.equals(document.get(UserFirestoreDbContract.FIELD_DOES_HAVE_PROFILE_PICTURE, Boolean.class));
        this.profilePictureUrl = document.get(UserFirestoreDbContract.FIELD_PROFILE_PICTURE_URL, String.class);
        this.firstName = document.get(UserFirestoreDbContract.FIELD_FIRST_NAME, String.class);
        this.lastName = document.get(UserFirestoreDbContract.FIELD_LAST_NAME, String.class);
        this.email = document.get(UserFirestoreDbContract.FIELD_EMAIL, String.class);
        this.phoneNumber = document.get(UserFirestoreDbContract.FIELD_PHONE_NUMBER, String.class);
        this.passwordIterations = document.get(UserFirestoreDbContract.FIELD_PASSWORD_ITERATIONS, Integer.class);
        this.passwordSalt = document.get(UserFirestoreDbContract.FIELD_PASSWORD_SALT, String.class);
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setDoesHaveProfilePicture(boolean doesHaveProfilePicture) {
        this.doesHaveProfilePicture = doesHaveProfilePicture;
        return this;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public User setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public User setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public User setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
        return this;
    }

    public Integer getPasswordIterations() {
        return passwordIterations;
    }

    public User setPasswordIterations(Integer passwordIterations) {
        this.passwordIterations = passwordIterations;
        return this;
    }

    public boolean isDoesHaveProfilePicture() {
        return doesHaveProfilePicture;
    }
}
