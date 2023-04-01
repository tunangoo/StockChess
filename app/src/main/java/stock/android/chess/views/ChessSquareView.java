package stock.android.chess.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import stock.android.chess.R;
import stock.android.chess.constants.ColorSchemes;
import stock.chess.Pos;
import stock.chess.board.ChessBoard;

public class ChessSquareView extends View {
    private int pos;
    private boolean selected;
    private boolean highlighted;
    private boolean focussed;

    private static Paint paint = new Paint();
    private static Paint highlightPaint = new Paint();

    public ChessSquareView(Context context, int pos) {
        super(context);
        this.pos = pos;
        selected = false;
        highlighted = false;
        focussed = false;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        if (this.pos != pos) {
            this.pos = pos;
            this.invalidate();
        }
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            this.invalidate();
        }
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setHighlighted(boolean highlighted) {
        if (this.highlighted != highlighted) {
            this.highlighted = highlighted;
            this.invalidate();
        }
    }

    public boolean getHighlighted() {
        return this.highlighted;
    }

    public void setFocussed(boolean focussed) {
        if (this.focussed != focussed) {
            this.focussed = focussed;
            this.invalidate();
        }
    }

    public void onDraw(Canvas canvas) {

        if (this.selected) {
            paint.setColor(ColorSchemes.getSelectedColor());
        } else {
            final int fieldColor = (pos & 1) == 0 ? (((pos >> 3) & 1) == 0 ? ChessBoard.WHITE : ChessBoard.BLACK) : (((pos >> 3) & 1) == 0 ? ChessBoard.BLACK : ChessBoard.WHITE);
            paint.setColor(fieldColor == ChessBoard.WHITE ? ColorSchemes.getLight() : ColorSchemes.getDark());
        }
        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);

        int patternDrawable = ColorSchemes.getSelectedPatternDrawable();
        if (patternDrawable > 0) {
            Drawable d = getResources().getDrawable(patternDrawable, null);
            d.setTint(ColorSchemes.getSelectedColor());
            d.setBounds(0, 0, getWidth(), getHeight());
            d.draw(canvas);
        }

        if (focussed) {
            Drawable d = getResources().getDrawable(R.drawable.ic_select, null);
            d.setBounds(0, 0, getWidth(), getHeight());
            d.draw(canvas);
        }

        if (ColorSchemes.showCoords) {
            String coord = "";
            if (pos > 55) {
                coord = Pos.colToString(pos).toUpperCase();
            } else {
                if (pos % 8 == 0) {
                    coord = Pos.rowToString(pos);
                }
            }
            if (coord.length() > 0) {
                final boolean isRot = ColorSchemes.isRotated;
                final int size = getHeight();
                final int textSize = size > 60 ? ((int) (size) / 5) : 10;

                paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(0x99ffffff);
                canvas.drawRect(
                        isRot ? size - textSize : 0,
                        isRot ? 0 : size - textSize,
                        isRot ? size : textSize,
                        isRot ? textSize : size, paint);

                paint.setColor(0x99000000);
                paint.setTextSize(textSize - 4);
                canvas.drawText(coord, isRot ? size - textSize + 4: 4, isRot ? textSize - 4 : size - 4, paint);
            }
        }

        if (highlighted) {
            int strokeWidth = getWidth() / 8;
            highlightPaint.setStyle(Paint.Style.STROKE);
            highlightPaint.setStrokeWidth(strokeWidth);
            highlightPaint.setColor(ColorSchemes.getHightlightColor());
            canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), highlightPaint);
        }
    }
}
