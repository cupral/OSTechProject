package anzai.ostechnology.co.jp.birdhumancontest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textView, textInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        textInfo = findViewById(R.id.text_info);
        // Get an instance of the TextView
        textView = findViewById(R.id.text_view);

        Button luButton = findViewById(R.id.letUp);
        Button ruButton = findViewById(R.id.rightUp);


        luButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //左上を触った時の処理
                try {
                    Thread.sleep(3000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("debug","緊張はするけど、風は読めそう。ちゃんと飛べるかな？");
                try {
                    Thread.sleep(5000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("debug","だよね。僕もしっかりアシストするよ。楽しみだね。");
                try {
                    Thread.sleep(4000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("debug","落ち着かせよう。シミュレーションしておくね。");

            }
        });


        ruButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //右上を触った時の処理
                try {
                    Thread.sleep(3000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("debug","いよいよだね。今の風の状態は悪くなさそう。行きましょうか。右は準備OK？");
                try {
                    Thread.sleep(2000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("debug","左は？");
                try {
                    Thread.sleep(2000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("debug","(パイロット名)は？");
                try {
                    Thread.sleep(2000); //3000ミリ秒Sleepする
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("debug","よし！3,2,1,GO~!");
            }
        });


    }




    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","==onResume==");
        // Listenerの登録
        Sensor accel = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
    }

    // 解除するコードも入れる!
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","==onPause==");
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("debug","==onSensorChanged==");
        float sensorX, sensorY, sensorZ;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.d("debug","==event.sensor.getType()==");
            sensorX = event.values[0];
            sensorY = event.values[1];
            sensorZ = event.values[2];

            String strTmp = "加速度センサー\n"
                    + " X: " + sensorX + "\n"
                    + " Y: " + sensorY + "\n"
                    + " Z: " + sensorZ;
            if (sensorX > 1){
                Log.d("deb","X1超え");
            }
            if (sensorY > 1){
                Log.d("deb","Y1超え");
            }
            if (sensorZ > 1){
                Log.d("deb","Z1超え");
            }
            textView.setText(strTmp);

            showInfo(event);
        }
    }

    // （お好みで）加速度センサーの各種情報を表示
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showInfo(SensorEvent event){
        Log.d("debug","==showInfo==");
        // センサー名
        StringBuffer info = new StringBuffer("Name: ");
        info.append(event.sensor.getName());
        info.append("\n");

        // ベンダー名
        info.append("Vendor: ");
        info.append(event.sensor.getVendor());
        info.append("\n");

        // 型番
        info.append("Type: ");
        info.append(event.sensor.getType());
        info.append("\n");

        // 最小遅れ
        int data = event.sensor.getMinDelay();
        info.append("Mindelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // 最大遅れ
        data = event.sensor.getMaxDelay();
        info.append("Maxdelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // レポートモード
        data = event.sensor.getReportingMode();
        String stinfo = "unknown";
        if(data == 0){
            stinfo = "REPORTING_MODE_CONTINUOUS";
        }else if(data == 1){
            stinfo = "REPORTING_MODE_ON_CHANGE";
        }else if(data == 2){
            stinfo = "REPORTING_MODE_ONE_SHOT";
        }
        info.append("ReportingMode: ");
        info.append(stinfo);
        info.append("\n");

        // 最大レンジ
        info.append("MaxRange: ");
        float fData = event.sensor.getMaximumRange();
        info.append(String.valueOf(fData));
        info.append("\n");

        // 分解能
        info.append("Resolution: ");
        fData = event.sensor.getResolution();
        info.append(String.valueOf(fData));
        info.append(" m/s^2\n");

        // 消費電流
        info.append("Power: ");
        fData = event.sensor.getPower();
        info.append(String.valueOf(fData));
        info.append(" mA\n");

        textInfo.setText(info);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("debug","==onAccuracyChanged==");
    }
}