package com.shhp.centrifuge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.shhp.centrifuge.annotation.Centrifuge;

@Centrifuge
public class SampleActivity extends AppCompatActivity {

    static {
        Log.i("Test", "This is a static block.");
    }

    @Override
    @Centrifuge
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

    static class TestClass {

        @Centrifuge
        public TestClass() {
            Log.i("Test", "This is the constructor of TestClass.");
        }
    }
}
