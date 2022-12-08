package repository.firestore.manager;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.example.thermalapplication.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;
import com.google.firebase.firestore.local.QueryResult;

import java.util.ArrayList;

import repository.firestore.contracts.PostFirestoreDbContract;
import repository.firestore.contracts.UserFirestoreDbContract;
import repository.firestore.datamodel.Post;

public final class PostsFirestoreManager {
    FirebaseFirestore client;
    Context ctx;
    public static PostsFirestoreManager postsFirestoreManager;

    public PostsFirestoreManager(FirebaseFirestore client) {
        this.client = client;
        postsFirestoreManager = this;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public Post convertSnapshotToPost(DocumentSnapshot snapshot) {
        return new Post(snapshot.get(PostFirestoreDbContract.FIELD_AUTHOR, String.class), snapshot.get(PostFirestoreDbContract.FIELD_POST_IMAGE_URL, String.class));
    }

    /**
     *
     * @param x - The number of posts to take from the top
     */
    public Post[] fetchXRecentPosts(int x) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ArrayList<Post> posts = new ArrayList<>();
            this.client.collection(PostFirestoreDbContract.COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    queryDocumentSnapshots.getDocuments().stream().limit(x).forEach(f -> posts.add(convertSnapshotToPost(f)));
                }
            });
        }
        return new Post[]{};
    }

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
        return new Post[]{};
    }

    public void removePost(String postId) {
        DocumentReference refDoc = this.client.collection(PostFirestoreDbContract.COLLECTION_NAME).document(postId);
        refDoc.delete();
    }

    public void addPost(String postImageUrl, String author, String content) {
        Task<QuerySnapshot> authorDoc = this.client.collection(UserFirestoreDbContract.COLLECTION_NAME).whereEqualTo(UserFirestoreDbContract.FIELD_USERNAME, author).get();
        authorDoc.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Post constructedPost = new Post(author, postImageUrl);
                constructedPost.setContent(content);
                constructedPost.setAuthorId(queryDocumentSnapshots.getDocuments().get(0).get(UserFirestoreDbContract.USERNAME_ID).toString());
                Task<DocumentReference> addPostRef = client.collection(PostFirestoreDbContract.COLLECTION_NAME).add(constructedPost);
                addPostRef.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void editPost(String postId, String partialContent) {
        Query postReference = this.client.collection(PostFirestoreDbContract.COLLECTION_NAME).whereEqualTo(PostFirestoreDbContract.DOCUMENT_ID, postId);
        postReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentReference postReference = client.collection(PostFirestoreDbContract.COLLECTION_NAME).document();
                postReference.update(PostFirestoreDbContract.FIELD_CONTENT, partialContent);
            }
        });
    }

    public void addUpvote(String postId) {
        Query postReference = this.client.collection(PostFirestoreDbContract.COLLECTION_NAME).whereEqualTo(PostFirestoreDbContract.DOCUMENT_ID, postId);
        postReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            }
        });
    }

    public static PostsFirestoreManager newInstance(FirebaseFirestore store) {
        if (postsFirestoreManager == null) {
            postsFirestoreManager = new PostsFirestoreManager(store);
        }
        return postsFirestoreManager;
    }
}
