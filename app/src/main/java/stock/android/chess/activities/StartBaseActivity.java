package stock.android.chess.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import stock.android.chess.R;
import stock.android.chess.lichessClient.LichessClient;
import stock.android.chess.ChessPreferences;
import stock.android.chess.HtmlActivity;
import stock.android.chess.ics.ICSClient;
import stock.android.chess.lichessClient.LichessInterface;
import stock.android.chess.lichessClient.pojo.UserEmail;
import stock.android.chess.play.PlayActivity;
import stock.android.chess.practice.PracticeActivity;
import stock.android.chess.puzzle.PuzzleActivity;
import stock.android.chess.tools.AdvancedActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import stock.android.chess.watch.WatchActivity;

public class StartBaseActivity extends AppCompatActivity {
    public static final String TAG = "StartBaseActivity";
    protected ListView _list;
    protected int layoutResource = R.layout.start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        setContentView(layoutResource);
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

        _list = findViewById(R.id.ListStart);
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String requestedItem = parent.getItemAtPosition(position).toString();
                try {
                    Intent i = new Intent();
                    Log.i(TAG, requestedItem);
                    if (requestedItem.equals(getString(R.string.start_play))) {
                        i.setClass(StartBaseActivity.this, PlayActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_practice))) {
                        i.setClass(StartBaseActivity.this, PracticeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_puzzles))) {
                        i.setClass(StartBaseActivity.this, PuzzleActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_about))) {
                        i.setClass(StartBaseActivity.this, HtmlActivity.class);
                        i.putExtra(HtmlActivity.HELP_STRING_RESOURCE, R.string.about_help);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_ics))) {
                        i.setClass(StartBaseActivity.this, ICSClient.class);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_pgn))) {
                        i.setClass(StartBaseActivity.this, AdvancedActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_globalpreferences))) {
                        i.setClass(StartBaseActivity.this, ChessPreferences.class);
                        startActivityForResult(i, 0);
                    } else if (requestedItem.equals(getString(R.string.start_boardpreferences))) {
                        i.setClass(StartBaseActivity.this, BoardPreferencesActivity.class);
                        startActivity(i);
                    } else if (requestedItem.equals(getString(R.string.start_watch))) {
                        i.setClass(StartBaseActivity.this, WatchActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(i);
                    } else {
                        Log.d(TAG, "Nothing to start");
                    }
                } catch (Exception ex) {
                    Log.d(TAG, "Exception " + (ex != null ? ex.getMessage() : " no ex"));
                    Toast t = Toast.makeText(StartBaseActivity.this, R.string.toast_could_not_start_activity, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.BOTTOM, 0, 0);
                    t.show();
                }
            }
        });

        _list.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "recreate");
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
