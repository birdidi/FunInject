package com.birdidi.android.myapt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.birdidi.android.aptlib.annotation.InjectView;
import com.birdidi.android.aptlib.annotation.Route;
import com.birdidi.android.funlib.helper.Binder;

/**
 * @author xuyu.chen
 * @date 2019/03/26
 * @email xuyu.chen@ucarinc.com
 * @desc
 */
@Route("android/birdidi/A")
public class TestAActivity extends AppCompatActivity {

    @InjectView(R.id.tv_test)
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_a);
        Binder.bind(this);

        textView.setText("AAA");
    }
}
