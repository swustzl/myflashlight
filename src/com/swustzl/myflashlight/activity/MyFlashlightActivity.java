package com.swustzl.myflashlight.activity;

import com.swustzl.myflashlight.R;
import com.swustzl.myflashlight.customview.MySwitch;
import com.swustzl.myflashlight.customview.MySwitch.OnStateListener;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MyFlashlightActivity extends Activity {

    private Camera camera;
    private Parameters params;
    private Boolean srcState;
    private RelativeLayout reLayout;
    private MySwitch mySwitch;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_flashlight_main);
        reLayout = (RelativeLayout) findViewById(R.id.re_layout);
        mySwitch = (MySwitch) findViewById(R.id.my_switch);
        srcState = false;
        pm = this.getPackageManager();
        mySwitch.setOnStateListener(new OnStateListener() {

            @Override
            public void viewState(Boolean state) {
                Log.i("state", state + "");
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    Toast.makeText(getApplicationContext(), "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
                    mySwitch.setState(false);
                } else if (state && !srcState) {
                    try {
                        camera = Camera.open();
                        params = camera.getParameters();
                        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(params);
                        camera.startPreview(); // 开始亮灯
                        srcState = true;
                        reLayout.setBackgroundColor(Color.BLACK);
                        mySwitch.setBackgroundColor(Color.BLACK);
                        Toast.makeText(getApplicationContext(), "您已经打开了手电筒", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "打开手电筒失败,摄像头闪光灯被其他应用占用或被禁止", Toast.LENGTH_SHORT).show();
                        mySwitch.setState(false);
                    }

                } else if (!state && srcState) {
                    camera.stopPreview(); // 关掉亮灯
                    camera.release(); // 关掉照相机
                    srcState = false;
                    reLayout.setBackgroundColor(Color.WHITE);
                    mySwitch.setBackgroundColor(Color.WHITE);
                    Toast.makeText(getApplicationContext(), "关闭了手电筒", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

}
