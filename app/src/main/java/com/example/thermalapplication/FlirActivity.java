/*
 * ******************************************************************
 * @title FLIR THERMAL SDK
 * @file FlirActivity.java
 * @Author FLIR Systems AB
 *
 * @brief  Main UI of test application
 *
 * Copyright 2019:    FLIR Systems
 * ******************************************************************/
package com.example.thermalapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.flir.thermalsdk.ErrorCode;
import com.flir.thermalsdk.androidsdk.ThermalSdkAndroid;
import com.flir.thermalsdk.androidsdk.live.connectivity.UsbPermissionHandler;
import com.flir.thermalsdk.live.CommunicationInterface;
import com.flir.thermalsdk.live.Identity;
import com.flir.thermalsdk.live.connectivity.ConnectionStatusListener;
import com.flir.thermalsdk.live.discovery.DiscoveryEventListener;
import com.flir.thermalsdk.log.ThermalLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sample application for scanning a FLIR ONE or a built in emulator
 * <p>
 * See the {@link CameraHandler} for how to preform discovery of a FLIR ONE camera, connecting to it and start streaming images
 * <p>
 * The FlirActivity is primarily focused to "glue" different helper classes together and updating the UI components
 * <p/>
 * Please note, this is <b>NOT</b> production quality code, error handling has been kept to a minimum to keep the code as clear and concise as possible
 */
public class FlirActivity extends AppCompatActivity {

    private static final String TAG = "FlirActivity";

    //Handles Android permission for eg Network
    private PermissionHandler permissionHandler;

    //Handles network camera operations
    private CameraHandler cameraHandler;
    private Button connectButton;
    private Button disconnectButton;
    private Button startDiscoveryButton;
    private Button flirScreenshotButton;

    private Identity connectedIdentity = null;
    private TextView discoveryStatus;

    private ImageView msxImage;
    private ImageView photoImage;

    private final LinkedBlockingQueue<FrameDataHolder> framesBuffer = new LinkedBlockingQueue<>(21);
    private final UsbPermissionHandler usbPermissionHandler = new UsbPermissionHandler();

    /**
     * Show message on the screen
     */
    public interface ShowMessage {
        void show(String message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flir);
        this.connectButton = findViewById(R.id.connect_flir_one);
        this.disconnectButton = findViewById(R.id.disconnect_any);
        this.startDiscoveryButton = findViewById(R.id.start_discovery);
        this.flirScreenshotButton = findViewById(R.id.flir_screenshot_button);

        ThermalLog.LogLevel enableLoggingInDebug = BuildConfig.DEBUG ? ThermalLog.LogLevel.DEBUG : ThermalLog.LogLevel.NONE;

        //ThermalSdkAndroid has to be initiated from a Activity with the Application Context to prevent leaking Context,
        // and before ANY using any ThermalSdkAndroid functions
        //ThermalLog will show log from the Thermal SDK in standards android log framework
        ThermalSdkAndroid.init(getApplicationContext(), enableLoggingInDebug);

        permissionHandler = new PermissionHandler(showMessage, FlirActivity.this);

        cameraHandler = new CameraHandler();

        setupViews();

        this.flirScreenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectButton.getText().toString().equalsIgnoreCase("Connected")) {
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Always close the connection with a connected FLIR ONE when going into background
        disconnect();
    }

    public void startDiscovery(View view) {
        startDiscovery();
    }

    public void stopDiscovery(View view) {
        stopDiscovery();
    }

    public void connectFlirOne(View view) {
        connect(cameraHandler.getFlirOne());
    }

    public void connectSimulatorOne(View view) {
        connect(cameraHandler.getCppEmulator());
    }

    public void connectSimulatorTwo(View view) {
        connect(cameraHandler.getFlirOneEmulator());
    }

    public void disconnect(View view) {
        disconnect();
    }

    public void performNuc(View view) {
        cameraHandler.performNuc();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    public Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                          @NonNull final Bitmap.CompressFormat format,
                          @NonNull final String mimeType,
                          @NonNull final String displayName) throws IOException {

        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");

                if (!bitmap.compress(format, 95, stream))
                    throw new IOException("Failed to save bitmap.");
            }

            return uri;
        }
        catch (IOException e) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
    }

    private void saveImage() throws IOException {

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) msxImage.getDrawable());
        Bitmap bitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        String fileName = String.format("thermal_picture%s.png", date);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmap(FlirActivity.this, bitmap, Bitmap.CompressFormat.PNG, "image/png", fileName);
        }
        Toast.makeText(FlirActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handle Android permission request response for Bluetooth permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = ["
                + Arrays.toString(permissions) + "], grantResults = [" + Arrays.toString(grantResults) + "]");
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Connect to a Camera
     */
    private void connect(Identity identity) {
        //We don't have to stop a discovery but it's nice to do if we have found the camera that we are looking for
        cameraHandler.stopDiscovery(discoveryStatusListener);

        if (connectedIdentity != null) {
            Log.d(TAG, "connect(), in *this* code sample we only support one camera connection at the time");
            showMessage.show("connect(), in *this* code sample we only support one camera connection at the time");
            return;
        }

        if (identity == null) {
            Log.d(TAG, "connect(), can't connect, no camera available");
            showMessage.show("connect(), can't connect, no camera available");
            return;
        }

        connectedIdentity = identity;

        updateConnectionText(identity, "CONNECTING");
        //IF your using "USB_DEVICE_ATTACHED" and "usb-device vendor-id" in the Android Manifest
        // you don't need to request permission, see documentation for more information
        if (UsbPermissionHandler.isFlirOne(identity)) {
            usbPermissionHandler.requestFlirOnePermisson(identity, this, permissionListener);
        } else {
            doConnect(identity);
        }

    }

    private final UsbPermissionHandler.UsbPermissionListener permissionListener = new UsbPermissionHandler.UsbPermissionListener() {
        @Override
        public void permissionGranted(Identity identity) {
            doConnect(identity);
        }

        @Override
        public void permissionDenied(Identity identity) {
            FlirActivity.this.showMessage.show("Permission was denied for identity ");
        }

        @Override
        public void error(UsbPermissionHandler.UsbPermissionListener.ErrorType errorType, final Identity identity) {
            FlirActivity.this.showMessage.show("Error when asking for permission for FLIR ONE, error:" + errorType + " identity:" + identity);
        }
    };

    private void doConnect(Identity identity) {
        new Thread(() -> {
            try {
                cameraHandler.connect(identity, connectionStatusListener);
                runOnUiThread(() -> {
                    updateConnectionText(identity, "Connected");
                    connectButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                    connectButton.setEnabled(false);
                });
                cameraHandler.startStream(streamDataListener);
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Could not connect: " + e);
                    updateConnectionText(identity, "DISCONNECTED");
                    connectButton.setBackgroundColor(Color.parseColor("#FF000000"));
                    connectButton.setEnabled(true);
                });
            }
        }).start();
    }

    /**
     * Disconnect to a camera
     */
    private void disconnect() {
        updateConnectionText(connectedIdentity, "DISCONNECTING");
        Log.d(TAG, "disconnect() called with: connectedIdentity = [" + connectedIdentity + "]");
        connectedIdentity = null;
        new Thread(() -> {
            cameraHandler.disconnect();
            runOnUiThread(() -> {
                updateConnectionText(null, "Connect");
                connectButton.setBackgroundColor(Color.parseColor("#FF000000"));
                connectButton.setEnabled(true);
            });
        }).start();
    }

    /**
     * Update the UI text for connection status
     */
    private void updateConnectionText(Identity identity, String status) {
        connectButton.setText(status);
    }

    /**
     * Start camera discovery
     */
    private void startDiscovery() {
        cameraHandler.startDiscovery(cameraDiscoveryListener, discoveryStatusListener);
    }

    /**
     * Stop camera discovery
     */
    private void stopDiscovery() {
        cameraHandler.stopDiscovery(discoveryStatusListener);
    }

    /**
     * Callback for discovery status, using it to update UI
     */
    private final CameraHandler.DiscoveryStatus discoveryStatusListener = new CameraHandler.DiscoveryStatus() {
        @Override
        public void started() {
            startDiscoveryButton.setText(getString(R.string.connection_status_text, "Discovering"));
            startDiscoveryButton.setBackgroundColor(Color.parseColor("#4CAF50"));
        }

        @Override
        public void stopped() {
            startDiscoveryButton.setText(getString(R.string.connection_status_text, "Discover"));
            startDiscoveryButton.setBackgroundColor(Color.parseColor("#FF000000"));
        }
    };

    /**
     * Camera connecting state thermalImageStreamListener, keeps track of if the camera is connected or not
     * <p>
     * Note that callbacks are received on a non-ui thread so have to eg use {@link #runOnUiThread(Runnable)} to interact view UI components
     */
    private final ConnectionStatusListener connectionStatusListener = new ConnectionStatusListener() {
        @Override
        public void onDisconnected(@org.jetbrains.annotations.Nullable ErrorCode errorCode) {
            Log.d(TAG, "onDisconnected errorCode:" + errorCode);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateConnectionText(connectedIdentity, "DISCONNECTED");
                }
            });
        }
    };

    private final CameraHandler.StreamDataListener streamDataListener = new CameraHandler.StreamDataListener() {

        @Override
        public void images(FrameDataHolder dataHolder) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msxImage.setImageBitmap(dataHolder.msxBitmap);
                    photoImage.setImageBitmap(dataHolder.dcBitmap);
                }
            });
        }

        @Override
        public void images(Bitmap msxBitmap, Bitmap dcBitmap) {

            try {
                framesBuffer.put(new FrameDataHolder(msxBitmap, dcBitmap));
            } catch (InterruptedException e) {
                //if interrupted while waiting for adding a new item in the queue
                Log.e(TAG, "images(), unable to add incoming images to frames buffer, exception:" + e);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "framebuffer size:" + framesBuffer.size());
                    FrameDataHolder poll = framesBuffer.poll();
                    msxImage.setImageBitmap(Objects.requireNonNull(poll).msxBitmap);
                    photoImage.setImageBitmap(Objects.requireNonNull(poll).dcBitmap);
                }
            });

        }
    };

    /**
     * Camera Discovery thermalImageStreamListener, is notified if a new camera was found during a active discovery phase
     * <p>
     * Note that callbacks are received on a non-ui thread so have to eg use {@link #runOnUiThread(Runnable)} to interact view UI components
     */
    private final DiscoveryEventListener cameraDiscoveryListener = new DiscoveryEventListener() {
        @Override
        public void onCameraFound(Identity identity) {
            Log.d(TAG, "onCameraFound identity:" + identity);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cameraHandler.add(identity);
                }
            });
        }

        @Override
        public void onDiscoveryError(CommunicationInterface communicationInterface, ErrorCode errorCode) {
            Log.d(TAG, "onDiscoveryError communicationInterface:" + communicationInterface + " errorCode:" + errorCode);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopDiscovery();
                    FlirActivity.this.showMessage.show("onDiscoveryError communicationInterface:" + communicationInterface + " errorCode:" + errorCode);
                }
            });
        }
    };

    private final ShowMessage showMessage = new ShowMessage() {
        @Override
        public void show(String message) {
            Toast.makeText(FlirActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void setupViews() {
        discoveryStatus = findViewById(R.id.discovery_status);
        msxImage = findViewById(R.id.msx_image);
        photoImage = findViewById(R.id.photo_image);
    }

}
