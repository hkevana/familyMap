package byu.cs240.familymapclient.ui;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import byu.cs240.familymapclient.R;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private LoginFragment loginFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify.with(new FontAwesomeModule());

        Log.d(TAG, "onCreate(Bundle)");
        setContentView(R.layout.activity_main);

        FragmentManager fm = this.getSupportFragmentManager();
        loginFragment = (LoginFragment)fm.findFragmentById(R.id.fragment_container);
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            //Bundle args = new Bundle();
            //args.putString(LoginFragment.ARG_TITLE, "Enter Information");
            fm.beginTransaction()
                    .add(R.id.fragment_container, loginFragment)
                    .commit();
        }
    }
}