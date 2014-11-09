package cz.czechhackathon.Panchromious;

import android.app.Activity;
import android.bluetooth.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import cz.czechhackathon.Panchromious.rest.ColorRGBGet;
import cz.czechhackathon.Panchromious.rest.RGBColor;
import org.json.JSONArray;


import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panchromious extends Activity implements SurfaceHolder.Callback  {

    private Camera camera;
    private int cameraId = 0;

    byte[] buffer;
    ImageButton identify;
    Camera.Size previewSize;
    TextView colorResult;
    FrameLayout selectedColorFrame;
    ImageView btIcon;

    boolean btScanning = false;
    boolean btConnected = false;
    private BluetoothLeService btService;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private String btMessage = "";

    RGBColor selectedColor;

    Webb webb;

    private BluetoothAdapter btAdapter;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        webb = Webb.create();
        webb.setBaseUri(App.API);

        selectedColorFrame = (FrameLayout)findViewById(R.id.selectedColorFrame);
        colorResult = (TextView)findViewById(R.id.colorResult);
        identify = (ImageButton)findViewById(R.id.identify);
        btIcon = (ImageView)findViewById(R.id.btIcon);

        colorResult.setVisibility(View.INVISIBLE);

        identify.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0) {
                Log.v("IDENTIFY", "clicked");

                new GetColorNameTask().execute(selectedColor);
            }
        });

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceCallback, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        startBt();
        startCamera();

        registerReceiver(gattCallback, makeGattUpdateIntentFilter());

    }


    @Override
    public void onPause() {
        super.onPause();

        stopCamera();
        stopBt();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void startBt() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("NOTE", "BT LE not available on this device");
            return;
        }
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btAdapter.startLeScan(btScanCallback);
        btScanning = true;
    }

    private void stopBt() {
        if (btAdapter != null) {
            if (btScanning) {
                btAdapter.stopLeScan(btScanCallback);
            }
        }
    }

    private void setPreviewSize(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        Camera.Size cs = params.getPreviewSize();
        double ar = (double)cs.width / (double)cs.height;

        FrameLayout frame =  (FrameLayout)findViewById(R.id.previewFrame);
        SurfaceView surface = (SurfaceView)findViewById(R.id.cameraPreview);
        Log.v("PREVIEW", "frame: " + surface.getWidth() + " x " + frame.getHeight());

        ViewGroup.LayoutParams fp = frame.getLayoutParams();
        ViewGroup.LayoutParams sp = surface.getLayoutParams();

        if (fp.height * ar > fp.width) {

        }
        surface.setLayoutParams(sp);
    }

    private void startCamera() {
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
            setCameraDisplayOrientation(cameraId, camera);

            SurfaceView cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
            SurfaceHolder holder = cameraPreview.getHolder();
            holder.addCallback(this);

            // setPreviewSize(camera);

            camera.startPreview();
            Log.v("CAMERA", "camera running");
        }
        catch (IOException e) {
            Log.e("ERROR", e.getMessage());
            App.toast(e.getMessage(), Toast.LENGTH_LONG);
            camera = null;
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

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void stopCamera() {
        if (camera != null) {
            camera.release();
        }
    }



    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Camera.Parameters params = camera.getParameters();

            int bpp = ImageFormat.getBitsPerPixel(params.getPreviewFormat());
            previewSize = params.getPreviewSize();
            int bs = (previewSize.width * previewSize.height * bpp) / 8 + 1;
            Log.v("SUIZE", "allocating for " + previewSize.width + " " + previewSize.height + " = " + bs);
            buffer = new byte[bs];
            camera.addCallbackBuffer(buffer);
            camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    computeSelectedColor();
                    camera.addCallbackBuffer(buffer);
                }
            });

            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (IOException e) {
            App.toast("Chyba pri spusteni preview", Toast.LENGTH_LONG);
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void computeSelectedColor() {
     /* Rotation involves accessing either the source or destination pixels in a
       non-sequential fashion. Since the source is smaller, I figure it's less
       cache-unfriendly to go jumping around that. */

        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;
        int samples = 0;
        int width = previewSize.width;
        int height = previewSize.height;

        final int frameSize = width * height;

        for (int row = (height * 2) / 5; row <= (height * 3) / 5; row++) {
            int yp = row  * width;
            int uvp = frameSize + (row / 2) * width;
            int u = 0;
            int v = 0;
            for (int col = 0; col < width; col++, yp++) {
                if ((col % 2) == 0) {
                    v = (0xff & buffer[uvp++]) - 128;
                    u = (0xff & buffer[uvp++]) - 128;
                }
                // Yeah, I know, it would be better to just limit the ranges in the for statements.
                // But I iz tired now and have no mental capacity to correctly adjust yp and
                // consider what happens when active area starts at an odd value.

                if (col < (width * 2)/5 || col > (width * 3)/5) {
                    continue;
                }

                int y = (0xff & ((int) buffer[yp])) - 16;
                if (y < 0)
                    y = 0;

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                sumRed += ((r > 0 ? r : 0) >> 10) & 0xff;
                sumGreen += ((g > 0 ? g : 0) >> 10) & 0xff;
                sumBlue += ((b > 0 ? b : 0) >> 10) & 0xff;
                samples++;
            }
        }

        int avgRed = sumRed / samples;
        int avgGreen = sumGreen / samples;
        int avgBlue = sumBlue / samples;
        selectedColorFrame.setBackgroundColor(0xff000000 | avgRed << 16 | avgGreen << 8 | avgBlue);
        selectedColor = new RGBColor(avgRed, avgGreen, avgBlue);
        identify.setEnabled(true);
    }

    private void updateConnectionState(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btIcon.setVisibility(connected ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    class GetColorNameTask extends AsyncTask<RGBColor, Void, ColorRGBGet> {
        protected ColorRGBGet doInBackground(RGBColor... colors) {
            RGBColor c = colors[0];
            String resource = String.format("/color/rgb/%d/%d/%d", c.red, c.green, c.blue);
            Log.v("URI", resource);


            try {
                Request req = webb.get(resource);
                Response<JSONArray> response = req.asJsonArray();
                Log.d("RESPONSE", response.toString());
                return new ColorRGBGet(response.getBody().getJSONObject(0));
            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }

            return null;
        }


        protected void onPostExecute(ColorRGBGet resp) {
            Log.v("RESPONSE", String.format("%s: %d %d %d", resp.name, resp.color.red, resp.color.green, resp.color.blue));
            RGBColor color = resp.color;
            colorResult.setBackgroundColor(color.toInt());
            String capitalized = resp.name.substring(0, 1).toUpperCase() + resp.name.substring(1);
            colorResult.setText(capitalized);
            int brightness = color.red + color.green + color.blue;
            int textColor = brightness > 3*127 ? 0xff000000 : 0xffffffff;
            colorResult.setTextColor(textColor);
            colorResult.setVisibility(View.VISIBLE);
        }
    }

    private BluetoothAdapter.LeScanCallback btScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (btService != null
                                    && device != null
                                    && device.getName() != null
                                    && device.getName().equals("Panchrom.io")
                                    && device.getAddress() != null) {
                                Log.v("CONNECTING", device.getName() + "@" + device.getAddress());
                                btService.connect(device.getAddress());
                            }
                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();

                        }
                    });
                }
            };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver gattCallback = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.v("INFO", "BT device connected");
                btConnected = true;
                updateConnectionState(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.v("INFO", "BT device disconnected");
                btConnected = false;
                updateConnectionState(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.v("INFO", "BT services discovered");
                List<BluetoothGattService> gattServices = btService.getSupportedGattServices();
                if (gattServices == null) return;
                for (BluetoothGattService gattService : gattServices) {
                    characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
                    characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
                }
                // hit enter to make Arduino happy
                characteristicTX.setValue("\n");
                btService.writeCharacteristic(characteristicTX);
                btService.setCharacteristicNotification(characteristicRX, true);

                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.v("INFO", "BT data available: " + intent.getStringExtra(btService.EXTRA_DATA));
                btMessage = btMessage + intent.getStringExtra(btService.EXTRA_DATA);
                if (btMessage.endsWith("\n")) {
                    Log.v("INFO", "Command complete: " + btMessage);
                    processBtCommand(btMessage);
                    btMessage = "";
                }


            }
        }
    };

    private final ServiceConnection serviceCallback = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            btService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!btService.initialize()) {
                Log.e("ERROR", "Unable to initialize Bluetooth Service");
                finish();
            }
            Log.v("INFO", "BT Service initialized");
            // Automatically connects to the device upon successful start-up initialization.
            //btService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            btService = null;
        }
    };

    static final Pattern cmdPattern = Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$");

    private void processBtCommand(String command) {
        Matcher m = cmdPattern.matcher(command);
        if (m.matches()) {
            Log.v("INFO", "command syntax ok: " + command);
            final int red = Integer.parseInt(m.group(1));
            final int green = Integer.parseInt(m.group(2));
            final int blue = Integer.parseInt(m.group(3));
            Log.v("INFO", "command parsed: " + red + "," + green + "," + blue);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetColorNameTask().execute(new RGBColor(red,green,blue));
                }
            });

        }
        else {
            Log.v("INFO", "invalid command syntax: \"" + command + "\"");
        }
    }

}




