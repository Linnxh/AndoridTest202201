package com.lxh.andoridtest.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;


/**
 * Created by gengzhibo on 17/8/3.
 */
public class DashBoard extends View {


    private Paint paint, tmpPaint, textPaint, strokePain;
    private RectF rect;
    private int backGroundColor;    //背景色
    private float pointLength;      //指针长度
    private float per;             //指数百分比
    private float perPoint;        //缓存(变化中)指针百分比
    private float perOld;          //变化前指针百分比
    private float length;          //仪表盘半径
    private float r;
    private float outerAngle = 50; // 圆环延伸的角度（圆环的角度 = 180度 + 此处的数值 ）
    private float ringLength = 0; // 圆环的宽度
    private float tempRou;// 每个间隔的度数

    public DashBoard(Context context) {
        super(context);
        init();
    }


    public DashBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int heitht = (int) (width / 2 / 4 * 6.5f); // TODO: 2022/1/18 lxh 这个组件的高度
        int heitht = (int) (width / 2 / 4 * 8f); // TODO: 2022/1/18 lxh 这个组件的高度
        initIndex(width / 2);
        //优化组件高度
        setMeasuredDimension(width, heitht);
    }


    private void initIndex(int specSize) {
        backGroundColor = Color.WHITE;
        r = specSize;
        length = r / 4 * 3;
        pointLength = -(float) (r * 0.5);
        per = 0;
        perOld = 0;
    }


    private void init() {
        paint = new Paint();
        rect = new RectF();
        textPaint = new Paint();
        tmpPaint = new Paint();
        strokePain = new Paint();
    }

    public DashBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setR(float r) {
        this.r = r;
        this.length = r / 4 * 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //颜色指示的环
        initRing(canvas);
        //刻度文字
        initScale(canvas);
        //指针
        initPointer(canvas);
        //提示内容
        initText(canvas);
    }

    private void initText(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, r);

        float rIndex = length;

//        设置文字展示的圆环
//        paint.setColor(Color.parseColor("#eeeeee"));
//        paint.setShader(null);
//        paint.setShadowLayer(5, 0, 0, 0x54000000);
//        rect = new RectF(-(rIndex / 3), -(rIndex / 3), rIndex / 3, rIndex / 3);
//        canvas.drawArc(rect, 0, 360, true, paint);

        paint.clearShadowLayer();

        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth() / 2f, r);


        textPaint.setStrokeWidth(1);
        textPaint.setAntiAlias(true);

        textPaint.setTextSize(60);
        textPaint.setColor(Color.parseColor("#fc6555"));
        textPaint.setTextAlign(Paint.Align.RIGHT);


        //判断指数变化及颜色设定

//        int _per = per;

//        if (_per < 60) {
//            textPaint.setColor(Color.parseColor("#ff6450"));
//        } else if (_per < 100) {
//            textPaint.setColor(Color.parseColor("#f5a623"));
//        } else {
//            textPaint.setColor(Color.parseColor("#79d062"));
//        }

        float swidth = textPaint.measureText(String.valueOf(per));
        //计算偏移量 是的数字和百分号整体居中显示
        swidth = (swidth - (swidth + 22) / 2);


        canvas.translate(swidth, length / 3);
        canvas.drawText("" + per, 0, 0, textPaint);

        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("%", 0, 0, textPaint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#999999"));


        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, r + length / 3 / 2);
        canvas.drawText("完成率", 0, -length / 3, textPaint);


    }


    public void setBackGroundColor(int color) {
        this.backGroundColor = color;
    }

    public void setPointLength1(float pointLength1) {
        this.pointLength = -length * pointLength1;
    }

    private void initScale(Canvas canvas) {
        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, r);
        paint.setColor(Color.parseColor("#999999"));

        tmpPaint = new Paint(paint); //小刻度画笔对象
        tmpPaint.setStrokeWidth(1);
        float textSize = 35;
        tmpPaint.setTextSize(textSize);
        tmpPaint.setTextAlign(Paint.Align.CENTER);

        canvas.rotate(-90, 0f, 0f);

        float y = length;
        y = -y;
//        int count = 12; //总刻度数
        int count = 10; //总刻度数
        paint.setColor(backGroundColor);

//        float tempRou = 180 / 12f;
        float tempRou = (180 + 2 * outerAngle) / count;

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);

        canvas.rotate(-outerAngle, 0f, 0f);
        //绘制刻度和百分比
        for (int i = 0; i <= count; i++) {

//            if (i % 2 == 0) {
            float textY = y;
            textY = textY + ringLength + textSize;
            if (i <= 2) {
                tmpPaint.setColor(Color.parseColor("#F44336"));
            } else if (i >= 9) {
                tmpPaint.setColor(Color.parseColor("#009688"));
            } else {
                tmpPaint.setColor(Color.parseColor("#FFEB3B"));
            }
            canvas.drawText(String.valueOf((i) * 10), 0, textY, tmpPaint);
//            }

            canvas.drawLine(0f, y, 0, y + length / 15, paint);

            canvas.rotate(tempRou, 0f, 0f);
        }

    }


    private void initPointer(Canvas canvas) {
        paint.setColor(Color.BLACK);


        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, r);
        //根据参数得到旋转角度
        canvas.rotate(-tempRou * (5 - perPoint / 10), 0f, 0f);

        //绘制三角形形成指针(前部)
        int firstPoint = -10;
        Path path = new Path();
        path.moveTo(0, pointLength + firstPoint);
        path.lineTo(-10, firstPoint);
        path.lineTo(10, firstPoint);
        path.lineTo(0, pointLength + firstPoint);
        path.close();
        canvas.drawPath(path, paint);
        //绘制三角形形成指针（后部）
        int secondPoint = -10;
        Path secondPath = new Path();
        secondPath.moveTo(0, -2 * secondPoint);
        secondPath.lineTo(-10, secondPoint);
        secondPath.lineTo(10, secondPoint);
        secondPath.lineTo(0, -2 * secondPoint);
        secondPath.close();
        paint.setColor(Color.BLACK);
        canvas.drawPath(secondPath, paint);

    }

    private void initRing(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, r);


        int count = 10; //总刻度数


        tempRou = (180 + 2 * outerAngle) / count;

        //前20红色圆环(可设置渐变)
        paint.setStyle(Paint.Style.FILL);
        int[] colors3 = {Color.parseColor("#F44336"), Color.parseColor("#F44336"), Color.parseColor("#F44336")};
        float[] positions3 = {0.5f - 10f / 180f * 0.5f, 0.5f + 0.5f * 5f / 6f, 1.0f};
        SweepGradient sweepGradient3 = new SweepGradient(0, 0, colors3, positions3);
        paint.setShader(sweepGradient3);
        rect = new RectF(-length, -length, length, length);
        canvas.drawArc(rect, 180 - outerAngle, tempRou * 2, true, paint);

        //前20到80黄色圆环
        paint.setStyle(Paint.Style.FILL);
        int[] colors = {Color.parseColor("#FFEB3B"), Color.parseColor("#FFEB3B"), Color.parseColor("#FFEB3B")};
        float[] positions = {0.5f - 10f / 180f * 0.5f, 0.5f + 0.5f * 5f / 6f, 1.0f};
        SweepGradient sweepGradient = new SweepGradient(0, 0, colors, positions);
        paint.setShader(sweepGradient);
        rect = new RectF(-length, -length, length, length);
        canvas.drawArc(rect, 180 - outerAngle + tempRou * 2, tempRou * 6, true, paint);


        //80之后绿色渐变圆环
        paint.setStyle(Paint.Style.FILL);
        int[] colors2 = {Color.parseColor("#009688"), Color.parseColor("#009688")};
        float[] positions2 = {0.5f + 0.5f * (144f / 180f), 1.0f};
        sweepGradient = new SweepGradient(0, 0, colors2, positions2);
        paint.setShader(sweepGradient);
        rect = new RectF(-length, -length, length, length);
        canvas.drawArc(rect, outerAngle - tempRou * 2, tempRou * 2, true, paint);


        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, r);

        // 描边
//        strokePain = new Paint(paint);
//
//        strokePain.setColor(0x3f979797);
//        strokePain.setStrokeWidth(30);
//        strokePain.setShader(null);
//        strokePain.setStyle(Paint.Style.STROKE);
//        canvas.drawArc(rect, 170, 200, true, strokePain);


//        canvas.restore();
//        canvas.save();
//        canvas.translate(canvas.getWidth()/2, r);
//
//        //底边水平
//        paint.setShader(null);
//        paint.setColor(backGroundColor);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(-length  , (float) (Math.sin(Math.toRadians(10) ) * length /3f * 2f), length  ,  (float) (Math.sin(Math.toRadians(10)) * length  + 100) , paint);
//        canvas.drawRect(-length  , (float) (Math.sin(Math.toRadians(10) ) * length /3f * 2f), length  ,  (float) (Math.sin(Math.toRadians(10) ) * length /3f * 2f) , strokePain);
//
//
        //内部背景色填充 (白色)
        paint.setColor(backGroundColor);
        paint.setShader(null);
        float innerLength = length - length / 5;
        ringLength = length / 5;
        rect = new RectF(-innerLength, -innerLength, innerLength, innerLength);
//        canvas.drawArc(rect, 180 - outerAngle, 200, true, strokePain);
        canvas.drawArc(rect, 0, 360, true, paint);


    }


    public void cgangePer(float per) {
        this.perOld = this.per;
        this.per = per;
        ValueAnimator va = ValueAnimator.ofFloat(perOld, per);
        va.setDuration(2000);
        va.setInterpolator(new OvershootInterpolator());// 回弹效果
//        va.setInterpolator(new AccelerateDecelerateInterpolator());// 加速到终点
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                perPoint = (float) animation.getAnimatedValue();
                Log.i("DashBoard", perPoint + "");
                invalidate();
            }
        });
        va.start();

    }
}