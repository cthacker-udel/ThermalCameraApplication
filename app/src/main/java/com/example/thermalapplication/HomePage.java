package com.example.thermalapplication;

import android.app.ActionBar;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.thermalapplication.databinding.ActivityHomePageBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.stream.Stream;

import repository.firestore.datamodel.Post;
import repository.firestore.manager.PostsFirestoreManager;

public class HomePage extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityHomePageBinding binding;
    FirebaseFirestore db;
    PostsFirestoreManager postsFirestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.db = FirebaseFirestore.getInstance();
        this.postsFirestoreManager = new PostsFirestoreManager(this.db);
        Post[] posts = this.postsFirestoreManager.fetchAllPosts();
        ScrollView homepageScrollView = (ScrollView) findViewById(R.id.homePageScrollView);
        // Streams all posts and adds each one
        if (posts.length == 0) {
            TextView noPostsTextView = new TextView(this);
            noPostsTextView.setText("No posts to report, you should create one!");
            noPostsTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            homepageScrollView.addView(noPostsTextView);
        } else {
            Stream.of(posts).forEach(e -> {
                setContentView(R.layout.activity_home_page);
                TextView textView = new TextView(this);
                textView.setText("Hello world");
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                homepageScrollView.addView(textView);
            });
        }
    }

}