package com.example.loginintegrationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.loginintegrationapp",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch (PackageManager.NameNotFoundException e) {
//        }
//        catch (NoSuchAlgorithmException e) {
//        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleProfileActivity.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = GoogleProfileActivity.mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        findViewById(R.id.fb_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fbIntent = new Intent(getApplicationContext(), FacebookProfileActivity.class);
                startActivity(fbIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            GoogleProfileActivity.mAccount = account;
            startActivity(new Intent(this, GoogleProfileActivity.class));
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {
        try {
            GoogleSignInAccount account = result.getResult(ApiException.class);

            if(account != null){
                GoogleProfileActivity.mAccount = account;
                startActivity(new Intent(this, GoogleProfileActivity.class));
                finish();
            }
        } catch (ApiException e) {
            Log.w("MainActivity", "signInResult:failed code=" + e.getStatusCode());
        }
//            if (result.isSuccess()){
//                goToProfilePage();
//            }
//            else {
//                Toast.makeText(getApplicationContext(), "Sign in attempt failed", Toast.LENGTH_SHORT);
//            }
    }

//    private void goToProfilePage() {
//        Intent intent = new Intent(MainActivity.this, GoogleProfileActivity.class);
//        startActivity(intent);
//    }

//    public void facebookLogin() {
//        Log.i("MainActivity", "----Inside facebookLogin method----");
//        loginManager = LoginManager.getInstance();
//        callbackManager = CallbackManager.Factory.create();
//
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                GraphRequest graphRequest = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                if(object != null) {
//                                    try {
//                                        Intent main = new Intent(MainActivity.this, FacebookActivity.class);
//                                        main.putExtra("name", object.getString("first_name"));
//                                        main.putExtra("surname", object.getString("last_name"));
//                                        main.putExtra("email", object.getString("email"));
////                                        main.putExtra("id", object.getString("id"));
//                                        String image_url = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=normal";
//                                        main.putExtra("imageUrl", image_url);
//                                        startActivity(main);
//
//                                        disconnectFromFacebook();
//                                    }
//                                    catch (JSONException | NullPointerException e){
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
//            }
//
//            @Override
//            public void onCancel() {
//                Log.v("LogInScreen", "---onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.v("LogInScreen", "---onError: " + error.getMessage());
//            }
//        });
//    }
//
//    public void disconnectFromFacebook() {
//        if (AccessToken.getCurrentAccessToken() == null) {
//            return; // already logged out
//        }
//
//        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
//                            null, HttpMethod.DELETE, new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse graphResponse) {
//                        LoginManager.getInstance().logOut();
//                    }
//                }).executeAsync();
//    }
}

//207939585319-h6n20c21tn7rund64vcglp4lsorlnpk3.apps.googleusercontent.com MCvfHjIaSSEOOHppRDSXleMW