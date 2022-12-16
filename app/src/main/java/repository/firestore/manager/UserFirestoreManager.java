package repository.firestore.manager;

import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import helpers.ListenerFuncGenerator;
import repository.firestore.contracts.UserFirestoreDbContract;
import repository.firestore.datamodel.User;

class AddUserStatus {
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

class PasswordGenerationSpecifications {
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

public final class UserFirestoreManager extends FirestoreManager {
    /**
     * Initializes the client with the FirebaseFirestore client
     *
     * @param client - The client passed into the constructor
     */
    public UserFirestoreManager(FirebaseFirestore client) {
        super(client);
        super.setCollection("users");
    }

    public User findUser(String username) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
        return super.snapshot != null ? new User(super.snapshot) : null;
    }

    public boolean deleteUser(String username) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
        return super.delete();
    }

    public boolean doesUserExist(String username) {
        return this.findUser(username) != null;
    }

    public boolean updateUser(String username, String field, String value) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
        return super.update(field, value);
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
        }
        return addingUserStatus;
    }

    public AddUserStatus addUserV2(final User user, final OnSuccessListener<DocumentReference> onSuccessListener, final OnFailureListener onFailureListener) {
        AddUserStatus addingUserStatus = new AddUserStatus();
        boolean alreadyExists = this.findUser(user.getUsername()) != null;
        System.out.printf("Does user exist: %s\n", alreadyExists);
        addingUserStatus.setAlreadyExists(alreadyExists);
        if (!alreadyExists) {
            System.out.printf("Adding user\n");
            super.add(user, onSuccessListener, onFailureListener);
            addingUserStatus.setSuccess(true);
        }
        return addingUserStatus;
    }

    public boolean validatePassword(final String username, final String hashedPassword) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
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

    public PasswordGenerationSpecifications generatePassword(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecureRandom randomGenerator = new SecureRandom();
        byte[] byteArray = new byte[128];
        randomGenerator.nextBytes(byteArray);
        // formulated bytes
        String salt = new String(byteArray, StandardCharsets.UTF_8);
        // formulated salt
        int randomIterations = randomGenerator.nextInt();
        // formulated # of iterations
        SecretKeyFactory pbkdf2Factory = null;
        // initializing factory to hash key
        try {
            pbkdf2Factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
            throw exception;
        }

        // generate key from combination of password, salt, and iterations
        PBEKeySpec generatedKey = new PBEKeySpec(password.toCharArray(), salt.getBytes(), randomIterations, 128);
        SecretKey key;
        try {
            // hashing part
            key = pbkdf2Factory.generateSecret(generatedKey);
        } catch (InvalidKeySpecException exception) {
            exception.printStackTrace();
            throw exception;
        }

        // get encoded key
        byte[] result = key.getEncoded();

        // convert encoded key to string
        String resultantPassword = new String(result, StandardCharsets.UTF_8);

        PasswordGenerationSpecifications specifications = new PasswordGenerationSpecifications();
        specifications.setHash(resultantPassword).setIterations(randomIterations).setSalt(salt);
        return specifications;
    }
}
