package com.swustzl.myflashlight.customview;

import com.swustzl.myflashlight.util.MyApplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 自定义状态开关
 * */
public class MySwitch extends View {
    private Paint paint;// 画笔
    private RectF rect;
    private RectF rect1;
    private RectF rect2, oval;

    private PorterDuffXfermode porterDuffXfermode;// 混合模式

    private int viewX, viewY;// 控件边长

    private Boolean state;

    private float maxY, minY, middleY;
    private float ccX, ccY;// 中心圆圆心坐标
    private float radiu;// 半径

    private OnStateListener onStateListener;

    public MySwitch(Context context) {
        super(context);
    }

    public MySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        state = false;
        porterDuffXfermode = new PorterDuffXfermode(Mode.SRC_ATOP);
        onStateListener = null;

    }

    /**
     * 设置监听接口
     * */
    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        WindowManager wm = (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int windowWidth = display.getWidth();
        int width = windowWidth / 4;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }

        int height = width * 2;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewX = w;
        viewY = h;
        radiu = viewX / 2;

        minY = viewY / 4;
        maxY = viewY * 3 / 4;
        middleY = viewY / 2;

        ccX = radiu;
        rect = new RectF(0, 0, viewX, viewY);
        if (state) {
            calculation(minY);
        } else {
            calculation(maxY);
        }

    }

    /**
     * 参数计算
     */
    private void calculation(float varY) {
        if (varY < minY) {
            varY = minY;
        } else if (varY > maxY) {
            varY = maxY;
        }
        ccY = varY;
        rect1 = new RectF(0, ccY - 3 * radiu, viewX, ccY + radiu);
        rect2 = new RectF(0, ccY - radiu, viewX, ccY + 3 * radiu);
        oval = new RectF(radiu / 2, ccY - radiu / 2, viewX * 3 / 4, ccY + radiu / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, viewX, viewY, null, Canvas.ALL_SAVE_FLAG);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rect, radiu, radiu, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(rect, radiu, radiu, paint);

        paint.setXfermode(porterDuffXfermode);
        canvas.drawRoundRect(rect1, radiu, radiu, paint);
        paint.setStrokeWidth(3);
        canvas.drawCircle(ccX, ccY - 2 * radiu, radiu / 10, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRoundRect(rect2, radiu, radiu, paint);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        canvas.drawLine(ccX, ccY + 2 * radiu - radiu / 10, ccX, ccY + 2 * radiu + radiu / 10, paint);
        paint.setXfermode(null);

        paint.setStrokeWidth(2);
        canvas.drawCircle(ccX, ccY, radiu, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawCircle(ccX, ccY, radiu, paint);
        paint.setStrokeWidth(3);
        canvas.drawArc(oval, -80, 340, false, paint);
        canvas.drawLine(ccX, ccY, ccX, ccY - radiu / 2, paint);
        canvas.restoreToCount(sc);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventType = event.getAction();
        switch (eventType) {
        case MotionEvent.ACTION_DOWN:
            calculation(event.getY());
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            calculation(event.getY());
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            if (ccY > middleY) {
                changeStateTo(false);
            } else {
                changeStateTo(true);
            }
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * 设置状态开关的开关状态
     * */
    public void setState(Boolean state) {
        this.state = state;
        if (state) {
            calculation(minY);
        } else {
            calculation(maxY);
        }
        invalidate();
    }

    /**
     * 状态开关滑动时改变开关的状态
     * */
    private void changeStateTo(Boolean state) {
        setState(state);
        onStateListener.viewState(state);
    }

    /**
     * 监听接口 监听状态开关的状态变化
     */
    public interface OnStateListener {
        public void viewState(Boolean state);

    }
}
