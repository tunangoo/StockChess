package stock.android.chess;

import android.os.Bundle;

import stock.android.chess.activities.StartBaseActivity;
import stock.android.chess.fragments.MainActivity;

//public class start extends MainActivity {
public class start extends StartBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutResource = R.layout.start_foss;

        super.onCreate(savedInstanceState);
    }
}
