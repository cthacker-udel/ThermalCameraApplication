package repository.firestore.manager;

import android.os.Build;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.function.Function;

import repository.firestore.contracts.PostFirestoreDbContract;
import repository.firestore.datamodel.Post;

/**
 *
 */
public final class PostsFirestoreManager extends FirestoreManager {


    /**
     * Initializes the client with the FirebaseFirestore client
     *
     * @param client - The client passed into the constructor
     */
    public PostsFirestoreManager(FirebaseFirestore client) {
        super(client);
        super.setCollection("posts");
    }

    /**
     * Converts a documentsnapshot to a Post
     *
     * @param snapshot - The document snapshot
     * @return The converted Post
     */
    public Post convertSnapshotToPost(DocumentSnapshot snapshot) {
        return new Post(snapshot.get(PostFirestoreDbContract.FIELD_AUTHOR, String.class), snapshot.get(PostFirestoreDbContract.FIELD_POST_IMAGE_URL, String.class));
    }

    /**
     * Fetches x posts from the database
     *
     * @param x - The number of posts to take from the top
     * @return x posts from the database
     */
    public Post[] fetchXRecentPosts(int x) {
        ArrayList<Post> posts = new ArrayList<>();
        this.client.collection(PostFirestoreDbContract.COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.getDocuments().stream().limit(x).forEach(f -> posts.add(convertSnapshotToPost(f)));
            }
        });
        return posts.toArray(Post[]::new);
    }

    /**
     * Fetches all posts from the database
     *
     * @return All posts within the database
     */
    public Post[] fetchAllPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        this.client.collection(PostFirestoreDbContract.COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    queryDocumentSnapshots.getDocuments().forEach(e -> posts.add(convertSnapshotToPost(e)));
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return posts.toArray(Post[]::new);
        }
        return (Post[]) posts.toArray();
    }

    /**
     * Adds a post to the database with the given parameters
     *
     * @param postImageUrl - The post image url that is required when creating a post
     * @param author - The author of the post
     * @param content - The content of the post
     * @return Whether or not the addition of the post was successful
     */
    public void addPost(String postImageUrl, String author, String content, final OnSuccessListener<DocumentReference> onSuccessListener, final OnFailureListener onFailureListener) {
        super.add(new Post(author, postImageUrl).setContent(content), onSuccessListener, onFailureListener);
    }

    /**
     * Edit the post field of the document
     *
     * @param field - The field we are updating
     * @param documentId - The id of the document to update
     * @param content - The content we are pushing to the post
     * @return Whether or not the edit was successful
     */
    public boolean editPost(String field, String documentId, Post content) {
        super.set(PostFirestoreDbContract.DOCUMENT_ID, documentId);
        return super.update(field, content);
    }

    /**
     * Increment the upvote field of the document
     *
     * @param documentId - The id of the document to update
     * @param upvoteFunction - The function we will apply to the upvote count when we encounter it in the database
     * @return Whether or not the edit was successful
     */
    public boolean editUpvote(String documentId, Function<Integer, Integer> upvoteFunction) {
        super.set(PostFirestoreDbContract.DOCUMENT_ID, documentId);
        return super.updateFunction(PostFirestoreDbContract.FIELD_NUMBER_UPVOTES, Integer.class, upvoteFunction);
    }

    /**
     * Increments the number of comments with the post associated with the documentId
     *
     * @param documentId - The id of the document to update
     * @param commentsFunction - The function we will apply to the comments value when we find it within the database
     * @return Whether or not the edit was successful
     */
    public boolean editNumberComments(String documentId, Function<Integer, Integer> commentsFunction) {
        super.set(PostFirestoreDbContract.DOCUMENT_ID, documentId);
        return super.updateFunction(PostFirestoreDbContract.FIELD_NUMBER_COMMENTS, Integer.class, commentsFunction);
    }

    /**
     * Edits the # of trophies a post has
     *
     * @param documentId - The ID of the entry in the database
     * @param updateFunction - The function that will be invoked upon the # of trophies when the document is found in the database
     * @return Whether or not the edit was successful
     */
    public boolean editNumberTrophies(String documentId, Function<Integer, Integer> updateFunction) {
        super.set(PostFirestoreDbContract.DOCUMENT_ID, documentId);
        return super.updateFunction(PostFirestoreDbContract.FIELD_NUMBER_TROPHIES, Integer.class, updateFunction);
    }

    /**
     * Adds a post url to an post
     *
     * @param url - The post image url
     * @return Whether or not the addition of the post url was successful
     */
    public boolean addPostImageUrl(String url) {
        return super.update(PostFirestoreDbContract.FIELD_POST_IMAGE_URL, url);
    }


}
