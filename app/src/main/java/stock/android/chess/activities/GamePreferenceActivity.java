package stock.android.chess.activities;

import android.os.Bundle;

import stock.android.chess.R;

public class GamePreferenceActivity extends BasePreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.game_prefs);

    }
}
