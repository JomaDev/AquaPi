package com.example.aquapi;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        final HomeFragment homeFragment= new HomeFragment();
        final EstadisticFragment estadisticFragment = new EstadisticFragment();
        final DevelopersFragment developersFragment = new DevelopersFragment();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.home){
                    setFragment(homeFragment);
                    return true;
                }else if (id == R.id.estadistics){
                    setFragment(estadisticFragment);
                    return true;
                }else if(id == R.id.developers){
                    setFragment(developersFragment);
                    return true;
                }
                return false;
            }
        });
        navigationView.setSelectedItemId(R.id.home);
    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}
