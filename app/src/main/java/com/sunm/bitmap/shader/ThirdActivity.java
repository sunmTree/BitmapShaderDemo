package com.sunm.bitmap.shader;

import android.animation.Animator;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.sunm.bitmap.view.ShaderImageView;

public class ThirdActivity extends AppCompatActivity {

    private ShaderImageView app_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        app_icon = (ShaderImageView) findViewById(R.id.app_icon);
        app_icon.setOnAnimCallback(new ShaderImageView.OnAnimPrepareCallback() {
            @Override
            public void onAnimPrepareCallback(ShaderImageView view) {
                Log.d("SecondActivity","onAnimPrepareCallback");
                app_icon.startAnim();
                getWindow().getDecorView().setAlpha(1);
            }
        });


        app_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_icon.exitAnim(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ThirdActivity.this.finish();
                        overridePendingTransition(0,0);
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
}
