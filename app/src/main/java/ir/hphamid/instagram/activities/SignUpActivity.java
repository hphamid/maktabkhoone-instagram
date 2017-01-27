package ir.hphamid.instagram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ir.hphamid.instagram.R;

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
                Intent homeActivityIntent = new Intent(SignUpActivity.this, HomeActivity.class);
                finish();
                startActivity(homeActivityIntent);
            }
        });
    }
}
