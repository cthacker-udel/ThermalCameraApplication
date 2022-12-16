package repository.firestore.manager;

import android.content.Context;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.function.Function;

/**
 * Interface mapping all the methods each child should implement
 */
public interface iFirestoreManager {

    /**
     * Sets the internal collection within the FirestoreManager
     *
     * @param collection - The collection to set
     * @return The instance to allow for method chaining
     */
    FirestoreManager setCollection(String collection);

    /**
     * Queries the current document stored within the instance of the FirestoreManager
     *
     * @param field - The field to find
     * @param documentId - The id of the document
     * @return The current document
     */
    FirestoreManager set(String field, String documentId, OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener);

    /**
     * Queries the current document stored within the instance of the FirestoreManager
     *
     * @param field - The field of the document we are querying
     * @param theClass - The class we are expecting to be returned
     * @param <I> - The type we are returning, which is specified by the class
     * @return The value we are querying for
     */
    <I> I query(String field, Class<I> theClass);

    /**
     * Updates the current document instance of the FirestoreManager
     *
     * @param field - The field to update
     * @param updateValue - The value we are replacing the field with
     * @param onSuccessListener - The listener that fires when the operation was successful
     * @param onFailureListener - The listener that fires when the operation was a failure
     * @param <I> The type of update value we are supplying
     * @return Whether the update was successful or not
     */
    <I> void update(String field, I updateValue, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener);

    /**
     * Updates the current document instance of the FirestoreManager
     *
     * @param field - The field to update
     * @param theClass - The type of field to update
     * @param updateFunction - The function to update the values
     * @param onSuccessListener - The listener that fires when the operation was successful
     * @param onFailureListener - The listener that fires when the operation was a failure
     * @param <I> - The type of the field
     * @return - Whether the field is updated or not
     */
    <I> void updateFunction(String field, Class<I> theClass, Function<I, I> updateFunction, OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener);

    /**
     * Adds an entry to the database
     *
     * @param value - The value we are adding to the database
     * @param onSuccessListener - The listener that fires when the operation was successful
     * @param onFailureListener - The listener that fires when the operation was a failure
     * @param <I> - The type of value being added
     */
    <I> void add(I value, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener);

    /**
     * Deletes the current document instance within the FirestoreManager
     *
     * @param onSuccessListener - The listener that fires when the operation was successful
     * @param onFailureListener - The listener that fires when the operation was a failure
     * @return Whether the delete was successful or not
     */
    void delete(OnSuccessListener<? super Void> onSuccessListener, OnFailureListener onFailureListener);

    /**
     * Sets the context of the class implementing this interface
     * @param ctx - The context we are setting
     */
    FirestoreManager setContext(Context ctx);

}
