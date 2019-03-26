package com.birdidi.android.myapt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.birdidi.android.aptlib.annotation.InjectView;
import com.birdidi.android.aptlib.annotation.Route;
import com.birdidi.android.funlib.helper.Binder;

@Route("android/birdidi/Main")
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.btn_route)
    Button btnRoute;
    @InjectView(R.id.tv_test)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Binder.bind(this);

        textView.setText("绑定成功");

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.navigate(MainActivity.this, "android/birdidi/A");
            }
        });
    }
}
