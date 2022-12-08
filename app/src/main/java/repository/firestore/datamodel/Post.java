package repository.firestore.datamodel;

import com.google.firebase.firestore.DocumentId;

public class Post {

    @DocumentId
    private String documentId;

    @DocumentId
    private String authorId;

    private String author;

    private String postImageUrl;
    private String content;

    private int numberUpvotes;
    private int numberComments;
    private int numberTrophies;

    public Post() {}
    public Post(String author, String postImageUrl) {
        this.author = author;
        this.postImageUrl = postImageUrl;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public int getNumberUpvotes() {
        return numberUpvotes;
    }

    public void setNumberUpvotes(int numberUpvotes) {
        this.numberUpvotes = numberUpvotes;
    }

    public int getNumberComments() {
        return numberComments;
    }

    public void setNumberComments(int numberComments) {
        this.numberComments = numberComments;
    }

    public int getNumberTrophies() {
        return numberTrophies;
    }

    public void setNumberTrophies(int numberTrophies) {
        this.numberTrophies = numberTrophies;
    }
}
