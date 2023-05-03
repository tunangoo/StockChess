package stock.android.chess.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stock.android.chess.R;
import stock.android.chess.databinding.ActivityMainBinding;
import stock.android.chess.lichessClient.LichessClient;
import stock.android.chess.lichessClient.LichessInterface;
import stock.android.chess.lichessClient.pojo.UserEmail;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "StartBaseActivity";
    protected ListView _list;
    protected int layoutResource = R.layout.start;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("ChessPlayer", Context.MODE_PRIVATE);
        String myLanguage = prefs.getString("localelanguage", "");

        Locale current = getResources().getConfiguration().locale;
        String language = current.getLanguage();
        if (myLanguage.equals("")) {    // localelanguage not used yet? then use device default locale
            myLanguage = language;
        }

        Locale locale = new Locale(myLanguage);    // myLanguage is current language
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.setLocale(locale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            getApplicationContext().createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, displayMetrics);
        }

        if (prefs.getBoolean("nightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.login, R.id.nav_play, R.id.nav_gallery, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        LichessInterface apiInterface = LichessClient.getClient().create(LichessInterface.class);
        Call<UserEmail> call = apiInterface.doGetUserEmail();
        call.enqueue(new Callback<UserEmail>() {
            @Override
            public void onResponse(Call<UserEmail> call, Response<UserEmail> response) {
                Log.d("EMAIL_Status: ", response.code()+"");
                UserEmail ue = response.body();
                Log.d("EMAIL: ", ue.email);
            }

            @Override
            public void onFailure(Call<UserEmail> call, Throwable t) {
                Log.d("Email", "Fail as fuck");
                call.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}