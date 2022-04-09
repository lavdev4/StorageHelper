package com.lavdevapp.storagehelper.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lavdevapp.storagehelper.R;
import com.lavdevapp.storagehelper.views.dialogs.ErrorDialog;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private NavHostFragment navHostFragment;
    private NavController navController;
    private FrameLayout progressView;
    private Handler handler;
    private volatile boolean blockTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressView = findViewById(R.id.view_progress);
        handler = new Handler();

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            NavigationUI.onNavDestinationSelected(item, navController);
            navController.popBackStack(item.getItemId(), false);
            return true;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (blockTouch) {
            return false;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    public void startProgressView() {
        blockTouch = true;
        handler.postDelayed(() -> {
            if (blockTouch) {
                progressView.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    public void stopProgressView() {
        blockTouch = false;
        progressView.setVisibility(View.GONE);
    }

    public void showErrorDialog(String errorName, String errorText) {
        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("error_name", errorName);
        bundle.putString("error_text", errorText);
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.setArguments(bundle);
        errorDialog.show(fragmentManager, "ErrorDialog");
    }

    public void hideKeyboard(View rootView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    public void redrawCurrentFragment() {
        int destinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        navController.popBackStack(destinationId, true);
        navController.navigate(destinationId);
    }
}