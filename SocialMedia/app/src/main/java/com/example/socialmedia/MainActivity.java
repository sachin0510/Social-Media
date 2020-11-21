package com.example.socialmedia;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ImageView imageView;
    private TextView textView_name;
    private TextView textView_mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginButton loginButton = findViewById(R.id.login_button);
        imageView = findViewById(R.id.profile_pic);
        textView_name = findViewById(R.id.profile_name);
        textView_mail = findViewById(R.id.profile_mail);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setPermissions(Collections.singletonList("user_gender, user_friends, email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("demo", "Login Successful");
            }

            @Override
            public void onCancel() {
                Log.d("demo", "Login Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("demo", "Login Error");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                (object, response) -> {
                    Log.d("demo", object.toString());

                    try {
                        String name = object.getString("name");
                        String mail = object.getString("email");

                        textView_name.setText(name);
                        textView_mail.setText(mail);
                        String pic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                        Picasso.get().load(pic).into(imageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id, first_name, last_name, picture, email");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken == null) {
                LoginManager.getInstance().logOut();
                textView_name.setText("");
                textView_mail.setText("");
                imageView.setImageResource(R.drawable.login_icon);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

}