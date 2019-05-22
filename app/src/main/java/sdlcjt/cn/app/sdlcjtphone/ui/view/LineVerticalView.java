package sdlcjt.cn.app.sdlcjtphone.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import sdlcjt.cn.app.sdlcjtphone.R;


/**
 * Created by tab on 2018/3/23.
 */
public class LineVerticalView extends View {
    private Paint p;
    private int lineColor;

    public LineVerticalView(Context context) {
        super(context);
        init();
    }

    public LineVerticalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineVerticalView);
        lineColor = ta.getColor(R.styleable.LineVerticalView_line_vertical_color, Color.parseColor("#f5f5f5"));
        ta.recycle();
        init();
    }

    private void init() {
        p = new Paint();
        p.setColor(lineColor);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawLine(WidthSize / 2, 0, WidthSize / 2, HeightSize, p);
    }

    private int WidthSize = 4;//控件的宽
    private int HeightSize = 100;//控件的高

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WidthSize = getMeasuredWidth();
        HeightSize = getMeasuredHeight();
    }

    public void setLineColor(int color){
        p.setColor(color);
        invalidate();
    }
}
