package com.panasonic.automotive.beanssample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.TextView;

import com.honda.displayaudio.system.vehiclesignal.IVehicleSignalCallback;
import com.honda.displayaudio.system.vehiclesignal.IVehicleSignalService;
import com.honda.displayaudio.system.vehiclesignal.VehicleSignal;
import com.honda.displayaudio.system.vehiclesignal.VehicleSignalNotificationType;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "<<<BeansSample>>>" + MainActivity.class.getSimpleName();
    
    private IVehicleSignalService mVehicleSignalService;
    private final Handler mHandler = new Handler();
    
    private static final int[] SIGNAL_ID_LIST = new int[]{
        VehicleSignal.AHB_REQ_PRESSURE,
        VehicleSignal.STR_ANGLE
    };
    
    private static int[] NOTIFICATION_LIST = new int[]{
        VehicleSignalNotificationType.SIGNAL_VALUE_REFRESHED,
        VehicleSignalNotificationType.SIGNAL_VALUE_CHANGED,
    };
    
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mVehicleSignalService = IVehicleSignalService.Stub.asInterface( service );
            registerCallback();
            getInitSignalValues();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    
    private IVehicleSignalCallback mVehicleSignalCallback = new IVehicleSignalCallback.Stub() {
        @Override
        public void onNotify(final int signalId, final String value) throws RemoteException {
            Log.d( TAG, "onNotify() called with: signalId = [" + signalId + "], value = [" + value + "]" );
            mHandler.post( new Runnable() {
                @Override
                public void run() {
                    switch (signalId) {
                        case VehicleSignal.AHB_REQ_PRESSURE:
                            TextView textView1 = findViewById( R.id.tv_001 );
                            textView1.setText( value );
                            break;
                        case VehicleSignal.STR_ANGLE:
                            TextView textView2 = findViewById( R.id.tv_002 );
                            textView2.setText( value );
                            break;
                        default:
                            break;
                    }
                }
            } );
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        
        bindService();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterCallback();
        unbindService();
    }
    
    void bindService() {
        Intent intent = new Intent( "com.honda.displayaudio.system.vehiclesignal.IVehicleSignalService" );
        intent.setComponent( new ComponentName( "com.honda.displayaudio.system.beansapi",
            "com.honda.displayaudio.system.vehiclesignal.VehicleSignalService" ) );
        bindService( intent, mConn, Context.BIND_AUTO_CREATE );
    }
    
    void unbindService() {
        unbindService( mConn );
    }
    
    void registerCallback() {
        if (mVehicleSignalService != null) {
            try {
                mVehicleSignalService.registerCallback( SIGNAL_ID_LIST, NOTIFICATION_LIST, mVehicleSignalCallback );
            } catch (RemoteException e) {
                Log.w( TAG, e.getMessage() );
            }
        }
    }
    
    void unregisterCallback() {
        if (mVehicleSignalService != null) {
            try {
                mVehicleSignalService.unregisterCallback( mVehicleSignalCallback );
            } catch (RemoteException e) {
                Log.w( TAG, e.getMessage() );
            }
        }
    }
    
    void getInitSignalValues() {
        if (mVehicleSignalService != null) {
            try {
                String[] values = mVehicleSignalService.getSignals( SIGNAL_ID_LIST );
                if (values == null) {
                    Log.d( TAG, "getSignals is null!!!!!!!!" );
                } else {
                    for (int i = 0; i < SIGNAL_ID_LIST.length; i++) {
                        switch (SIGNAL_ID_LIST[i]) {
                            case VehicleSignal.AHB_REQ_PRESSURE:
                                TextView textView1 = findViewById( R.id.tv_001 );
                                textView1.setText( values[i] );
                                break;
                            case VehicleSignal.STR_ANGLE:
                                TextView textView2 = findViewById( R.id.tv_002 );
                                textView2.setText( values[i] );
                                break;
                            default:
                                break;
                        }
                    }
                }
                
            } catch (RemoteException e) {
                Log.w( TAG, e.getMessage() );
            }
        }
    }
}
