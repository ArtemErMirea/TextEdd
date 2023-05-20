package com.example.textedd.presentation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.textedd.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity  {
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment navHostFragment =
                (NavHostFragment)
                        getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //Параметры конфигурации для NavigationUI методов,
        // которые взаимодействуют с реализациями шаблона панели приложений
        Toolbar toolbar = findViewById(R.id.toolbar); //создание панели инструментов
        toolbar.setTitle("@string/app_name");
        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
    }
}