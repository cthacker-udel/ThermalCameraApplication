package repository.firestore.datamodel;

import com.google.firebase.firestore.DocumentId;

public class Comment {

    @DocumentId
    private String commentId;

    @DocumentId
    private String authorId;
    private String author;
    private String commentValue;

    public Comment() {}
    public Comment(String author, String commentValue) {
        this.author = author;
        this.commentValue = commentValue;
    }

}
