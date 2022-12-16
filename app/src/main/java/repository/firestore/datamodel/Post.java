package repository.firestore.datamodel;

import com.google.firebase.firestore.DocumentId;

public abstract class Post {

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

    public Post setContent(String content) {
        this.content = content;
        return this;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Post setDocumentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Post setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Post setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public Post setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
        return this;
    }

    public int getNumberUpvotes() {
        return numberUpvotes;
    }

    public Post setNumberUpvotes(int numberUpvotes) {
        this.numberUpvotes = numberUpvotes;
        return this;
    }

    public int getNumberComments() {
        return numberComments;
    }

    public Post setNumberComments(int numberComments) {
        this.numberComments = numberComments;
        return this;
    }

    public int getNumberTrophies() {
        return numberTrophies;
    }

    public Post setNumberTrophies(int numberTrophies) {
        this.numberTrophies = numberTrophies;
        return this;
    }

    public abstract void createPost();
}
