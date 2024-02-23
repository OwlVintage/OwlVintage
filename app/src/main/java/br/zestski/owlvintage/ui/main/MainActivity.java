package br.zestski.owlvintage.ui.main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.databinding.ActivityMainBinding;
import br.zestski.owlvintage.fragment.splash.SplashFragment;

/**
 * MainActivity responsible for initializing the main UI components.
 *
 * @author Zestski
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeSplashScreen();
        initializeEdgeToEdge();
        initializeFragment();
        initializeWindowInsetsListener();
    }

    /**
     * Initializes the splash screen.
     */
    private void initializeSplashScreen() {
        SplashScreen.installSplashScreen(this);
    }

    /**
     * Initializes EdgeToEdge for immersive UI experience.
     */
    private void initializeEdgeToEdge() {
        EdgeToEdge.enable(this);
    }

    /**
     * Initializes the main fragment.
     */
    private void initializeFragment() {
        var activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        if (getSupportFragmentManager().findFragmentByTag(SplashFragment.TAG) == null) {
            replaceSplashFragment(activityMainBinding);
        }
    }

    /**
     * Replaces the current fragment with the SplashFragment if not already added.
     *
     * @param activityMainBinding The binding object for the main activity layout.
     */
    private void replaceSplashFragment(
            @NonNull ActivityMainBinding activityMainBinding
    ) {
        getSupportFragmentManager().beginTransaction()
                .replace(activityMainBinding.activityMainFragmentContainer.getId(), new SplashFragment(),
                        SplashFragment.TAG).commitAllowingStateLoss();
    }

    /**
     * Initializes the window insets listener for adjusting padding based on system bars.
     */
    private void initializeWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main_fragment_container), (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            v.requestApplyInsets();
            return insets;
        });
    }
}