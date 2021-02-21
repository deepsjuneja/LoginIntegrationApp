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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;

public class FacebookProfileActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private TextView nameView, emailView;
    private ImageView profilePhoto;
    private ProfilePictureView profilePictureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebookprofile);

        nameView = findViewById(R.id.fb_username);
        emailView = findViewById(R.id.fb_email);
//        profilePhoto = findViewById(R.id.fb_profile_image);
        LoginButton logInButton = findViewById(R.id.fb_button);
        profilePictureView = (ProfilePictureView)findViewById(R.id.fb_profile_image);

//        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        logInButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        if(!loggedOut)
            facebookLogin(AccessToken.getCurrentAccessToken());
//        facebookLogin();
//        AppEventsLogger.activateApp(this);
//
//        logInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logInButton.setReadPermissions(Arrays.asList("email", "public_profile"));
//            }
//        });

        logInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
//        Bundle inBundle = getIntent().getExtras();
//        String name = inBundle.get("name").toString();
//        String surname = inBundle.get("surname").toString();
//        String email = inBundle.get("email").toString();
//        String imageUrl = inBundle.get("imageUrl").toString();
//
//        TextView nameView = (TextView)findViewById(R.id.fb_username);
//        nameView.setText("" + name + " " + surname);
//
//        TextView emailView = (TextView)findViewById(R.id.fb_email);
//        emailView.setText(email);
//
//        ImageView profilePhoto = (ImageView) findViewById(R.id.fb_profile_image);
//        new LoadImage(profilePhoto).execute(imageUrl);

//        findViewById(R.id.fb_signout_button).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                nameView.setText("");
                emailView.setText("");
                profilePhoto.setImageResource(0);
                Toast.makeText(FacebookProfileActivity.this, "Signed out successfully", Toast.LENGTH_LONG).show();
            }
            else {
                facebookLogin(currentAccessToken);
            }
        }
    };

    private void facebookLogin(AccessToken newAccessToken) {
        Log.i("MainActivity", "----Inside facebookLogin method----");

        GraphRequest graphRequest = GraphRequest.newMeRequest(
                newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if(object != null) {
                            try {
//                                        Intent main = new Intent(MainActivity.this, FacebookActivity.class);
//                                        main.putExtra("name", object.getString("first_name"));
//                                        main.putExtra("surname", object.getString("last_name"));
//                                        main.putExtra("email", object.getString("email"));
////                                        main.putExtra("id", object.getString("id"));
//                                        String image_url = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=normal";
//                                        main.putExtra("imageUrl", image_url);
//                                        startActivity(main);
//
//                                        disconnectFromFacebook()
                                String first_name = object.getString("first_name");
                                String last_name = object.getString("last_name");
                                String email = object.getString("email");
                                String id = object.getString("id");
//                                Log.i("ProfileDetails", id);
//                                String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                                nameView.setText(first_name + " " + last_name);
                                Log.i("ProfileDetails", first_name + " " + last_name);

                                emailView.setText(email);
                                Log.i("ProfileDetails", email);

                                profilePictureView.setProfileId(id);
//                                try {
//                                    new LoadImage(profilePhoto).execute(image_url);
//                                } catch (NullPointerException e) {
//                                    Toast.makeText(getApplicationContext(), "Image not found!", Toast.LENGTH_LONG).show();
//                                }
//                              disconnectFromFacebook();
                            }
                            catch (JSONException | NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name, last_name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(FacebookProfileActivity.this, MainActivity.class));
                finish();
            }
        }).executeAsync();
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

        protected void onPostExecute(Bitmap profilePhoto) {
            mImageView.setImageBitmap(profilePhoto);
        }
    }
}
