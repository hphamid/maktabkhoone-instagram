package ir.hphamid.instagram.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.hphamid.instagram.R;

public class LoginActivity extends Activity {
    public final static String PrefName = "login";
    public final static String UserName_key = "userName";

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
        String password = loginPassword.getText().toString();
        if(email.length() <= 0){
            Toast.makeText(this, "email is empty", Toast.LENGTH_SHORT).show();
        }
        saveUserName(email);
        Intent loginActivityIntent = new Intent(LoginActivity.this, HomeActivity.class);
        finish();
        startActivity(loginActivityIntent);

    }

    private void saveUserName(String email){
        SharedPreferences preferences = getSharedPreferences(PrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UserName_key, email);
        editor.commit();
    }




}
