package repository.firestore.manager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import helpers.ListenerFuncGenerator;
import repository.firestore.contracts.UserFirestoreDbContract;
import repository.firestore.datamodel.User;

public final class UserFirestoreManager extends FirestoreManager {
    public DocumentSnapshot foundUserSnapshot = null;
    public User foundUser = null;
    public static class PasswordGenerationSpecifications {
        private String hash;
        private Integer iterations;
        private String salt;

        public PasswordGenerationSpecifications() {
            this.hash = null;
            this.iterations = null;
            this.salt = null;
        }

        public PasswordGenerationSpecifications(String hash, Integer iterations, String salt) {
            this.hash = hash;
            this.iterations = iterations;
            this.salt = salt;
        }

        public String getHash() {
            return hash;
        }

        public PasswordGenerationSpecifications setHash(String hash) {
            this.hash = hash;
            return this;
        }

        public Integer getIterations() {
            return iterations;
        }

        public PasswordGenerationSpecifications setIterations(Integer iterations) {
            this.iterations = iterations;
            return this;
        }

        public String getSalt() {
            return salt;
        }

        public PasswordGenerationSpecifications setSalt(String salt) {
            this.salt = salt;
            return this;
        }
    }
    public static class AddUserStatus {
        public boolean alreadyExists = false;
        public boolean success = false;

        public AddUserStatus() {
            this.alreadyExists = false;
            this.success = false;
        }

        public AddUserStatus(boolean alreadyExists, boolean success) {
            this.alreadyExists = alreadyExists;
            this.success = success;
        }

        public boolean isAlreadyExists() {
            return alreadyExists;
        }

        public void setAlreadyExists(boolean alreadyExists) {
            this.alreadyExists = alreadyExists;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
    /**
     * Initializes the client with the FirebaseFirestore client
     *
     * @param client - The client passed into the constructor
     */
    public UserFirestoreManager(FirebaseFirestore client) {
        super(client);
        super.setCollection("users");
    }

    public User convertDocumentSnapshot(DocumentSnapshot foundUser) {
        User newUser = new User();
        newUser.setUsername(foundUser.get(UserFirestoreDbContract.FIELD_USERNAME, String.class))
                .setEmail(foundUser.get(UserFirestoreDbContract.FIELD_EMAIL, String.class))
                .setPhoneNumber(foundUser.get(UserFirestoreDbContract.FIELD_PHONE_NUMBER, String.class))
                .setFirstName(foundUser.get(UserFirestoreDbContract.FIELD_FIRST_NAME, String.class))
                .setLastName(foundUser.get(UserFirestoreDbContract.FIELD_LAST_NAME, String.class))
                .setPasswordSalt(foundUser.get(UserFirestoreDbContract.FIELD_PASSWORD_SALT, String.class))
                .setPasswordIterations(foundUser.get(UserFirestoreDbContract.FIELD_PASSWORD_ITERATIONS, Integer.class))
                .setPassword(foundUser.get(UserFirestoreDbContract.FIELD_PASSWORD, String.class))
                .setDoesHaveProfilePicture(Boolean.TRUE.equals(foundUser.get(UserFirestoreDbContract.FIELD_DOES_HAVE_PROFILE_PICTURE, Boolean.class)))
                .setLastLogin(foundUser.get(UserFirestoreDbContract.FIELD_LAST_LOGIN, Date.class));
        return newUser;
    }

    public User findUser(String username) {
        super.set(UserFirestoreDbContract.FIELD_USERNAME, username, ListenerFuncGenerator.generateOnSuccessListener(e -> {
            this.foundUserSnapshot = e.getDocuments().size() > 0 ? e.getDocuments().get(0) : null;
            this.foundUser = this.foundUserSnapshot != null ? convertDocumentSnapshot(this.foundUserSnapshot) : null;
            return null;
        }), ListenerFuncGenerator.generateFailureListener(e -> {
            e.printStackTrace();
            return null;
        }));
        return this.foundUser;
    }

    public void deleteUser(String username, final OnSuccessListener<? super Void> onSuccessListener, final OnFailureListener onFailureListener) {
        super.set(UserFirestoreDbContract.FIELD_USERNAME, username, ListenerFuncGenerator.generateOnSuccessListener(e -> {
            this.foundUserSnapshot = e.getDocuments().get(0);
            return null;
        }), ListenerFuncGenerator.generateFailureListener(e -> {
            e.printStackTrace();
            return null;
        }));
        super.delete(onSuccessListener, onFailureListener);
    }

    public boolean doesUserExist(String username) {
        return this.findUser(username) != null;
    }

    public void updateUser(String username, String field, String value, final OnSuccessListener<? super Void> onSuccessListener, final OnFailureListener onFailureListener) {
        super.set(UserFirestoreDbContract.FIELD_USERNAME, username, ListenerFuncGenerator.generateOnSuccessListener(e -> {
            this.foundUserSnapshot = e.getDocuments().get(0);
            return null;
        }), ListenerFuncGenerator.generateFailureListener(e -> {
            e.printStackTrace();
            return null;
        }));
        super.update(field, value, onSuccessListener, onFailureListener);
    }

    public AddUserStatus addUser(final String username, final String password, final OnSuccessListener<DocumentReference> onSuccessListener, final OnFailureListener onFailureListener) {
        AddUserStatus addingUserStatus = new AddUserStatus();
        boolean alreadyExists = this.findUser(username) != null;
        addingUserStatus.setAlreadyExists(alreadyExists);
        if (!alreadyExists) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            super.add(newUser, onSuccessListener, onFailureListener);
            addingUserStatus.setSuccess(true);
        }
        return addingUserStatus;
    }

    public AddUserStatus addUserV2(final User user, final OnSuccessListener<DocumentReference> onSuccessListener, final OnFailureListener onFailureListener) {
        AddUserStatus addingUserStatus = new AddUserStatus();
        boolean alreadyExists = this.findUser(user.getUsername()) != null;
        addingUserStatus.setAlreadyExists(alreadyExists);
        if (!alreadyExists) {
            super.add(user, onSuccessListener, onFailureListener);
            addingUserStatus.setSuccess(true);
        }
        return addingUserStatus;
    }

    public boolean validatePassword(final String username, final String hashedPassword) {
        super.set(UserFirestoreDbContract.FIELD_USERNAME, username, ListenerFuncGenerator.generateOnSuccessListener(e -> {
            this.foundUserSnapshot = e.getDocuments().get(0);
            return null;
        }), ListenerFuncGenerator.generateFailureListener(e -> {
            e.printStackTrace();
            return null;
        }));
        return Objects.requireNonNull(snapshot.get(UserFirestoreDbContract.FIELD_PASSWORD)).toString().equals(hashedPassword);
    }

    public boolean validateUser(final String username, final String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // hash password if user exists
        if (!this.doesUserExist(username)) {
            return false;
        }
        // hash password similar to user data
        User foundUser = this.findUser(username);
        assert foundUser != null;
        Integer iterations = foundUser.getPasswordIterations();
        String hash = foundUser.getPassword();
        String salt = foundUser.getPasswordSalt();
        String result = fixedPasswordEncryption(password, iterations, salt);
        return result.equals(hash);
    }

    public String fixedPasswordEncryption(final String password, final Integer numberIterations, final String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory pbkdf2Factory = null;
        try {
            pbkdf2Factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
            throw exception;
        }

        PBEKeySpec generatedKey = new PBEKeySpec(password.toCharArray(), salt.getBytes(), numberIterations, 128);
        SecretKey key;
        try {
            // hashing part
            key = pbkdf2Factory.generateSecret(generatedKey);
        } catch (InvalidKeySpecException exception) {
            exception.printStackTrace();
            throw exception;
        }

        byte[] result = key.getEncoded();

        // convert encoded key to string and return
        return new String(result, StandardCharsets.UTF_8);
    }

    public PasswordGenerationSpecifications generatePassword(String username, String password)  {
        SecureRandom randomGenerator = new SecureRandom();
        String saltCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<>,./?;:[]{}|()&*%^#$!@~`";
        int saltLength = saltCharacters.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            sb.append(saltCharacters.charAt(randomGenerator.nextInt(saltLength)));
        }
        // formulated salt
        int randomIterations = randomGenerator.nextInt(501);
        // formulated # of iterations
        SecretKeyFactory pbkdf2Factory = null;
        // initializing factory to hash key
        try {
            pbkdf2Factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }

        // generate key from combination of password, salt, and iterations
        PBEKeySpec generatedKey = new PBEKeySpec(password.toCharArray(), sb.toString().getBytes(), randomIterations, 128);
        SecretKey key = null;
        try {
            // hashing part
            assert pbkdf2Factory != null;
            key = pbkdf2Factory.generateSecret(generatedKey);
        } catch (InvalidKeySpecException | NullPointerException exception) {
            exception.printStackTrace();
        }

        // get encoded key
        assert key != null;
        byte[] result = key.getEncoded();

        // convert encoded key to string
        String resultantPassword = new String(result, StandardCharsets.UTF_8);

        PasswordGenerationSpecifications specifications = new PasswordGenerationSpecifications();
        specifications.setHash(resultantPassword).setIterations(randomIterations).setSalt(sb.toString());
        return specifications;
    }

    /**
     * Retrieves an instance of the class
     *
     * @return The instance of the class
     */
    public UserFirestoreManager getInstance() {
        return this;
    }
}
