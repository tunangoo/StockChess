package stock.android.chess;
import android.os.Bundle;

import stock.android.chess.R;
import stock.android.chess.activities.StartBaseActivity;

public class MainActivity extends StartBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutResource = R.layout.start_foss;

        super.onCreate(savedInstanceState);
    }
}