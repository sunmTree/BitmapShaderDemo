package com.sunm.bitmap.shader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.sunm.bitmap.view.ShaderImageView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


    }

    public void repeat(View v){
//        app_icon.startAnim();
        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
        startActivity(intent);
//      overridePendingTransition(R.anim.window_fade_in_slow,R.anim.window_fade_out_delay);
        overridePendingTransition(0,0);
    }
}
