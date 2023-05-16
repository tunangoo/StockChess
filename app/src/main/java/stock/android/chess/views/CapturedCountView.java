package stock.android.chess.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;
import stock.android.chess.constants.ColorSchemes;

public class CapturedCountView extends AppCompatTextView {
    private int piece;

    public CapturedCountView(Context context, int count, int piece) {
        super(context);

        this.piece = piece;

        setWillNotDraw(false);

        setTextColor(ColorSchemes.getHighlightColor());

        if (count > 0) {
            setText("" + count);
        } else {
            setText("");
        }
    }

    public int getPiece() {
        return piece;
    }

    public void onDraw(Canvas canvas) {
        int textSize = getHeight() / 3;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize >= 8 ? textSize : 8);

        super.onDraw(canvas);
    }
}
