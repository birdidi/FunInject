package com.birdidi.android.myapt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdidi.android.aptlib.annotation.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.iv_test)
    ImageView imageView;
    @InjectView(R.id.tv_test)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Binder.bind(this);

        textView.setText("绑定成功");
    }
}
