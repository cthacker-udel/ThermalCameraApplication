package repository.firestore.manager;

import android.content.Context;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
    public FirestoreManager set(String field, String documentId, OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        Query docQuery = this.collection.whereEqualTo(field, documentId);
        Task<QuerySnapshot> docQuerySnapshot = docQuery.get();
        docQuerySnapshot.addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
//        docQuerySnapshot.addOnSuccessListener(queryDocumentSnapshots -> {
//           if (queryDocumentSnapshots.isEmpty()) {
//               this.snapshot = null;
//           } else {
//               this.snapshot = queryDocumentSnapshots.getDocuments().get(0);
//               this.reference = this.snapshot.getReference();
//           }
//        }).addOnFailureListener(onFailureListener);
        return this;
    }

    @Override
    public <I> I query(String field, Class<I> theClass) {
        if (this.snapshot == null) {
            return null;
        } else {
            return this.snapshot.get(field, theClass);
        }
    }

    @Override
    public <I> void update(String field, I updateValue, final OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (this.snapshot == null || this.reference == null) {
            return;
        }
        this.reference.update(field, updateValue).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public <I> void updateFunction(String field, Class<I> theClass, Function<I, I> func, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        this.reference.update(field, func.apply(query(field, theClass))).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    @Override
    public void delete(OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (this.snapshot == null || this.reference == null) {
            return;
        }
        this.reference.delete().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    @Override
    public FirestoreManager setContext(Context ctx) {
        this.ctx = ctx;
        return this;
    }

    @Override
    public <I> void add(I value, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        if (this.snapshot != null || this.reference != null) {
            return;
        }
        this.collection.add(value).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }
}
