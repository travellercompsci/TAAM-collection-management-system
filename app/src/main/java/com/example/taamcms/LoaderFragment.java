package com.example.taamcms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

abstract public class FragmentLoaderActivity extends AppCompatActivity {
    /**
     * Switches the screen to the other fragment. Use this when a button is pressed to change screens.
     * @param fragment Class instance of the screen to switch to.
     */
    protected void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
