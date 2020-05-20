package com.example.myapplication.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

@RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
public class CameraSource extends AppCompatActivity {
    public static final String LOG_TAG = "myLogs";
    CameraService[] myCameras = null;


    private CameraManager mCameraManager    = null;
    private final int CAMERA1   = 0;
    private final int CAMERA2   = 1;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler = null;

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
    private void stopBackgroundThread(){
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }


   @RequiresApi(api = Build.VERSION_CODES.M) // Проверка версии Android

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  Запрос на использования камеры в общем патоки (Могут быть проблемы при создание activity!!!)

       Log.d(LOG_TAG,"Запрашиваем разрешение");
       if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
               ||
               (ContextCompat.checkSelfPermission(CameraSource.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
       {
           requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
       }


   }
    @Override
    public void onPause() {
       //if(myCameras[CAMERA1].isOpen()){myCameras[CAMERA1].closeCamera();}
        //if(myCameras[CAMERA2].isOpen()){myCameras[CAMERA2].closeCamera();}
        stopBackgroundThread();
        super.onPause();
    }
    /// Полная остоновка камеры
    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();


    }

    }

