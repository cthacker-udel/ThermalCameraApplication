package repository.firestore.manager;

import android.os.Build;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.ArrayList;

import repository.firestore.contracts.PostFirestoreDbContract;
import repository.firestore.datamodel.Post;

public final class PostsFirestoreManager {
    FirebaseFirestore client;
    public static PostsFirestoreManager postsFirestoreManager;

    public PostsFirestoreManager(FirebaseFirestore client) {
        this.client = client;
        postsFirestoreManager = this;
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

    public static PostsFirestoreManager newInstance(FirebaseFirestore store) {
        if (postsFirestoreManager == null) {
            postsFirestoreManager = new PostsFirestoreManager(store);
        }
        return postsFirestoreManager;
    }
}
