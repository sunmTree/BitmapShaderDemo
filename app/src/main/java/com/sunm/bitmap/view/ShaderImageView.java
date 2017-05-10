package com.sunm.bitmap.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.sunm.bitmap.shader.R;

/**
 * Created by sm on 5/10/17.
 */

public class ShaderImageView extends ImageView {

    private Bitmap mTargetBitmap;
    private Paint mPaint = new Paint();
    private Matrix mMatrix = new Matrix();

    private Bitmap mMask;
    private float mMaskScale;


    private OnAnimPrepareCallback mCallback;

    public interface OnAnimPrepareCallback {
        void onAnimPrepareCallback(ShaderImageView view);
    }

    public ShaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMask = convertToAlphaMask(BitmapFactory.decodeResource(getResources(), R.drawable.spot_mask));

        cirPaint.setColor(Color.RED);
        cirPaint.setStrokeWidth(4);
        cirPaint.setAlpha(100);
        cirPaint.setStyle(Paint.Style.STROKE);
    }

    public void setMaskScale(float mMaskScale) {
        this.mMaskScale = mMaskScale;
        invalidate();
    }

    private Paint cirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("ShaderImageView", "onDraw " + mMaskScale + " maskW [ " + getWidth() / 2 + " ] maskH [ " + getHeight() / 2 + " ] ");

        float maskW = mMask.getWidth() / 2.0f;
        float maskH = mMask.getHeight() / 2.0f;

        float x = - maskW * mMaskScale;
        float y = - maskH * mMaskScale;

        mMatrix.setScale(1.0f / mMaskScale, 1.0f / mMaskScale);
        mMatrix.preTranslate(-x, -y);
        mPaint.getShader().setLocalMatrix(mMatrix);

        canvas.translate(x,y);
        canvas.scale(mMaskScale, mMaskScale);
        canvas.drawBitmap(mMask, 0, 0, mPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 40, cirPaint);

    }

    public void setOnAnimCallback(OnAnimPrepareCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                createShader();
                setMaskScale(1.0f);
                if (mCallback != null) {
                    mCallback.onAnimPrepareCallback(ShaderImageView.this);
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private static Bitmap convertToAlphaMask(Bitmap b) {
        Bitmap bitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b, 0.0f, 0.0f, null);
        return bitmap;
    }

    private void createShader() {
        View view = getRootView().findViewById(R.id.content);
        mTargetBitmap = createBitmap(view);
        Shader shader = createShader(mTargetBitmap);
        mPaint.setShader(shader);
    }

    private Shader createShader(Bitmap b) {
        return new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    private Bitmap createBitmap(View target) {
        Bitmap bitmap = Bitmap.createBitmap(target.getWidth(), target.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        target.draw(canvas);
        return bitmap;
    }


    public void startAnim() {
        this.animate().alpha(1.0f).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                int heightPixels = getContext().getResources().getDisplayMetrics().heightPixels;
                float startScale = heightPixels / (float) mMask.getHeight();
                Log.d("ShaderImageView", "startAnim heightPixels " + heightPixels + " startScale " + startScale + " height [ " + (float) mMask.getHeight() + " ]");
                ValueAnimator animator = ValueAnimator.ofFloat(1, startScale * 3);
//                ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(this, "MaskScale", startScale, startScale * 3);
                animator.setDuration(400).setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale = (float) animation.getAnimatedValue();
                        setMaskScale(scale);
                    }
                });
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getRootView().findViewById(R.id.content).setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });

    }

    public void exitAnim(final Animator.AnimatorListener listener){
        this.animate().alpha(1.0f).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                int heightPixels = getContext().getResources().getDisplayMetrics().heightPixels;
                float startScale = heightPixels / (float) mMask.getHeight();
                Log.d("ShaderImageView", "startAnim heightPixels " + heightPixels + " startScale " + startScale + " height [ " + (float) mMask.getHeight() + " ]");
                ValueAnimator animator = ValueAnimator.ofFloat(startScale * 3, 1);
//                ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(this, "MaskScale", startScale, startScale * 3);
                animator.setDuration(4000).setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale = (float) animation.getAnimatedValue();
                        setMaskScale(scale);
                    }
                });
                animator.start();
                animator.addListener(listener);
            }
        });
    }
}
