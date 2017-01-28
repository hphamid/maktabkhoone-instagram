package ir.hphamid.instagram.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import ir.hphamid.instagram.HttpAddresses;
import ir.hphamid.instagram.HttpHelper;
import ir.hphamid.instagram.LoginHelper;
import ir.hphamid.instagram.R;
import ir.hphamid.instagram.reqAndres.LoginRequest;
import ir.hphamid.instagram.reqAndres.LoginResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {

    Button loginButton;
    Button signUpButton;
    EditText loginEmail;
    EditText loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_constraint);
        loginButton = (Button) findViewById(R.id.login_constraint_login);
        signUpButton = (Button) findViewById(R.id.login_constraint_register);
        loginEmail = (EditText) findViewById(R.id.login_constraint_email);
        loginPassword = (EditText) findViewById(R.id.login_constraint_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                finish();
                startActivity(loginActivityIntent);
            }
        });
    }

    private void loginUser(){
        final String email = loginEmail.getText().toString();
        final String password = loginPassword.getText().toString();
        LoginRequest requestJson = new LoginRequest();
        requestJson.setUsername(email);
        requestJson.setPassword(password);
        RequestBody body = RequestBody.create(HttpHelper.JSON, new Gson().toJson(requestJson));
        Request request = new Request.Builder()
                .post(body)
                .url(HttpAddresses.LoginAddress).build();
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        HttpHelper.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                LoginResponse res = new Gson().fromJson(responseString, LoginResponse.class);
                if(res.isSuccess()){
                    //save login data
                    LoginHelper.saveLoginData(LoginActivity.this, res, email);

                    //go to home page
                    Intent loginActivityIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    finish();
                    startActivity(loginActivityIntent);
                    dialog.dismiss();
                }else{
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }


}
