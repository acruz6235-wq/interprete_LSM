package com.example.interpretels.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.interpretels.R;
import com.example.interpretels.database.DatabaseSeeder;
import com.example.interpretels.fragments.AjustesFragment;
import com.example.interpretels.fragments.HistorialFragment;
import com.example.interpretels.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Llenar la base de datos al iniciar
        DatabaseSeeder.seedDatabase(this);

        // Configurar Bottom Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Cargar fragment inicial (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_historial) {
            selectedFragment = new HistorialFragment();
        } else if (itemId == R.id.nav_ajustes) {
            selectedFragment = new AjustesFragment();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            return true;
        }

        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}