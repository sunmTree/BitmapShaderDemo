package com.sunm.bitmap.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sunm.bitmap.shader.R;

/**
 * Created by sm on 5/10/17.
 */

public class ShaderImage extends View {

    private int mTargetId;

    private Bitmap mMask;
    private float mMaskX;
    private float mMaskY;
    private float mMaskScale;
    private Matrix mShaderMatrix = new Matrix();

    private AnimationSetupCallback mCallback;

    private Bitmap mTargetBitmap;
    private Paint mPaint = new Paint();
    private Paint cirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public interface AnimationSetupCallback{
        void onSetupAnimation(ShaderImage view);
    }

    public ShaderImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SpotlightView, 0, 0);
        mTargetId= array.getResourceId(R.styleable.SpotlightView_target, 0);

        int maskId = array.getResourceId(R.styleable.SpotlightView_mask,0);
        mMask = convertToAlphaMask(BitmapFactory.decodeResource(getResources(),maskId));

        array.recycle();

        cirPaint.setColor(Color.RED);
        cirPaint.setStrokeWidth(4);
        cirPaint.setAlpha(100);
        cirPaint.setStyle(Paint.Style.STROKE);
    }

    private static Bitmap convertToAlphaMask(Bitmap b){
        Bitmap bitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b,0.0f,0.0f,null);
        return bitmap;
    }

    private Shader createShader(Bitmap bitmap){
        return new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    public Bitmap getMask() {
        return mMask;
    }

    public void setMask(Bitmap mMask) {
        this.mMask = mMask;
    }

    public float getMaskX() {
        return mMaskX;
    }

    public void setMaskX(float mMaskX) {
        this.mMaskX = mMaskX;
        invalidate();
    }

    public float getMaskY() {
        return mMaskY;
    }

    public void setMaskY(float mMaskY) {
        this.mMaskY = mMaskY;
        invalidate();
    }

    public float getMaskScale() {
        return mMaskScale;
    }

    public void setMaskScale(float mMaskScale) {
        this.mMaskScale = mMaskScale;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG","onDraw "+mMaskScale);
        float maskW = mMask.getWidth() / 2.0f;
        float maskH = mMask.getHeight() / 2.0f;

        float x = mMaskX - maskW * mMaskScale;
        float y = mMaskY - maskH * mMaskScale;

        Log.d("TAG","maskW [ "+maskW+" ] maskH [ "+maskH+" ] mMaskX [ "+mMaskX+" ] mMaskY [ "+mMaskY+" ] " +
                "x [ "+x+" ] y [ "+y+" ] "+getPivotX()+" >> "+getPivotY());

        mShaderMatrix.setScale(1.0f / mMaskScale, 1.0f / mMaskScale);
        mShaderMatrix.preTranslate(-x, -y);

        mPaint.getShader().setLocalMatrix(mShaderMatrix);

        canvas.translate(x,y);
        canvas.scale(mMaskScale,mMaskScale);
        canvas.drawBitmap(mMask,0.0f,0.0f,mPaint);
        canvas.drawCircle(x,y,mMask.getHeight() * mMaskScale,cirPaint);
    }

    public void setAnimationSetupCallback(AnimationSetupCallback callback){
        this.mCallback = callback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("TAG","onAttachedToWindow ");
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                createShader();
                setMaskScale(1.0f);
                if (mCallback != null){
                    mCallback.onSetupAnimation(ShaderImage.this);
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void createShader() {
        View target = getRootView().findViewById(mTargetId);
        mTargetBitmap = createBitmap(target);
        Shader targetShader = createShader(mTargetBitmap);
        mPaint.setShader(targetShader);
    }

    private static Bitmap createBitmap(View target) {
        Bitmap b = Bitmap.createBitmap(target.getWidth(), target.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        target.draw(c);
        return b;
    }

    public float computeMaskScale(float d) {
        // Let's assume the mask is square
        return d / (float) mMask.getHeight();
    }
}
