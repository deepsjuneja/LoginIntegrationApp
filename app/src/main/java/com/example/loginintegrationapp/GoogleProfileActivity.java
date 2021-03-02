package com.example.loginintegrationapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.InputStream;

public class GoogleProfileActivity extends AppCompatActivity implements View.OnClickListener{
    static GoogleSignInClient mGoogleSignInClient;
    static GoogleSignInAccount mAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googleprofile);

        if(mAccount == null) {
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            TextView name = findViewById(R.id.name);
            name.setText(mAccount.getDisplayName());
            TextView email = findViewById(R.id.email);
            email.setText(mAccount.getEmail());
            TextView id = findViewById(R.id.profile_id);
            id.setText(mAccount.getId());

            ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
            try {
                new LoadImage(profileImage).execute(mAccount.getPhotoUrl().toString());
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "Image not found!", Toast.LENGTH_LONG).show();
            }

            findViewById(R.id.signout_button).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signout_button :
                signOut();
                break;
        }
    }

    private void signOut() {
        Log.i("GoogleProfileActivity", "Inside signOut method");

        if (mGoogleSignInClient != null) {
            Log.i("GoogleProfileActivity", "Inside if clause of signOut method");
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(GoogleProfileActivity.this, MainActivity.class));
                    finish();
                }
            });
        }
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;

        public LoadImage(ImageView imageView) {
            this.mImageView = imageView;
            Toast.makeText(getApplicationContext(), "Loading profile image...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bImage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bImage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("LoadImage", e.getMessage());
            }
            return bImage;
        }

        protected void onPostExecute(Bitmap profileImage) {
            mImageView.setImageBitmap(profileImage);
        }
    }
}
