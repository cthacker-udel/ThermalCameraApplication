package repository.firestore.manager;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import repository.firestore.contracts.PostFirestoreDbContract;
import repository.firestore.contracts.UserFirestoreDbContract;
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
    }

    /**
     *
     * @param snapshot
     * @return
     */
    public Post convertSnapshotToPost(DocumentSnapshot snapshot) {
        return new Post(snapshot.get(PostFirestoreDbContract.FIELD_AUTHOR, String.class), snapshot.get(PostFirestoreDbContract.FIELD_POST_IMAGE_URL, String.class));
    }

    /**
     *
     * @param x - The number of posts to take from the top
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
     *
     * @return
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
     *
     *
     * @param postImageUrl
     * @param author
     * @param content
     * @return
     */
    public boolean addPost(String postImageUrl, String author, String content) {
        return super.add(new Post(author, postImageUrl).setContent(content), "Added a post successfully!", "Failed to add a post");
    }

    /**
     *
     *
     * @param content
     * @return
     */
    public boolean editPost(String field, String documentId, Post content) {
        super.set(PostFirestoreDbContract.DOCUMENT_ID, documentId);
        return super.update(field, content);
    }

    /**
     * Increment the upvote field of the document
     *
     * @param documentId - The id of the document to update
     */
    public boolean addUpvote(String documentId) {
        super.set(PostFirestoreDbContract.DOCUMENT_ID, documentId);
        return super.updateFunction(PostFirestoreDbContract.FIELD_NUMBER_UPVOTES, Integer.class, (Integer value) -> value + 1);
    }

    /**
     * Increments the number of comments with the post associated with the documentId
     *
     * @param documentId - The id of the document to update
     */
    public boolean addNumberComments(String documentId) {
        return super.updateFunction(PostFirestoreDbContract.FIELD_NUMBER_COMMENTS, Integer.class, (Integer value) -> value + 1);
    }

    /**
     *
     * @param postId
     * @return
     */
    public boolean addNumberTrophies(String postId) {
        return super.updateFunction(PostFirestoreDbContract.FIELD_NUMBER_TROPHIES, Integer.class, (Integer value) -> value + 1);
    }

    /**
     *
     * @param url
     * @return
     */
    public boolean addPostImageUrl(String url) {
        return super.update(PostFirestoreDbContract.FIELD_POST_IMAGE_URL, url);
    }


}
