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
public class LineView extends View {
    private Paint p;
    private int lineColor;

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineView);
        lineColor = ta.getColor(R.styleable.LineView_line_color, Color.GRAY);
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
        canvas.drawLine(0, HeightSize / 2, WidthSize, HeightSize / 2, p);
    }

    private int WidthSize = 720;//控件的宽
    private int HeightSize = 4;//控件的高

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WidthSize = getMeasuredWidth();
        HeightSize = getMeasuredHeight();
    }
}
