package com.example.myapplication.camera;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.Arrays;


public class CameraService extends CameraSource {
    private TextureView mImageView = null; // хронит всебе текстуру на ложения
    private CameraManager mCameraManager = null;
    private String mCameraID; // Номер камеры 0 или 1
    private CameraDevice mCameraDevice = null; // хранит все id камеры
    private CameraCaptureSession mCaptureSession;
    private ImageReader mImageReader;
    private String LOG_TAG;
    private  Handler mBackgroundHandler = null;
    private HandlerThread mBackgroundThread;


    // Смотрит на версию Android



    public CameraService(CameraManager cameraManager, String cameraID){

        mCameraManager  = cameraManager;

        mCameraID = cameraID;
    }
    // метод делать фотографии
    public  void makePhoto(){

    }
   /// Жизненый цикел камеры

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback(){
        @Override
        public void onOpened(CameraDevice camera){
            mCameraDevice = camera;
            Log.i(LOG_TAG,"Окрытие камеры:"+mCameraDevice.getId());
            createCameraPreviewSession(); // Настройки камеры
        }

        @Override // Отключение камеры
        public void onDisconnected( CameraDevice camera){
            mCameraDevice.close();
            Log.i(LOG_TAG,"Отключение камеры id:"+mCameraDevice.getId());
            mCameraDevice = null;
        }
        // Ошибки при открытие
        @Override
        public void onError(CameraDevice camera,int error){

        }

    };



    private void createCameraPreviewSession (){
        mImageReader = ImageReader.newInstance(192,1080, ImageFormat.JPEG, 1);

        SurfaceTexture texture = mImageView.getSurfaceTexture(); // Если activity не загрузилось будет пустое окно
        texture.setDefaultBufferSize(1920,1080);
        Surface surface =new Surface(texture);

        try {
         final CaptureRequest.Builder builder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
         builder.addTarget(surface);



         mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                 new CameraCaptureSession.StateCallback() {
                     @Override
                     public void onConfigured(@NonNull CameraCaptureSession session) {
                         mCaptureSession = session;
                         try {
                             mCaptureSession.setRepeatingRequest(builder.build(),null, mBackgroundHandler );
                         }  catch (CameraAccessException e){
                             e.printStackTrace();
                         }
                     }

                     @Override
                     public void onConfigureFailed(@NonNull CameraCaptureSession session) {}}, mBackgroundHandler);
                 } catch (CameraAccessException e) {
            e.printStackTrace();
                        }
        }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera() {
        try {

            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {


                mCameraManager.openCamera(mCameraID,mCameraCallback,mBackgroundHandler);

            }



        } catch (CameraAccessException e) {
            Log.i(LOG_TAG,e.getMessage());

        }
    }

    public void closeCamera() {

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }
        }



