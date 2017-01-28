package ir.hphamid.instagram.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import ir.hphamid.instagram.LoginHelper;

/**
 * Created on 1/26/17 at 1:47 AM.
 * Project: instagram
 *
 * @author hamid
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userName = getUserName();
        if (userName != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }


    private String getUserName() {
        return LoginHelper.getEmail(this);
    }
}

