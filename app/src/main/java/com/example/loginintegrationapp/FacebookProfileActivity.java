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
        LoginButton logInButton = findViewById(R.id.fb_button);
        profilePictureView = (ProfilePictureView)findViewById(R.id.fb_profile_image);

        callbackManager = CallbackManager.Factory.create();
        logInButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        if(!loggedOut)
            facebookLogin(AccessToken.getCurrentAccessToken());

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
                                String first_name = object.getString("first_name");
                                String last_name = object.getString("last_name");
                                String email = object.getString("email");
                                String id = object.getString("id");

                                nameView.setText(first_name + " " + last_name);
                                Log.i("ProfileDetails", first_name + " " + last_name);

                                emailView.setText(email);
                                Log.i("ProfileDetails", email);

                                profilePictureView.setProfileId(id);
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
}
