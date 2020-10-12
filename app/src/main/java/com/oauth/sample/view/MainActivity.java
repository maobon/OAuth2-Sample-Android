package com.oauth.sample.view;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oauth.sample.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    /***********************************************************
     *  Attributes
     **********************************************************/
    Button btnDo;
    TextView txvResult;

    /***********************************************************
     *  Managing LifeCycle
     **********************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDo = (Button) findViewById(R.id.btnDo);

        btnDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // listGDriveUserFiles();
            }
        });

        txvResult = (TextView) findViewById(R.id.txvResult);

        txvResult.setMovementMethod(new ScrollingMovementMethod());

    }
}
