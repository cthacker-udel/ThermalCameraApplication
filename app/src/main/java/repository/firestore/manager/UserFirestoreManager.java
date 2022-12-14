package repository.firestore.manager;

import com.google.firebase.firestore.FirebaseFirestore;

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

public final class UserFirestoreManager extends FirestoreManager {
    /**
     * Initializes the client with the FirebaseFirestore client
     *
     * @param client - The client passed into the constructor
     */
    public UserFirestoreManager(FirebaseFirestore client) {
        super(client);
    }

    public User findUser(String username) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
        return super.snapshot != null ? new User(super.snapshot) : null;
    }

    public boolean deleteUser(String username) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
        return super.delete();
    }

    public boolean updateUser(String username, String field, String value) {
        super.set(UserFirestoreDbContract.USERNAME_ID, username);
        return super.update(field, value);
    }

    public AddUserStatus addUser(String username, String password) {
        AddUserStatus addingUserStatus = new AddUserStatus();
        boolean alreadyExists = this.findUser(username) != null;
        addingUserStatus.setAlreadyExists(alreadyExists);
        if (!alreadyExists) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            addingUserStatus.setSuccess(super.add(newUser));
        }
        return addingUserStatus;
    }
}
