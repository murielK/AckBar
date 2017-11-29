package com.murielkamgang.ackbardemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.murielkamgang.AckBar;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_test_1, R.id.button_test_2, R.id.button_test_3, R.id.button_test_4})
    public void onClick(View view) {
        AckBar ackBar;
        switch (view.getId()) {
            case R.id.button_test_1:
                ackBar = AckBar.make(this, R.string.test_1, android.R.color.black, 5000);
                ackBar.setMsg(R.string.test_1_msg);
                ackBar.show();
                break;
            case R.id.button_test_2:
                ackBar = AckBar.make(this, R.string.test_2, android.R.color.holo_red_dark, 5000);
                ackBar.show();
                break;
            case R.id.button_test_3:
                ackBar = AckBar.make(this, R.string.test_3, android.R.color.holo_blue_dark, 0);
                ackBar.setMsg(R.string.test_3_msg);
                ackBar.show();
                break;
            case R.id.button_test_4:
                ackBar = AckBar.make(this, R.string.test_4, android.R.color.holo_purple);
                ackBar.setMsg(R.string.test_4_msg);
                ackBar.setAction(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Action", Toast.LENGTH_LONG).show();
                    }
                });
                ackBar.show();
                break;
        }
    }
}
