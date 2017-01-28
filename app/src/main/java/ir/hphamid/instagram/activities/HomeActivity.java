package ir.hphamid.instagram.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;

import ir.hphamid.instagram.R;
import ir.hphamid.instagram.fragments.ImageListFragment;

/**
 * Created on 1/24/17 at 2:49 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class HomeActivity extends AppCompatActivity{
    private FloatingActionButton share;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_fragment_container, new ImageListFragment());
        transaction.commit();
        share = (FloatingActionButton) findViewById(R.id.home_share);
        share.setRippleColor(Color.BLUE);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivityIntent = new Intent(HomeActivity.this, ShareImageActivity.class);
                startActivity(loginActivityIntent);
            }
        });
    }


}
