package cz.czechhackathon.Panchromious;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;


import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cz.czechhackathon.Panchromious.Dbg;

import java.io.IOException;

public class Panchromious extends Activity implements SurfaceHolder.Callback {

    private Camera camera;
    private int cameraId = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button identify = (Button)findViewById(R.id.identify);
        identify.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {
                Dbg.v("IDENTIFY", "clicked");
                startit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startit() {
        // do we have a camera?

        try {
            if (!getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                throw new IOException("I HAZ NO CAMERAZ 8(");
            }

            cameraId = findCamera(CameraInfo.CAMERA_FACING_BACK);
            cameraId = cameraId >= 0 ? cameraId : findCamera(CameraInfo.CAMERA_FACING_FRONT);

            if (cameraId < 0) {
                throw new IOException("I HAZ NO FRONT OR BACK CAMERAZ 8(.");
            }
            camera = Camera.open(cameraId);
            Parameters parameters = camera.getParameters();

            SurfaceView cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
            SurfaceHolder holder = cameraPreview.getHolder();
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            camera.setPreviewDisplay(holder);
            //holder.addCallback(this);

            camera.startPreview();
            Dbg.v("CAMERA", "camera running");
        }
        catch (IOException e) {
            Dbg.e("ERROR", e.getMessage());
            App.toast(e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private int findCamera(int facing) {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                Log.d("CAMERA", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
}
