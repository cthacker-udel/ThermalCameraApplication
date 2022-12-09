package repository.firestore.manager;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Optional;
import java.util.function.Function;

/**
 * Manager class that sub-classes will inherit, which houses all important methods one might encounter when trying to
 * utilize firebase firestore within their application
 */
public class FirestoreManager implements iFirestoreManager {
    FirebaseFirestore client;
    Context ctx;
    public static FirestoreManager manager = null;
    public CollectionReference collection = null;
    public DocumentReference reference = null;
    public DocumentSnapshot snapshot;


    /**
     * Initializes the client with the FirebaseFirestore client
     *
     * @param client - The client passed into the constructor
     */
    public FirestoreManager(FirebaseFirestore client) {
        this.client = client;
        manager = this;
    }

    /**
     * Generates a new instance of the FirestoreManager class
     *
     * @param store - The FirebaseStore we are initializing the manager with
     * @return The instance of the class
     */
    public static FirestoreManager newInstance(FirebaseFirestore store) {
        if (manager == null) {
            manager = new FirestoreManager(store);
        }
        return manager;
    }

    public DocumentReference getReference() {
        return this.reference;
    }

    public DocumentSnapshot getSnapshot() {
        return this.snapshot;
    }

    @Override
    public FirestoreManager setCollection(String collection) {
        this.collection = this.client.collection(collection);
        return this;
    }

    @Override
    public FirestoreManager set(String field, String documentId) {
        Query docQuery = this.collection.whereEqualTo(field, documentId);
        Task<QuerySnapshot> docQuerySnapshot = docQuery.get();
        docQuerySnapshot.addOnSuccessListener(queryDocumentSnapshots -> {
           if (queryDocumentSnapshots.isEmpty()) {
               this.snapshot = null;
           } else {
               this.snapshot = queryDocumentSnapshots.getDocuments().get(0);
               this.reference = this.snapshot.getReference();
           }
        });
        return this;
    }

    @Override
    public <I> I query(String field, Class<I> theClass) {
        if (this.snapshot == null) {
            return null;
        } else {
            I value = this.snapshot.get(field, theClass);
            return value;
        }
    }

    @Override
    public <I> boolean update(String field, I updateValue) {
        if (this.snapshot == null || this.reference == null) {
            return false;
        }
        Task<Void> result = this.reference.update(field, updateValue);
        return result.isSuccessful();
    }

    public <I> boolean updateFunction(String field, Class<I> theClass, Function<I, I> func) {
        if (this.snapshot == null || this.reference == null) {
            return false;
        }
        Task<Void> result = this.reference.update(field, func.apply(query(field, theClass)));
        return result.isSuccessful();
    }

    @Override
    public boolean delete() {
        if (this.snapshot == null || this.reference == null) {
            return false;
        }
        Task<Void> result = this.reference.delete();
        if (result.isSuccessful()) {
            this.reference = null;
            this.snapshot = null;
        }
        return result.isSuccessful();
    }

    @Override
    public FirestoreManager setContext(Context ctx) {
        this.ctx = ctx;
        return this;
    }

    @Override
    public <I> boolean add(I value) {
        if (this.snapshot == null || this.reference == null) {
            return false;
        }
        Task<DocumentReference> result = this.collection.add(value);
        return result.isSuccessful();
    }

    @Override
    public <I> boolean add(I value, String successMessage, String successFailure) {
        if (this.snapshot == null || this.reference == null) {
            return false;
        }
        Task<DocumentReference> result = this.collection.add(value);
        if (this.ctx != null) {
            Toast.makeText(this.ctx, result.isSuccessful() ? successMessage : successFailure, Toast.LENGTH_SHORT).show();
        }
        return result.isSuccessful();
    }
}
