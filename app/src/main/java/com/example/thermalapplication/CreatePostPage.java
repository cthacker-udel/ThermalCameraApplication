package com.example.thermalapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.thermalapplication.ThermalPost;
import com.example.thermalapplication.post;
import com.example.thermalapplication.databinding.ActivityCreatePostPageBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import repository.firestore.datamodel.Comment;
import repository.firestore.datamodel.Post;

public class CreatePostPage extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 123;
    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int PICK_IMAGE_REQUEST = 22;
    private AppBarConfiguration appBarConfiguration;
    private ActivityCreatePostPageBinding binding;
    public String postId;
    public static Post newPost;

    private String imagePath;
    private Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreatePostPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_create_post_page);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                binding.imageView.setVisibility(View.VISIBLE);
                selectImage();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_create_post_page);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();

    }

     binding.takeimageButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            binding.imageView.setVisibility(View.VISIBLE);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
    }




    public void createPost() {
        post post;
        post = new ThermalPost(username);
        post.setPostId(postId);
        post.setPostAuthor(username);
        ArrayList<String> upVoteList = new ArrayList<>();
        post.setUpVotedBy(upVoteList);
        ArrayList<Comment> comments = new ArrayList<>();
        post.setComments(comments);

        DocumentReference document = FirebaseFirestore.getInstance().collection("Posts").document(String.valueOf(postId));

        Map<String, Object> currPost = new HashMap<>();
        currPost.put("postAuthor", username);
        currPost.put("postId", postId);
        currPost.put("upVoteCount", 0);
        currPost.put("upVoteBy", post.getUpVotedBy());
        currPost.put("comments",post.getComments());
        currPost.put("commentCount",0);
        currPost.put("photo",imagePath);
        document.set(currPost);
        

        setContentView(R.layout.activity_create_post_page);

    }

    public void selectImage()
    {
        Intent intent = new Intent(); // intent = screen
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Bitmap newImage = (Bitmap) data.getExtras().get("data");
            binding.imageView.setImageBitmap(newImage);
            Uri filePath = data.getData();
            image = filePath;
            imagePath = filePath.toString();



        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            image = filePath;
            imagePath = filePath.toString();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContext().getContentResolver(),
                                filePath);
                binding.imageView.setImageBitmap(bitmap);            }

            // when image selction fails
            //todo: write an exception that is printed when user denies access to gallery
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String postId) {
        //creates a profile image path for the newly uploaded image
        if(image!=null) {
            imagePath = postId + "_image";
        }
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Uploading...");
        if(image!= null) {
            progressDialog.show();
            //adds image to the database
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ imagePath);
            ref.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this.getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this.getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

}