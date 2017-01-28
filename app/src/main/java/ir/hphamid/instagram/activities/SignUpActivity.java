package ir.hphamid.instagram.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import ir.hphamid.instagram.reqAndres.SignupRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends Activity {

    Button loginButton;
    Button signUpButton;
    EditText email;
    EditText fullName;
    EditText password;
    EditText passwordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signUpButton = (Button) findViewById(R.id.signup_submit);
        loginButton = (Button) findViewById(R.id.signup_login);
        email = (EditText) findViewById(R.id.signup_email);
        fullName = (EditText) findViewById(R.id.signup_name);
        password = (EditText) findViewById(R.id.signup_password);
        passwordRepeat = (EditText) findViewById(R.id.signnup_password_repeat);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                finish();
                startActivity(loginActivityIntent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser();
            }
        });
    }

    protected void signupUser(){
        final String  passwords = password.getText().toString();
        final String passwordRepeats = passwordRepeat.getText().toString();
        final String emails = email.getText().toString();
        final String fullNames = fullName.getText().toString();

        if(!passwords.equals(passwordRepeats)){
            Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            return;
        }
        SignupRequest requestJson = new SignupRequest();
        requestJson.setEmail(emails);
        requestJson.setFullName(fullNames);
        requestJson.setPassword(passwords);
        RequestBody body = RequestBody.create(HttpHelper.JSON, new Gson().toJson(requestJson));
        Request request = new Request.Builder()
                .post(body)
                .url(HttpAddresses.RegisterAddress).build();
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        HttpHelper.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.e("error", responseString);
                final LoginResponse res = new Gson().fromJson(responseString, LoginResponse.class);
                if(res.isSuccess()){
                    //save login data
                    LoginHelper.saveLoginData(SignUpActivity.this, res, emails);

                    //go to home page
                    Intent signupActivityIntent = new Intent(SignUpActivity.this, HomeActivity.class);
                    finish();
                    startActivity(signupActivityIntent);
                    dialog.dismiss();
                }else{
                    SignUpActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUpActivity.this, "Signup Failed ", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
