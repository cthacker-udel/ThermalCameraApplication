package com.example.thermalapplication;

import java.util.ArrayList;

import repository.firestore.datamodel.Comment;
import repository.firestore.datamodel.Post;


public abstract class post {
    private String postAuthor;
    private String photo;
    private Integer upVoteCount;
    private ArrayList<String> upVotedBy;
    private String postId;
    private ArrayList<Comment> comments;
    private int commentCount;

    public abstract void createPost();

    public post() {
        this.upVotedBy = new ArrayList<String>();
    }

    public post(String postAuthor){
        this.postAuthor = postAuthor;

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public ArrayList<String> getUpVotedBy() {
        return this.upVotedBy;
    }

    public void addToUpVotedBy(String username){
        this.upVotedBy.add(username);
    }

    public void removeFromUpVotedBy(String username){
        this.upVotedBy.remove(username);
    }
    public void setUpVotedBy(ArrayList<String> upVotedBy) {
        this.upVotedBy = upVotedBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }
    public void removeComment(Comment comment){
        this.comments.remove(comment);
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public int getUpVoteCount() {
        return upVoteCount;
    }

    public void setLikeCount(int upVoteCount) {
        this.upVoteCount = upVoteCount;
    }
}

