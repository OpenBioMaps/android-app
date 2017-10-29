package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import hu.ektf.iot.openbiomapsapp.R;


public class SquareImageView extends ImageView {

    public enum FixedAlong {
        width, height
    }

    private FixedAlong fixedAlong = FixedAlong.width;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.SquareImageView, 0, 0);

        fixedAlong = FixedAlong.valueOf(array
                .getString(R.styleable.SquareImageView_fixedAlong));
        if (fixedAlong == null)
            fixedAlong = FixedAlong.width;

        array.recycle();
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    int squareDimen = 1;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = 0;
        int h = 0;

        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        if (getDrawable() == null) {
            // If no drawable, its intrinsic size is 0.
            w = h = 0;
        } else {
            w = getDrawable().getIntrinsicWidth();
            h = getDrawable().getIntrinsicHeight();
            if (w <= 0)
                w = 1;
            if (h <= 0)
                h = 1;
        }

        w += pleft + pright;
        h += ptop + pbottom;

        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        int square = (fixedAlong == FixedAlong.width) ? resolveSize(w, widthMeasureSpec)
                : resolveSize(h, heightMeasureSpec);

        if (square > squareDimen) {
            squareDimen = square;
        }

        setMeasuredDimension(square, square);
    }
}