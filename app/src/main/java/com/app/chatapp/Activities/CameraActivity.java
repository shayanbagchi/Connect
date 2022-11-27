package com.app.chatapp.Activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.app.chatapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    PreviewView previewViewCamera;
    ImageCapture imageCapture;
    ProcessCameraProvider cameraProvider = null;
    CameraSelector cameraSelector;
    ImageButton imageCaptureButton;
    TabLayout tabLayout;

    float  x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initializeViewElements();

        if (checkPermission()) {
            startCameraForImage();
        } else {
            requestPermissions(new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void initializeViewElements() {
        previewViewCamera = findViewById(R.id.viewFinder);
        imageCaptureButton = findViewById(R.id.imageCaptureButton);
        imageCaptureButton.setOnClickListener(this);
        tabLayout = findViewById(R.id.tabLayout);
    }

    boolean checkPermission() {
        Log.d("PERMISSION", "DEVICE ANDROID M");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (checkSelfPermission(Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        } else return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void startCameraForImage() {
        initializeCameraProvider();

        Preview preview = new Preview.Builder().build();
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        imageCapture = new ImageCapture.Builder().setTargetRotation(
                (int) previewViewCamera.getRotation()).build();

        preview.setSurfaceProvider(previewViewCamera.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
    }

    private void initializeCameraProvider() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        } else {
            ListenableFuture < ProcessCameraProvider > cameraProviderFuture =
                    ProcessCameraProvider.getInstance(this);
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "CAMERA GRANTED");
            } else {
                Log.d("PERMISSION", "CAMERA DENIED");
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "STORAGE GRANTED");
            } else {
                Log.d("PERMISSION", "STORAGE DENIED");
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageCaptureButton) {
            captureImage();
        }
    }

    private void captureImage() {
        String nameTimeStamp = "Image_" + System.currentTimeMillis();
        String name = nameTimeStamp + ".jpeg";
        ImageCapture.OutputFileOptions outputFileOptions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, nameTimeStamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.ORIENTATION, 90);

            outputFileOptions = new ImageCapture.OutputFileOptions.Builder(this.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build();

        } else {
            File mImageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Connect");
            boolean isDirectoryCreated = mImageDir.exists() || mImageDir.mkdirs();
            if (isDirectoryCreated) {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES) + File.separator +
                        "Connect", name);

                outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            }
        }

        assert outputFileOptions != null;
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.e("IMAGE", "Image Captured Successfully");
                        Uri savedUri = outputFileResults.getSavedUri();
                        if (savedUri != null) {
                            Log.i("IMAGE", "Saved Image at " + savedUri);
                        }
                        startImageViewActivity(savedUri, name);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("IMAGE", "Image Capture Failed: " + exception);
                        Toast.makeText(CameraActivity.this, "Image Capture Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startImageViewActivity(Uri savedUri, String imageName) {
        Intent imageViewIntent = new Intent(CameraActivity.this, ImageViewActivity.class);
        imageViewIntent.putExtra("IMAGE_URI", savedUri);
        imageViewIntent.putExtra("IMAGE_NAME", imageName);
        startActivity(imageViewIntent);
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 > x2){
                    Intent i = new Intent(CameraActivity.this, HomeActivity.class);
                    startActivity(i);
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(CameraActivity.this, HomeActivity.class);
        startActivity(i);
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();
        finish();
    }
}