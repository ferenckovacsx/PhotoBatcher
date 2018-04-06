package com.ferenckovacsx.photobatcher.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ferenckovacsx.photobatcher.R;
import com.ferenckovacsx.photobatcher.tools.DatabaseTools;
import com.ferenckovacsx.photobatcher.tools.PermissionsDelegate;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

import static com.ferenckovacsx.photobatcher.tools.Utilities.generateName;
import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;


public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CAMERA ACTIVITY";

    private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate(this);
    private boolean hasCameraPermission;
    private CameraView cameraView;
    private FocusView focusView;
    private TextView counterTextView;
    private ImageView captureButton, doneButton, thumbnailPreview, counterImageBackground, flashSwitch;

    boolean isFlashOpen = false;

    File file;

    private Fotoapparat fotoapparat;

    boolean activeCameraBack = true;

//    private CameraConfiguration cameraConfiguration = CameraConfiguration
//            .builder()
//            .photoResolution(standardRatio(
//                    highestResolution()
//            ))
//            .focusMode(firstAvailable(
//                    continuousFocusPicture(),
//                    autoFocus(),
//                    fixed()
//            ))
//            .flash(firstAvailable(
//                    autoRedEye(),
//                    autoFlash(),
//                    torch(),
//                    off()
//            ))
//            .previewFpsRange(highestFps())
//            .sensorSensitivity(highestSensorSensitivity())
//            .frameProcessor(new SampleFrameProcessor())
//            .build();

    int imageCount = 0;
    DatabaseTools databaseTools;

    boolean success;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera2Preview);
        focusView = findViewById(R.id.focusView);
        captureButton = findViewById(R.id.btn_camera2_takepicture);
        doneButton = findViewById(R.id.camera2_btn_done);
        thumbnailPreview = findViewById(R.id.camera2_capture_thumbnail);
        counterTextView = findViewById(R.id.camera2_counter_textview);
        counterImageBackground = findViewById(R.id.camera2_counter_background);
        flashSwitch = findViewById(R.id.torchSwitch);

//        flashSwitch.setSelected(false);

        databaseTools = new DatabaseTools(CameraActivity.this);

        hasCameraPermission = permissionsDelegate.hasCameraPermission();
        if (hasCameraPermission) {
            cameraView.setVisibility(View.VISIBLE);
        } else {
            permissionsDelegate.requestCameraPermission();
        }

        fotoapparat = createFotoapparat();

        takePictureOnClick();


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(CameraActivity.this, GalleryActivity.class);
                startActivity(intent);

                CameraActivity.this.finish();

            }
        });


//        switchCameraOnClick();
        toggleTorchOnSwitch();
        zoomSeekBar();
    }

    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .focusView(focusView)
                .previewScaleType(ScaleType.CenterCrop)
                .photoResolution(ResolutionSelectorsKt.highestResolution())
                .frameProcessor(new SampleFrameProcessor())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {
                        Toast.makeText(CameraActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();
    }

    private void zoomSeekBar() {
        SeekBar seekBar = findViewById(R.id.zoomSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fotoapparat.setZoom(progress / (float) seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    //    private void switchCameraOnClick() {
//        View switchCameraButton = findViewById(R.id.switchCamera);
//
//        boolean hasFrontCamera = fotoapparat.isAvailable(front());
//
//        switchCameraButton.setVisibility(
//                hasFrontCamera ? View.VISIBLE : View.GONE
//        );
//
//        if (hasFrontCamera) {
//            switchCameraOnClick(switchCameraButton);
//        }
//    }
//
    private void toggleTorchOnSwitch() {

        flashSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFlashOpen) {
                    isFlashOpen = false;
                    flashSwitch.setSelected(false);
                } else {
                    isFlashOpen = true;
                    flashSwitch.setSelected(true);
                }

                fotoapparat.updateConfiguration(UpdateConfiguration.builder()
                        .flash(isFlashOpen ? torch() : off())
                        .build()
                );
            }
        });
    }

//
//    private void switchCameraOnClick(View view) {
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activeCameraBack = !activeCameraBack;
//                fotoapparat.switchTo(
//                        activeCameraBack ? back() : front(),
//                        cameraConfiguration
//                );
//            }
//        });
//    }

    private void takePictureOnClick() {
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color = Color.parseColor("#505050");
                captureButton.setColorFilter(color);
                captureButton.setEnabled(false);

                takePicture();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int color = Color.parseColor("#000000");
                        captureButton.setColorFilter(color);
                        captureButton.setEnabled(true);
                    }
                }, 1000);
            }
        });
    }

    private void takePicture() {


//        File tempDir = new File(getFilesDir() + "/PhotoBatcherTemp");
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/PhotoBatcherTemp");
        tempDir.mkdirs();

        String tempDirString = tempDir.getPath();
        final String fileNameString = generateName("IMG");

        file = new File(tempDirString, fileNameString);
        final String completeFilePath = file.getPath();


        PhotoResult photoResult = fotoapparat.takePicture();

        photoResult.saveToFile(file);


        photoResult
                .toBitmap()
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto == null) {
                            Log.e(TAG, "Couldn't capture photo.");
                            return;
                        }

                        //add new image to batch database
                        databaseTools.insertNewScore(fileNameString, completeFilePath);

//                        thumbnailPreview.setImageBitmap(bitmapPhoto.bitmap);

                        Uri uri = Uri.fromFile(new File(completeFilePath));
                        Picasso.with(CameraActivity.this)
                                .load(uri)
                                .fit()
                                .into(thumbnailPreview);

                        //make done button and counter visible
                        imageCount += 1;
                        counterTextView.setText(String.valueOf(imageCount));
                        if (imageCount > 0) {
                            doneButton.setVisibility(View.VISIBLE);
                            counterImageBackground.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission) {
            fotoapparat.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasCameraPermission) {
            fotoapparat.stop();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fotoapparat.start();
        fotoapparat.stop();
        fotoapparat.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            hasCameraPermission = true;
            fotoapparat.start();
            cameraView.setVisibility(View.VISIBLE);
        }
    }

    private class SampleFrameProcessor implements FrameProcessor {
        @Override
        public void process(@NotNull Frame frame) {
            // Perform frame processing, if needed
        }
    }


}
