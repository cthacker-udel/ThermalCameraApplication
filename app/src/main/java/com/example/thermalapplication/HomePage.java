package com.example.thermalapplication;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import com.example.thermalapplication.usersettings.UserSettings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
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
    private ConstraintLayout layout;
    FirebaseFirestore db;
    PostsFirestoreManager postsFirestoreManager;
    ImageView userSettingsIcon;
    ImageView homePageCameraIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.layout = (ConstraintLayout)findViewById(R.id.homePageInclude);
        this.userSettingsIcon = findViewById(R.id.userSettingsIcon);
        this.homePageCameraIcon = findViewById(R.id.homePageCameraButton);
        this.db = FirebaseFirestore.getInstance();
        this.postsFirestoreManager = new PostsFirestoreManager(this.db);
        Post[] posts = new Post[] {}; //this.postsFirestoreManager.fetchAllPosts();
        ScrollView homepageScrollView = (ScrollView) this.layout.findViewById(R.id.homePageScrollView);
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

        this.userSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, UserSettings.class));
            }
        });

        this.homePageCameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, FlirActivity.class));
            }
        });
    }

}