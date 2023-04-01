package stock.android.chess.ics;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import stock.android.chess.activities.BasePreferenceActivity;
import stock.android.chess.R;

public class ICSPrefs extends BasePreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.icsprefs);

        Preference prefCustomCommand = findPreference("icscustomcommandHandle");
        prefCustomCommand.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(ICSPrefs.this, CustomCommands.class);

                startActivity(intent);
                return true;
            }
        });

    }
}

