package repository.firestore.manager;

import android.content.Context;

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
    FirestoreManager set(String field, String documentId);

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
     * @param <I> The type of update value we are supplying
     * @return Whether the update was successful or not
     */
    <I> boolean update(String field, I updateValue);

    /**
     * Updates the current document instance of the FirestoreManager
     *
     * @param field - The field to update
     * @param theClass - The type of field to update
     * @param updateFunction - The function to update the values
     * @param <I> - The type of the field
     * @return - Whether the field is updated or not
     */
    <I> boolean updateFunction(String field, Class<I> theClass, Function<I, I> updateFunction);

    /**
     * Adds an entry to the database
     *
     * @param value - The value we are adding to the database
     * @param <I> - The type of value being added
     * @return Whether or not the
     */
    <I> boolean add(I value);

    /**
     * Adds an entry to the database, with the associated message being displayed to the application
     *
     * @param value - The value we are adding to the database
     * @param toastMessageSuccess - The message we are displaying to the user when the entry is added successfully
     * @param toastMessageFailure - The message we are displaying to the user when the entry is unsuccessfully added
     * @param <I> - The type of value we are adding
     * @return Whether the insertion was successful or not
     */
    <I> boolean add(I value, String toastMessageSuccess, String toastMessageFailure);

    /**
     * Deletes the current document instance within the FirestoreManager
     *
     * @return Whether the delete was successful or not
     */
    boolean delete();

    /**
     * Sets the context of the class implementing this interface
     * @param ctx - The context we are setting
     */
    void setContext(Context ctx);

}