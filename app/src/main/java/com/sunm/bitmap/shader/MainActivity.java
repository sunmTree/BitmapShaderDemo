package com.sunm.bitmap.shader;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.sunm.bitmap.view.ShaderImage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ShaderImage spotlight = (ShaderImage) findViewById(R.id.spotlight);
        spotlight.setAnimationSetupCallback(new ShaderImage.AnimationSetupCallback() {
            @Override
            public void onSetupAnimation(ShaderImage spotlight) {
                createAnimation(spotlight);
            }
        });
    }

    private void createAnimation(final ShaderImage spotlight) {
        View top = findViewById(R.id.textView1);
        View bottom = findViewById(R.id.textView2);

        final float textHeight = bottom.getBottom() - top.getTop();
        final float startX = top.getLeft();
        final float startY = top.getTop() + textHeight / 2.0f;
        final float endX = Math.max(top.getRight(), bottom.getRight());

        spotlight.setMaskX(endX);
        spotlight.setMaskY(startY);

        spotlight.animate().alpha(1.0f).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator moveLeft = ObjectAnimator.ofFloat(spotlight, "maskX", endX, startX);
                moveLeft.setDuration(2000);

                float startScale = spotlight.computeMaskScale(textHeight);
                ObjectAnimator scaleUp = ObjectAnimator.ofFloat(spotlight, "maskScale", startScale, startScale * 3.0f);
                scaleUp.setDuration(2000);

                ObjectAnimator moveCenter = ObjectAnimator.ofFloat(spotlight, "maskX", spotlight.getWidth() / 2.0f);
                moveCenter.setDuration(1000);

                ObjectAnimator moveUp = ObjectAnimator.ofFloat(spotlight, "maskY", spotlight.getHeight() / 2.0f);
                moveUp.setDuration(1000);

                ObjectAnimator superScale = ObjectAnimator.ofFloat(spotlight, "maskScale",
                        spotlight.computeMaskScale(Math.max(spotlight.getHeight(), spotlight.getWidth()) * 1.7f));
                superScale.setDuration(2000);

                AnimatorSet set = new AnimatorSet();
                set.play(moveLeft).with(scaleUp);
                set.play(moveCenter).after(scaleUp);
                set.play(moveUp).after(scaleUp);
                set.play(superScale).after(scaleUp);
                set.start();

                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.content).setVisibility(View.VISIBLE);
                        findViewById(R.id.spotlight).setVisibility(View.GONE);
                        getWindow().setBackgroundDrawable(null);

                        spotlight.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                startActivity(intent);
                            }
                        },1000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
            }
        });
    }

}
