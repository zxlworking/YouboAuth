package cn.iubo.youboauth.testjni;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String str = JniUtils.getJniString();
        System.out.println("zxl--->str--->"+str);
    }
}
