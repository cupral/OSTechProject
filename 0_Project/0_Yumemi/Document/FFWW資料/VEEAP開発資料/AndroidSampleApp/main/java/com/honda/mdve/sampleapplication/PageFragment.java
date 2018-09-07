package com.honda.mdve.sampleapplication;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Iterator;

import static com.honda.mdve.sampleapplication.VehcleDataDef.mVehcledataAPIList;
import com.honda.mdve.service.*;

public class PageFragment extends Fragment{
    public VehicleData                      mVehicleData;
    public FragmentTransaction              ft;
    private final String                    TAG             = "PageFragment";
    private static final String             ARG_PARAM       = "page";

    private MainActivity                    mMainActivity;
    // FragmentとActivity間でやり取りできる　未使用
    private OnFragmentInteractionListener   mListener;
    private View                            mView           = null;
    private static final boolean            DEBUG           = true;
    private static final boolean            DEBUG_TOAST     = true;
    // コンストラクタ(必須)
    public PageFragment() {}

    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mView = inflater.inflate(R.layout.pagefragment, container, false);
        mVehicleData = new VehicleData();
        // 子Fragmentに子Fragmentを付ける。自身のFragment Managerを取得
        // getFragmentManager()←こちらは使わないこと．
        ft = this.getChildFragmentManager().beginTransaction();
        ft.replace(R.id.vehicledata_container, mVehicleData, "vehicledata").commit();
        // Manual
        Button btn1 = (Button) mView.findViewById(R.id.button01);
        btn1.setOnClickListener(btnListener);
        // TouchUp EventHandler関数の登録
        for( int btnID: WidgetData.onTouchUpList) {
            Button btn = (Button) mView.findViewById(btnID);
            btn.setOnTouchListener(btnTouchListener);
        }
        // Spinnerに表示するリストの登録
        for(Iterator<Integer> iterator = WidgetData.spinnerMap.keySet().iterator(); iterator.hasNext(); ){
            Integer key = iterator.next();
            Spinner spinner = (Spinner) mView.findViewById(key);
            // MainActivityのインスタンス(Context)を渡す
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mMainActivity, WidgetData.spinnerMap.get(key), R.layout.spinner_layout);
            // Spinner Dropdown Listのデザイン変更
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
//            spinner.setOnItemSelectedListener(spinnerSelectedListener);
        }
        // AUTOボタン
        ToggleButton togglebtn1 = (ToggleButton) mView.findViewById(R.id.toggleButton01);
        togglebtn1.setChecked(false);
        togglebtn1.setOnCheckedChangeListener(tglbtnListener);
        // テキストエディタの入力を制御(XMLの埋め込みでは効かない(バグ?))
        EditText edittext = (EditText) mView.findViewById(R.id.ReturnValue);
        edittext.setInputType(0);
        return mView;
    }

    @Override
    public void onStart () {
        super.onStart();
        // 初期選択状態の設定 ※1
        // Spinnerに表示するリストの登録
        for(Iterator<Integer> iterator = WidgetData.spinnerMap.keySet().iterator(); iterator.hasNext(); ){
            Integer key = iterator.next();
            Spinner spinner = (Spinner) mView.findViewById(key);
            spinner.setFocusable(true);
            spinner.setFocusableInTouchMode(true);
        }
    }


    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //onAttach(Activity activity)は非推奨

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            // ActivityとFragmentの紐付け
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mMainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mMainActivity.isConnectservice() == true) {
            try {
                if (DEBUG) Log.d(TAG, "call unregisterCallback");
                if( mIMdveCallback != null) {
                    mMainActivity.mdveServiceIf.unregisterCallback(mIMdveCallback);
                }
            } catch (Exception e) {
                Log.e(TAG, "RemoteExeption, called mdveServiceIf,unregisterCallback()." +
                        "param1=" + mIMdveCallback);
                if (DEBUG_TOAST)
                    Toast.makeText(getActivity(), "failed calling IMdveService.unregisterCallback", Toast.LENGTH_LONG).show();
            }
            mMainActivity.unregistservice();
        }
        ft.remove(mVehicleData);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private CompoundButton.OnCheckedChangeListener tglbtnListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.toggleButton01) {
                if( DEBUG ) Log.d(TAG, "onClick tglbutton01");
                mMainActivity.startBeepTone();

                if (isChecked) {
                    if(mMainActivity.isConnectservice() == true) {
                        try {
                            if( DEBUG ) Log.d(TAG, "call registerCallback");
                            int ret = mMainActivity.mdveServiceIf.registerCallback( mIMdveCallback, MdveUtil.ACCESSKEY, MdveUtil.VERSION);
                            if(ret != 0){
                                Log.e(TAG, ".mdveServiceIf.registerCallback failed. return="+ret+" callback="+ mIMdveCallback+", ACCESSKEY=" + MdveUtil.ACCESSKEY);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "RemoteExeption, called .mdveServiceIf,registerCallback()." +
                                    "param1=" + mIMdveCallback +
                                    "param2=" + MdveUtil.ACCESSKEY);
                            if(DEBUG_TOAST) Toast.makeText(mMainActivity, "failed calling IMdveService.registerCallback", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if(mMainActivity.isConnectservice() == true) {
                        try {
                            if( DEBUG ) Log.d(TAG, "call unregisterCallback");
                            mMainActivity.mdveServiceIf.unregisterCallback(mIMdveCallback);
                        } catch (Exception e) {
                            Log.e(TAG, "RemoteExeption, called .mdveServiceIf,unregisterCallback()." +
                                    "param1=" + mIMdveCallback);
                            if(DEBUG_TOAST) Toast.makeText(mMainActivity, "failed calling IMdveService.unregisterCallback", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    };

    private View.OnTouchListener btnTouchListener = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event){
            boolean outResult = true;
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                // タッチ時処理
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                if (mMainActivity.isConnectservice() == true) {
                    mMainActivity.startBeepTone();
                    // タッチリリース時処理
                    switch (v.getId()) {
                        case R.id.AirconClimateStatus_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconClimateStatus Send Button");
                            reqAirconClimateStatus_local();
                            break;
                        case R.id.AirconACSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconACSetting Send Button");
                            reqAirconACSetting_local();
                            break;
                        case R.id.AirconModeSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconModeSetting Send Button");
                            reqAirconModeSetting_local();
                            break;
                        case R.id.AirconFanUpDown_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconFanUpDown Send Button");
                            reqAirconFanUpDown_local();
                            break;
                        case R.id.AirconFanSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconFanSetting Send Button");
                            reqAirconFanSetting_local();
                            break;
                        case R.id.AirconTempUpDown_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconTempUpDown Send Button");
                            reqAirconTempUpDown_local();
                            break;
                        case R.id.AirconTempSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconTempSetting Send Button");
                            reqAirconTempSetting_local();
                            break;
                        case R.id.AirconFrDefSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconFrDefSetting Send Button");
                            reqAirconFrDefSetting_local();
                            break;
                        case R.id.AirconRrDefSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconRrDefSetting Send Button");
                            reqAirconRrDefSetting_local();
                            break;
                        case R.id.AirconSyncSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconSyncSetting Send Button");
                            reqAirconSyncSetting_local();
                            break;
                        case R.id.AirconRecFrsSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconRecFrsSetting Send Button");
                            reqAirconRecFrsSetting_local();
                            break;
                        case R.id.AirconVentTempSetting_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AirconVentTempSetting Send Button");
                            reqAirconVentTempSetting_local();
                            break;
                        case R.id.AudioSourceChangeDirect_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSourceChangeDirect Send Button");
                            reqAudioSourceChangeDirect_local();
                            break;
                        case R.id.AudioSourceChange_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSourceChange Send Button");
                            reqAudioSourceChange_local();
                            break;
                        case R.id.AudioSkipUpDown_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSkipUpDown Send Button");
                            reqAudioSkipUpDown_local();
                            break;
                        case R.id.AudioPlay_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioPlay Send Button");
                            reqAudioPlay_local();
                            break;
                        case R.id.AudioFfRew_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioFfRew Send Button");
                            reqAudioFfRew_local();
                            break;
                        case R.id.AudioSeek_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSeek Send Button");
                            reqAudioSeek_local();
                            break;
                        case R.id.AudioShuffle_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioShuffle Send Button");
                            reqAudioShuffle_local();
                            break;
                        case R.id.AudioRepeat_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioRepeat Send Button");
                            reqAudioRepeat_local();
                            break;
                        case R.id.AudioSetChannelSXM_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSetChannelSXM Send Button");
                            reqAudioSetChannelSXM_local();
                            break;
                        case R.id.AudioSetAudioVol_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSetAudioVol Send Button");
                            reqAudioSetAudioVol_local();
                            break;
                        case R.id.AudioSetAudioVolDirect_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSetAudioVolDirect Send Button");
                            reqAudioSetAudioVolDirect_local();
                            break;
                        case R.id.AudioScan_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioScan Send Button");
                            reqAudioScan_local();
                            break;
                        case R.id.AudioTune_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioTune Send Button");
                            reqAudioTune_local();
                            break;
                        case R.id.SettingDisplay_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingDisplay Send Button");
                            reqSettingDisplay_local();
                            break;
                        case R.id.SettingVolume_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingVolume Send Button");
                            reqSettingVolume_local();
                            break;
                        case R.id.SettingVolumeDirect_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingVolumeDirect Send Button");
                            reqSettingVolumeDirect_local();
                            break;
                        case R.id.SettingControlVol_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingControlVol Send Button");
                            reqSettingControlVol_local();
                            break;
                        case R.id.SettingTouchPanelSensitivity_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingTouchPanelSensitivity Send Button");
                            reqSettingTouchPanelSensitivity_local();
                            break;
                        case R.id.SettingTone_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingTone Send Button");
                            reqSettingTone_local();
                            break;
                        case R.id.SettingToneDirect_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingToneDirect Send Button");
                            reqSettingToneDirect_local();
                            break;
                        case R.id.SettingFaderBalance_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingFaderBalance Send Button");
                            reqSettingFaderBalance_local();
                            break;
                        case R.id.SettingFaderBalanceDirect_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingFaderBalanceDirect Send Button");
                            reqSettingFaderBalanceDirect_local();
                            break;
                        case R.id.SettingSubwoofer_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingSubwoofer Send Button");
                            reqSettingSubwoofer_local();
                            break;
                        case R.id.SettingSubwooferDirect_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingSubwooferDirect Send Button");
                            reqSettingSubwooferDirect_local();
                            break;
                        case R.id.SettingSVC_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease SettingSVC Send Button");
                            reqSettingSVC_local();
                            break;
                        case R.id.NaviDestinationLatLon_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease NaviDestinationLatLon Send Button");
                            reqNaviDestinationLatLon_local();
                            break;
                        case R.id.NotificationPopup_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease NotificationPopup Send Button");
                            reqNotificationPopup_local();
                            break;
                        case R.id.Performance_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease NotificationPopup Send Button");
                            reqPerformanceTest();
                            break;
                        default:
                            if (DEBUG) Log.d(TAG, "onTouchRelease id=" + v.getId());
                            outResult = false;
                            break;
                    }
                }
            }
            return outResult;
        }
    };


    private View.OnClickListener btnListener = new View.OnClickListener(){
        public void onClick(View v){
            switch(v.getId()) {
                case R.id.button01:
                    if( DEBUG ) Log.d(TAG, "onClick Manual button");
                    mMainActivity.startBeepTone();

                    if(mMainActivity.isConnectservice() == true) {
                        getVehicleData_local( "DUMMY", MdveUtil.ACCESSKEY, MdveUtil.VERSION); // エラーパターン1, グループ名誤り
                        getVehicleData_local( mVehcledataAPIList[0], "DUMMYKEY", MdveUtil.VERSION); // エラーパターン2, アクセスキー誤り
                        getVehicleData_local( mVehcledataAPIList[0], MdveUtil.ACCESSKEY, "ff.ff"); // エラーパターン3, バージョン誤り
                        for(int i=0;i<mVehcledataAPIList.length;i++){
                            getVehicleData_local( mVehcledataAPIList[i], MdveUtil.ACCESSKEY, MdveUtil.VERSION);
                        }
                    }
                    mVehicleData.updateDisp();
                    break;
                default:
                    if( DEBUG ) Log.d(TAG, "onClick id=" + v.getId());
                    break;
            }
        }
    };

    private void getVehicleData_local( String groupname , String accesskey, String version ){
        String data = null;
        try {
            // IMdveService.aidl#getVehicleData()をコールする
            data = mMainActivity.mdveServiceIf.getVehicleData( groupname, accesskey, version);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mdveServiceIf,getVehicleData()." +
                    " param1=" + groupname +
                    " param2=" + accesskey);
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.getVehicleData.", Toast.LENGTH_LONG).show();
            data = "";
        }
        if( DEBUG ) Log.d(TAG, "call getVehicleData item:"+ groupname + " return:"+data.toString());

        mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_REQUEST, data.toString());

        mVehicleData.setJSONStringData(data);
    }


    // Climate ON/OFF
    private void reqAirconClimateStatus_local(){
        String  data            = null;
        int     seat            = 0;
        int     climatestate    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconClimateStatus_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconClimateStatus_climatestate);
            seat                = WidgetData.AirconClimateStatus_seat[spinner1.getSelectedItemPosition()];
            climatestate        = WidgetData.AirconClimateStatus_climatestate[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconClimateStatus()をコールする
            Log.d(TAG, "Called reqAirconClimateStatus()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + climatestate );
            data = mMainActivity.mdveServiceIf.reqAirconClimateStatus( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, climatestate );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mdveServiceIf,reqAirconClimateStatus()." + "Exeption:" + e.toString());
            if (DEBUG_TOAST)
                Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconClimateStatus.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconClimateStatus");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconClimateStatus seat:"+ seat +  " climatestate:" + climatestate + " return:"+data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconClimateStatus", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconClimateStatus Failed seat:"+ seat +  " climatestate:" + climatestate);
        }
    }

    // A/C操作
    private void reqAirconACSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     acstatus    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconACSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconACSetting_acstatus);
            seat                = WidgetData.AirconACSetting_seat[spinner1.getSelectedItemPosition()];
            acstatus            = WidgetData.AirconACSetting_acstatus[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconACSetting()をコールする
            Log.d(TAG, "Called reqAirconACSettings()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + acstatus );
            data = mMainActivity.mdveServiceIf.reqAirconACSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, acstatus );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mdveServiceIf,reqAirconACSettings()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconACSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconACSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconACSetting seat:"+ seat +  " acstatus:" + acstatus + " return:"+data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconACSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconACSetting Failed seat:"+ seat +  " acstatus:" + acstatus);
        }
    }

    // MODE吹出し口操作
    private void reqAirconModeSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     modestatus  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconModeSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconModeSetting_modestatus);
            seat                = WidgetData.AirconModeSetting_seat[spinner1.getSelectedItemPosition()];
            modestatus          = WidgetData.AirconModeSetting_modestatus[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconModeSetting()をコールする
            Log.d(TAG, "Called reqAirconModeSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + modestatus );
            data = mMainActivity.mdveServiceIf.reqAirconModeSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, modestatus );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mdveServiceIf,reqAirconModeSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconModeSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconModeSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconModeSetting seat:"+ seat +  " modestatus:" + modestatus + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconModeSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconModeSetting Failed seat:"+ seat +  " modestatus:" + modestatus);
        }
    }

    // 風量UP/DOWN操作
    private void reqAirconFanUpDown_local(){
        String  data        = null;
        int     seat        = 0;
        int     fanupdown   = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconFanUpDown_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconFanUpDown_fanupdown);
            seat                = WidgetData.AirconFanUpDown_seat[spinner1.getSelectedItemPosition()];
            fanupdown           = WidgetData.AirconFanUpDown_fanupdown[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconFanUpDown()をコールする
            Log.d(TAG, "Called reqAirconFanUpDown()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + fanupdown );
            data = mMainActivity.mdveServiceIf.reqAirconFanUpDown( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, fanupdown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconFanUpDown()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconFanUpDown.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconFanUpDown");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconFanUpDown seat:"+ seat +  " fanupdown:" + fanupdown + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconFanUpDown", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconFanUpDown Failed seat:"+ seat +  " fanupdown:" + fanupdown);
        }
    }

    // 風量値指定操作
    private void reqAirconFanSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     fanvalue    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconFanSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconFanSetting_fanvalue);
            seat                = WidgetData.AirconFanSetting_seat[spinner1.getSelectedItemPosition()];
            fanvalue            = WidgetData.AirconFanSetting_fanvalue[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconFanSetting()をコールする
            Log.d(TAG, "Called reqAirconFanSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + fanvalue );
            data = mMainActivity.mdveServiceIf.reqAirconFanSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, fanvalue );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconFanSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconFanSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconFanSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconFanSetting seat:"+ seat +  " fanvalue:" + fanvalue + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconFanSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconFanSetting Failed seat:"+ seat +  " fanvalue:" + fanvalue);
        }

    }

    // 設定温度UP/DOWN操作
    private void reqAirconTempUpDown_local(){
        String  data        = null;
        int     seat        = 0;
        int     tempupdown  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconTempUpDown_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconTempUpDown_tempupdown);
            seat                = WidgetData.AirconTempUpDown_seat[spinner1.getSelectedItemPosition()];
            tempupdown          = WidgetData.AirconTempUpDown_tempupdown[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconTempUpDown()をコールする
            Log.d(TAG, "Called reqAirconTempUpDown()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + tempupdown );
            data = mMainActivity.mdveServiceIf.reqAirconTempUpDown( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, tempupdown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconTempUpDown()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconTempUpDown.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconTempUpDown");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconTempUpDown seat:"+ seat +  " tempupdown :" + tempupdown  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconTempUpDown", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconTempUpDown Failed seat:"+ seat +  " tempupdown:" + tempupdown);
        }
    }

    // 設定温度値指定操作
    private void reqAirconTempSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     tempvalue   = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconTempSetting_seat);
            EditText text1      = (EditText)mView.findViewById(R.id.AirconTempSetting_tempvalue_edit);
            seat                = WidgetData.AirconTempSetting_seat[spinner1.getSelectedItemPosition()];
            if( text1.getText().toString().equals("") ){
                tempvalue = 0;
            }
            else {
                tempvalue = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqAirconTempSetting()をコールする
            Log.d(TAG, "Called reqAirconTempSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + tempvalue );
            data = mMainActivity.mdveServiceIf.reqAirconTempSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, tempvalue );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconTempSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconTempSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconTempSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconTempSetting seat:"+ seat +  " tempvalue :" + tempvalue  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconTempSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconTempSetting Failed seat:"+ seat +  " tempvalue:" + tempvalue);
        }
    }

    // フロントDEF操作
    private void reqAirconFrDefSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     defstatus   = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconFrDefSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconFrDefSetting_defstatus);
            seat                = WidgetData.AirconFrDefSetting_seat[spinner1.getSelectedItemPosition()];
            defstatus           = WidgetData.AirconFrDefSetting_defstatus[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconFrDefSetting()をコールする
            Log.d(TAG, "Called reqAirconFrDefSetting" +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + defstatus );
            data = mMainActivity.mdveServiceIf.reqAirconFrDefSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, defstatus );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconFrDefSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconFrDefSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconFrDefSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconFrDefSetting seat:"+ seat +  " defstatus :" + defstatus  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconFrDefSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconFrDefSetting Failed seat:"+ seat +  " defstatus:" + defstatus);
        }
    }

    //  リアDEF操作
    private void reqAirconRrDefSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     defstatus   = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconRrDefSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconRrDefSetting_defstatus);
            seat                = WidgetData.AirconRrDefSetting_seat[spinner1.getSelectedItemPosition()];
            defstatus           = WidgetData.AirconRrDefSetting_defstatus[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconRrDefSetting()をコールする
            Log.d(TAG, "Called reqAirconRrDefSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + defstatus );
            data = mMainActivity.mdveServiceIf.reqAirconRrDefSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, defstatus );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconRrDefSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconRrDefSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconRrDefSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconRrDefSetting seat:"+ seat +  " defstatus :" + defstatus  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconRrDefSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconRrDefSetting Failed seat:"+ seat +  " defstatus:" + defstatus);
        }
    }

    // SYNC操作
    private void reqAirconSyncSetting_local(){
        String  data        = null;
        int     seat        = 0;
        int     syncstatus  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconSyncSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconSyncSetting_syncstatus);
            seat                = WidgetData.AirconSyncSetting_seat[spinner1.getSelectedItemPosition()];
            syncstatus           = WidgetData.AirconSyncSetting_syncstatus[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconSyncSetting()をコールする
            Log.d(TAG, "Called reqAirconSyncSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + syncstatus );
            data = mMainActivity.mdveServiceIf.reqAirconSyncSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, syncstatus );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconSyncSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconSyncSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにJSONコードを出力
        displayReturnValue(data, "AirconSyncSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconSyncSetting seat:"+ seat +  " syncstatus :" + syncstatus  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconSyncSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconSyncSetting Failed seat:"+ seat +  " syncstatus:" + syncstatus);
        }
    }

    // 内外気操作
    private void reqAirconRecFrsSetting_local(){
        String  data         = null;
        int     seat         = 0;
        int     recfrsstatus = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconRecFrsSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconRecFrsSetting_recfrsstatus);
            seat                = WidgetData.AirconRecFrsSetting_seat[spinner1.getSelectedItemPosition()];
            recfrsstatus        = WidgetData.AirconRecFrsSetting_recfrsstatus[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconRecFrsSetting()をコールする
            Log.d(TAG, "Called reqAirconRecFrsSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + recfrsstatus );
            data = mMainActivity.mdveServiceIf.reqAirconRecFrsSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, recfrsstatus );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconRecFrsSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconRecFrsSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconRecFrsSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconRecFrsSetting seat:"+ seat +  " recfrsstatus :" + recfrsstatus  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconRecFrsSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconRecFrsSetting Failed seat:"+ seat +  " recfrsstatus:" + recfrsstatus);
        }
    }

    // VENT温度可変操作
    private void reqAirconVentTempSetting_local(){
        String  data     = null;
        int     seat     = 0;
        int     venttemp = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AirconVentTempSetting_seat);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AirconVentTempSetting_venttemp);
            seat                = WidgetData.AirconVentTempSetting_seat[spinner1.getSelectedItemPosition()];
            venttemp            = WidgetData.AirconVentTempSetting_venttemp[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAirconVentTempSetting()をコールする
            Log.d(TAG, "Called reqAirconVentTempSetting()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seat +
                    " param4=" + venttemp );
            data = mMainActivity.mdveServiceIf.reqAirconVentTempSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seat, venttemp );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAirconVentTempSetting()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAirconVentTempSetting.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AirconVentTempSetting");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconVentTempSetting seat:"+ seat +  " venttemp:" + venttemp  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAirconVentTempSetting", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAirconVentTempSetting Failed seat:"+ seat +  " venttemp:" + venttemp);
        }
    }

    //オーディオソース変更(ソース直接指定)
    private void reqAudioSourceChangeDirect_local(){
        String  data    = null;
        int     source  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioSourceChangeDirect_source);
            source              = WidgetData.AudioSourceChangeDirect_source[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioSourceChangeDirect()をコールする
            Log.d(TAG, "Called reqAudioSourceChangeDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source);
            data = mMainActivity.mdveServiceIf.reqAudioSourceChangeDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, source);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSourceChangeDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSourceChangeDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSourceChangeDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSourceChangeDirect source:"+ source + " return:"+data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSourceChangeDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSourceChangeDirect Failed source:"+ source);
        }
    }

    //オーディオソース変更(ソース送り)
    private void reqAudioSourceChange_local(){
        String data = null;
        int next    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioSourceChange_next);
            next              = WidgetData.AudioSourceChange_next[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioSourceChange()をコールする
            Log.d(TAG, "Called reqAudioSourceChange()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + next);
            data = mMainActivity.mdveServiceIf.reqAudioSourceChange( MdveUtil.ACCESSKEY, MdveUtil.VERSION, next );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSourceChange()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSourceChange.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSourceChange");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSourceChange next:"+ next + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSourceChange", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSourceChange Failed next:"+ next);
        }
    }

    // 現在オーディオソース：SkipUp/Down
    private void reqAudioSkipUpDown_local(){
        String data = null;
        int skip    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioSkipUpDown_skip);
            skip                = WidgetData.AudioSkipUpDown_skip[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioSkipUpDown()をコールする
            Log.d(TAG, "reqAudioSkipUpDown()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + skip);
            data = mMainActivity.mdveServiceIf.reqAudioSkipUpDown( MdveUtil.ACCESSKEY, MdveUtil.VERSION, skip);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSkipUpDown()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSkipUpDown.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSkipUpDown");
        if (data != null) {
            // ログ出力
            if (DEBUG)  Log.d(TAG, "call reqAudioSkipUpDown skip:" + skip + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSkipUpDown", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSkioUpDown Failed skip:"+ skip);
        }
    }

    // 現在オーディオソース：再生／停止
    private void reqAudioPlay_local(){
        String data = null;
        int play    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioPlay_play);
            play                = WidgetData.AudioPlay_play[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioPlay()をコールする
            Log.d(TAG, "reqAudioPlay()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + play);
            data = mMainActivity.mdveServiceIf.reqAudioPlay( MdveUtil.ACCESSKEY, MdveUtil.VERSION, play);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioPlay()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioPlay.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioPlay");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioPlay play:"+ play + " return:"+data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioPlay", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioPlay Failed play:" + play);
        }
    }

    // 現在オーディオソース：早送り／巻き戻し
    private void reqAudioFfRew_local(){
        String data = null;
        int ffrew   = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioFfRew_ffrew);
            ffrew               = WidgetData.AudioFfRew_ffrew[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioFfRew()をコールする
            Log.d(TAG, "reqAudioFfRew()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + ffrew);
            data = mMainActivity.mdveServiceIf.reqAudioFfRew( MdveUtil.ACCESSKEY, MdveUtil.VERSION, ffrew);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioFfRew()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioFfRew.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioFfRew");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioFfRew ffrew"+ ffrew + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioFfRew", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioFfRew Failed ffrew:" + ffrew);
        }
    }

    // 現在オーディオソース：seek up/down[AM/FM]
    private void reqAudioSeek_local(){
        String data = null;
        int seek    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioSeek_seek);
            seek               = WidgetData.AudioSeek_seek[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioSeek()をコールする
            Log.d(TAG, "reqAudioSeek()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + seek);
            data = mMainActivity.mdveServiceIf.reqAudioSeek( MdveUtil.ACCESSKEY, MdveUtil.VERSION, seek);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSeek()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSeek.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSeek");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSeek seek"+ seek + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioSeek", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSeek Failed seek:" + seek);
        }
    }

    // 現在オーディオソース：シャッフル指定
    private void reqAudioShuffle_local(){
        String data     = null;
        int shufflemode = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioShuffle_shufflemode);
            shufflemode         = WidgetData.AudioShuffle_shufflemode[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioShuffle()をコールする
            Log.d(TAG, "reqAudioShuffle()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + shufflemode);
            data = mMainActivity.mdveServiceIf.reqAudioShuffle( MdveUtil.ACCESSKEY, MdveUtil.VERSION, shufflemode);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioShuffle()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioShuffle.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioShuffle");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioShuffle shufflemode"+ shufflemode + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioShuffle", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioShuffle Failed shufflemode:" + shufflemode);
        }
    }

    // 現在オーディオソース：リピート指定
    private void reqAudioRepeat_local(){
        String data     = null;
        int repeatmode  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioRepeat_repeatmode);
            repeatmode         = WidgetData.AudioRepeat_repeatmode[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioRepeat()をコールする
            Log.d(TAG, "reqAudioRepeat()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + repeatmode);
            data = mMainActivity.mdveServiceIf.reqAudioRepeat( MdveUtil.ACCESSKEY, MdveUtil.VERSION, repeatmode);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioRepeat()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioRepeat.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioRepeat");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioRepeat repeatmode"+ repeatmode + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioRepeat", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioRepeat Failed repeatmode:" + repeatmode);
        }
    }

    // SiriusXM　チャンネル指定
    private void reqAudioSetChannelSXM_local(){
        String data     = null;
        int channelno   = 0;
        clearReturnValue();
        try {
            EditText text1      = (EditText)mView.findViewById(R.id.AudioSetChannelSXM_channelno_edit);
            if( text1.getText().toString().equals("") ){
                channelno = 0;
            }
            else {
                channelno = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqAudioSetChannelSXM()をコールする
            Log.d(TAG, "reqAudioSetChannelSXM()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + channelno);
            data = mMainActivity.mdveServiceIf.reqAudioSetChannelSXM( MdveUtil.ACCESSKEY, MdveUtil.VERSION, channelno);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSetChannelSXM()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSetChannelSXM.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSetChannelSXM");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetChannelSXM channelno"+ channelno + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioSetChannelSXM", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetChannelSXM Failed channelno:" + channelno);
        }
    }

    // 現在オーディオソース：音量設定(Up/Down)
    private void reqAudioSetAudioVol_local(){
        String data = null;
        int updown  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioSetAudioVol_updown);
            updown         = WidgetData.AudioSetAudioVol_updown[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioSetAudioVol()をコールする
            Log.d(TAG, "reqAudioSetAudioVol()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + updown);
            data = mMainActivity.mdveServiceIf.reqAudioSetAudioVol( MdveUtil.ACCESSKEY, MdveUtil.VERSION, updown);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSetAudioVol()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSetAudioVol.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSetAudioVol");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetAudioVol updown"+ updown + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioSetAudioVol", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetAudioVol Failed updown:" + updown);
        }
    }

    // 現在オーディオソース：音量設定(ダイレクト)
    private void reqAudioSetAudioVolDirect_local(){
        String data = null;
        int vol     = 0;
        clearReturnValue();
        try {
            EditText text1      = (EditText)mView.findViewById(R.id.AudioSetAudioVolDirect_vol_edit);
            if( text1.getText().toString().equals("") ){
                vol = 0;
            }
            else {
                vol = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqAudioSetAudioVolDirect()をコールする
            Log.d(TAG, "reqAudioSetAudioVolDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + vol);
            data = mMainActivity.mdveServiceIf.reqAudioSetAudioVolDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, vol);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSetAudioVolDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSetAudioVolDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSetAudioVolDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetAudioVolDirect vol"+ vol + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqAudioSetAudioVolDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetAudioVolDirect Failed vol:" + vol);
        }
    }

    // Scan(FM/AM)
    private void reqAudioScan_local(){
        String  data    = null;
        int     source  = 0;
        int     scan    = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioScan_source);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AudioScan_scan);
            source              = WidgetData.AudioScan_source[spinner1.getSelectedItemPosition()];
            scan                = WidgetData.AudioScan_scan[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioScan()をコールする
            Log.d(TAG, "Called reqAudioScan()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " param4=" + scan );
            data = mMainActivity.mdveServiceIf.reqAudioScan( MdveUtil.ACCESSKEY, MdveUtil.VERSION, source, scan );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioScan()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioScan.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioScan");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioScan source:"+ source +  " scan :" + scan  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioScan", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioScan Failed source:"+ source +  " scan:" + scan);
        }
    }

    // Tune(FM/AM)(周波数選択)
    private void reqAudioTune_local(){
        String  data   = null;
        int     source = 0;
        int     updown = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.AudioTune_source);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.AudioTune_updown);
            source              = WidgetData.AudioTune_source[spinner1.getSelectedItemPosition()];
            updown                = WidgetData.AudioTune_updown[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioTune()をコールする
            Log.d(TAG, "Called reqAudioTune()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " param4=" + updown );
            data = mMainActivity.mdveServiceIf.reqAudioTune( MdveUtil.ACCESSKEY, MdveUtil.VERSION, source, updown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioTune()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioTune.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioTune");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioTune source:"+ source +  " updown :" + updown  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioTune", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioTune Failed source:"+ source +  " updown:" + updown);
        }
    }

    // DisplaySetting(明るさ／色の濃さ／コントラスト／色合い)
    private void reqSettingDisplay_local(){
        String  data        = null;
        int     dispsetkind = 0;
        int     updown      = 0;
        int     videosource = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingDisplay_videosource);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.SettingDisplay_dispsetkind);
            Spinner spinner3    = (Spinner)mView.findViewById(R.id.SettingDisplay_updown);
            videosource         = WidgetData.SettingDisplay_videosource[spinner1.getSelectedItemPosition()];
            dispsetkind         = WidgetData.SettingDisplay_dispsetkind[spinner2.getSelectedItemPosition()];
            updown              = WidgetData.SettingDisplay_updown[spinner3.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingDisplay()をコールする
            Log.d(TAG, "Called reqSettingDisplay()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + videosource +
                    " param4=" + dispsetkind +
                    " param5=" + updown );
            data = mMainActivity.mdveServiceIf.reqSettingDisplay( MdveUtil.ACCESSKEY, MdveUtil.VERSION, videosource, dispsetkind, updown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingDisplay()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingDisplay.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingDisplay");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingDisplay videosource:"+ videosource + "dispsetkind:" + dispsetkind +  " updown :" + updown  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingDisplay", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingDisplay Failed videosource:"+ videosource + "dispsetkind:" + dispsetkind +  " updown:" + updown);
        }
    }

    // 音量設定(Up/Down)
    private void reqSettingVolume_local(){
        String  data    = null;
        int     volkind = 0;
        int     updown  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingVolume_volkind);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.SettingVolume_updown);
            volkind              = WidgetData.SettingVolume_volkind[spinner1.getSelectedItemPosition()];
            updown                = WidgetData.SettingVolume_updown[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingVolume()をコールする
            Log.d(TAG, "Called reqSettingVolume()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + volkind +
                    " param4=" + updown );
            data = mMainActivity.mdveServiceIf.reqSettingVolume( MdveUtil.ACCESSKEY, MdveUtil.VERSION, volkind, updown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingVolume()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingVolume.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingVolume");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingVolume volkind:"+ volkind +  " updown :" + updown  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingVolume", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingVolume Failed volkind:"+ volkind +  " updown:" + updown);
        }
    }

    // 音量設定(ダイレクト指定)
    private void reqSettingVolumeDirect_local(){
        String  data    = null;
        int     volkind = 0;
        int     volume  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingVolumeDirect_volkind);
            volkind             = WidgetData.SettingVolumeDirect_volkind[spinner1.getSelectedItemPosition()];
            EditText text1      = (EditText)mView.findViewById(R.id.SettingVolumeDirect_volume_edit);
            if( text1.getText().toString().equals("") ){
                volume = 0;
            }
            else {
                volume = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqSettingVolumeDirect()をコールする
            Log.d(TAG, "Called reqSettingVolumeDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + volkind +
                    " param4=" + volume );
            data = mMainActivity.mdveServiceIf.reqSettingVolumeDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, volkind, volume );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingVolumeDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingVolumeDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingVolumeDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingVolumeDirect volkind:"+ volkind +  " volume :" + volume  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingVolumeDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingVolumeDirect Failed volkind:"+ volkind +  " volume:" + volume);
        }
    }

    // 操作音量設定(OFF/1/2/3等)
    private void reqSettingControlVol_local(){
        String  data    = null;
        int     volume  = 0;
        clearReturnValue();
        try {
            Spinner spinner1   = (Spinner)mView.findViewById(R.id.SettingControlVol_volume);
            volume             = WidgetData.SettingControlVol_volume[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingControlVol()をコールする
            Log.d(TAG, "Called reqSettingControlVol()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + volume );
            data = mMainActivity.mdveServiceIf.reqSettingControlVol( MdveUtil.ACCESSKEY, MdveUtil.VERSION, volume );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingControlVol()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingControlVol.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingControlVol");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingControlVol volume:"+ volume + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingControlVol", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingControlVol Failed volume:" + volume);
        }
    }

    // タッチパネル感度設定(High/Low)
    private void reqSettingTouchPanelSensitivity_local(){
        String  data        = null;
        int     sensitivity = 0;
        clearReturnValue();
        try {
            Spinner spinner1   = (Spinner)mView.findViewById(R.id.SettingTouchPanelSensitivity_sensitivity);
            sensitivity             = WidgetData.SettingTouchPanelSensitivity_sensitivity[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingTouchPanelSensitivity()をコールする
            Log.d(TAG, "Called reqSettingTouchPanelSensitivity()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + sensitivity );
            data = mMainActivity.mdveServiceIf.reqSettingTouchPanelSensitivity( MdveUtil.ACCESSKEY, MdveUtil.VERSION, sensitivity );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingTouchPanelSensitivity()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingTouchPanelSensitivity.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingTouchPanelSensitivity");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingTouchPanelSensitivity sensitivity:"+ sensitivity + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingTouchPanelSensitivity", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingTouchPanelSensitivity Failed sensitivity:" + sensitivity);
        }
    }

    // Tone(BASS/MIDRANGE/TREBLE)設定(Up/Down)
    private void reqSettingTone_local(){
        String  data        = null;
        int     tonekind    = 0;
        int     updown      = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingTone_tonekind);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.SettingTone_updown);
            tonekind            = WidgetData.SettingTone_tonekind[spinner1.getSelectedItemPosition()];
            updown              = WidgetData.SettingTone_updown[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingTone()をコールする
            Log.d(TAG, "Called reqSettingTone()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + tonekind +
                    " param4=" + updown );
            data = mMainActivity.mdveServiceIf.reqSettingTone( MdveUtil.ACCESSKEY, MdveUtil.VERSION, tonekind, updown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingTone()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingTone.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingTone");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingTone tonekind:"+ tonekind +  " updown :" + updown  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingTone", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingTone Failed tonekind:"+ tonekind +  " updown:" + updown);
        }
    }

    // Tone(BASS/MIDRANGE/TREBLE)設定(ダイレクト)
    private void reqSettingToneDirect_local(){
        String  data        = null;
        int     tonekind    = 0;
        int     set         = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingToneDirect_tonekind);
            tonekind            = WidgetData.SettingToneDirect_tonekind[spinner1.getSelectedItemPosition()];
            EditText text1      = (EditText)mView.findViewById(R.id.SettingToneDirect_set_edit);
            if( text1.getText().toString().equals("") ){
                set = 0;
            }
            else {
                set = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqSettingToneDirect()をコールする
            Log.d(TAG, "Called reqSettingToneDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + tonekind +
                    " param4=" + set );
            data = mMainActivity.mdveServiceIf.reqSettingToneDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, tonekind, set );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingToneDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingToneDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingToneDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingToneDirect tonekind:"+ tonekind +  " set :" + set  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingToneDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingToneDirect Failed tonekind:"+ tonekind +  " set:" + set);
        }
    }

    // Fader/Balance設定(左右／上下)(Up/Down)
    private void reqSettingFaderBalance_local(){
        String  data            = null;
        int     faderbalance    = 0;
        int     updownleftright = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingFaderBalance_faderbalance);
            Spinner spinner2    = (Spinner)mView.findViewById(R.id.SettingFaderBalance_updownleftright);
            faderbalance        = WidgetData.SettingFaderBalance_faderbalance[spinner1.getSelectedItemPosition()];
            updownleftright     = WidgetData.SettingFaderBalance_updownleftright[spinner2.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingFaderBalance()をコールする
            Log.d(TAG, "Called reqSettingFaderBalance()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + faderbalance +
                    " param4=" + updownleftright );
            data = mMainActivity.mdveServiceIf.reqSettingFaderBalance( MdveUtil.ACCESSKEY, MdveUtil.VERSION, faderbalance, updownleftright );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingFaderBalance()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingFaderBalance.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingFaderBalance");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingFaderBalance faderbalance:"+ faderbalance +  " updownleftright :" + updownleftright  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingFaderBalance", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingFaderBalance Failed faderbalance:"+ faderbalance +  " updownleftright:" + updownleftright);
        }
    }

    // Fader/Balance設定(左右／上下)(ダイレクト)
    private void reqSettingFaderBalanceDirect_local(){
        String  data         = null;
        int     faderbalance = 0;
        int     set          = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingFaderBalanceDirect_faderbalance);
            faderbalance        = WidgetData.SettingFaderBalanceDirect_faderbalance[spinner1.getSelectedItemPosition()];
            EditText text1      = (EditText)mView.findViewById(R.id.SettingFaderBalanceDirect_set_edit);
            if( text1.getText().toString().equals("") ){
                set = 0;
            }
            else {
                set = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqSettingFaderBalanceDirect()をコールする
            Log.d(TAG, "Called reqSettingFaderBalanceDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + faderbalance +
                    " param4=" + set );
            data = mMainActivity.mdveServiceIf.reqSettingFaderBalanceDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, faderbalance, set );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingFaderBalanceDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingFaderBalanceDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingFaderBalanceDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingFaderBalanceDirect faderbalance:"+ faderbalance +  " set :" + set  + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingFaderBalanceDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingFaderBalanceDirect Failed faderbalance:"+ faderbalance +  " set:" + set);
        }
    }

    // Subwoofer設定(Up/Down)
    private void reqSettingSubwoofer_local(){
        String  data    = null;
        int     updown  = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingSubwoofer_updown);
            updown              = WidgetData.SettingSubwoofer_updown[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingSubwoofer()をコールする
            Log.d(TAG, "Called reqSettingSubwoofer()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + updown );
            data = mMainActivity.mdveServiceIf.reqSettingSubwoofer( MdveUtil.ACCESSKEY, MdveUtil.VERSION, updown );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingSubwoofer()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingSubwoofer.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingSubwoofer");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingSubwoofer updown:"+ updown + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingSubwoofer", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingSubwoofer Failed updown:" + updown);
        }
    }

    // Subwoofer設定(ダイレクト)
    private void reqSettingSubwooferDirect_local(){
        String  data    = null;
        int     set     = 0;
        clearReturnValue();
        try {
            EditText text1      = (EditText)mView.findViewById(R.id.SettingSubwooferDirect_set_edit);
            if( text1.getText().toString().equals("") ){
                set = 0;
            }
            else {
                set = Integer.parseInt(text1.getText().toString());
            }
            // IMdveService.aidl#reqSettingSubwooferDirect()をコールする
            Log.d(TAG, "reqSettingSubwooferDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + set);
            data = mMainActivity.mdveServiceIf.reqSettingSubwooferDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, set);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingSubwooferDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingSubwooferDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingSubwooferDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingSubwooferDirect set"+ set + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqSettingSubwooferDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingSubwooferDirect Failed set:" + set);
        }
    }

    // 車速連動音量調整機能(SVC)モード設定
    private void reqSettingSVC_local(){
        String  data    = null;
        int     set     = 0;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.SettingSVC_set);
            set                 = WidgetData.SettingSVC_set[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqSettingSVC()をコールする
            Log.d(TAG, "Called reqSettingSVC()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + set );
            data = mMainActivity.mdveServiceIf.reqSettingSVC( MdveUtil.ACCESSKEY, MdveUtil.VERSION, set );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqSettingSVC()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqSettingSVC.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "SettingSVC");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingSVC set:"+ set + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqSettingSVC", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqSettingSVC Failed set:" + set);
        }
    }

    // 目的地探索(緯度／経度指定)
    private void reqNaviDestinationLatLon_local(){
        String data         = null;
        String latitude     = null;
        String longitude    = null;
        String destname     = null;
        clearReturnValue();
        try {
            EditText text1      = (EditText)mView.findViewById(R.id.NaviDestinationLatLon_latitude_edit);
            if( text1.getText().toString().equals("") ){
                latitude = null;
            }
            else {
                latitude = text1.getText().toString();
            }
            EditText text2      = (EditText)mView.findViewById(R.id.NaviDestinationLatLon_longitude_edit);
            if( text2.getText().toString().equals("") ){
                longitude = null;
            }
            else {
                longitude = text2.getText().toString();
            }
            EditText text3      = (EditText)mView.findViewById(R.id.NaviDestinationLatLon_destname_edit);
            if( text3.getText().toString().equals("") ){
                destname = null;
            }
            else {
                destname = text3.getText().toString();
            }
            // IMdveService.aidl#reqNaviDestinationLatLon()をコールする
            Log.d(TAG, "reqNaviDestinationLatLon()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + latitude +
                    " param4=" + longitude +
                    " param5=" + destname );
            data = mMainActivity.mdveServiceIf.reqNaviDestinationLatLon( MdveUtil.ACCESSKEY, MdveUtil.VERSION, latitude, longitude, destname);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqNaviDestinationLatLon()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqNaviDestinationLatLon.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "NaviDestinationLatLon");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqNaviDestinationLatLon latitude"+ latitude + "longitude:" + longitude + "destname:" + destname + " return:"+ data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ,"reqNaviDestinationLatLon", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqNaviDestinationLatLon Failed latitude:" + latitude + "longitude:" + longitude + "destname:" + destname );
        }
    }

    // Notification(ポップアップ表示)
    private void reqNotificationPopup_local(){
        String  data    = null;
        int     kind    = 0;
        String  wording = null;
        clearReturnValue();
        try {
            Spinner spinner1    = (Spinner)mView.findViewById(R.id.NotificationPopup_kind);
            kind                 = WidgetData.NotificationPopup_kind[spinner1.getSelectedItemPosition()];
            EditText text1      = (EditText)mView.findViewById(R.id.NotificationPopup_wording_edit);
            if( text1.getText().toString().equals("") ){
                wording = null;
            }
            else {
                wording = text1.getText().toString();
            }
            // IMdveService.aidl#reqNotificationPopup()をコールする
            Log.d(TAG, "Called reqNotificationPopup()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + kind +
                    " param4=" + wording);
            data = mMainActivity.mdveServiceIf.reqNotificationPopup( MdveUtil.ACCESSKEY, MdveUtil.VERSION, kind, wording );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqNotificationPopup()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqNotificationPopup.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "NotificationPopup");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqNotificationPopup kind:"+ kind + "wording:" + wording + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqNotificationPopup", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqNotificationPopup Failed kind:" + kind + "wording:" + wording);
        }
    }


    // Performanceテスト
    private void reqPerformanceTest(){
        int     kind    = 30;
        String  data    = null;
        clearReturnValue();
        try {
            Log.d(TAG, "reqPerformanceTest()-S.");

            //エアコン機能関連
            mMainActivity.mdveServiceIf.reqAirconClimateStatus( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 1 );        //Climate ON/OFF
            mMainActivity.mdveServiceIf.reqAirconACSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 1 );            //A/C操作
            mMainActivity.mdveServiceIf.reqAirconModeSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 2 );          //MODE吹出し口操作
            mMainActivity.mdveServiceIf.reqAirconFanSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 3 );           //風量値指定操作
            mMainActivity.mdveServiceIf.reqAirconTempSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 20 );         //設定温度値指定操作
            mMainActivity.mdveServiceIf.reqAirconFrDefSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 1 );         //フロントDEF操作
            mMainActivity.mdveServiceIf.reqAirconRrDefSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 1 );         //リアDEF操作
            mMainActivity.mdveServiceIf.reqAirconSyncSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 1 );          //SYNC操作
            mMainActivity.mdveServiceIf.reqAirconRecFrsSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 10 );       //内外気操作
            mMainActivity.mdveServiceIf.reqAirconVentTempSetting( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 1, 4 );      //VENT温度可変操作
            //AUDIO機能関連
            mMainActivity.mdveServiceIf.reqAudioSeek( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0);                      //現在オーディオソース：seek up/down[AM/FM]
            mMainActivity.mdveServiceIf.reqAudioSelectPresetList( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 1 , 1, 5);   //プリセット選択&再生[AM/FM]
            mMainActivity.mdveServiceIf.reqAudioSetAudioVolDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 10);        //現在オーディオソース：音量設定(ダイレクト)
            //Setting機能関連
            mMainActivity.mdveServiceIf.reqSettingDisplay( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 0, 0, 0 );          //DisplaySetting(明るさ／色の濃さ／コントラスト／色合い)
            mMainActivity.mdveServiceIf.reqSettingVolumeDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 1, 6 );        //音量設定(ダイレクト指定)
            mMainActivity.mdveServiceIf.reqSettingControlVol( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 2 );             //操作音量設定(OFF/1/2/3等)
            mMainActivity.mdveServiceIf.reqSettingTouchPanelSensitivity( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 1 );  //タッチパネル感度設定(High/Low)
            mMainActivity.mdveServiceIf.reqSettingToneDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 2, 3 );          //Tone(BASS/MIDRANGE/TREBLE)設定(ダイレクト)
            mMainActivity.mdveServiceIf.reqSettingFaderBalanceDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 1, 5 );  //Fader/Balance設定(左右／上下)(ダイレクト)
            mMainActivity.mdveServiceIf.reqSettingSubwooferDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, -5);        //Subwoofer設定(ダイレクト)
            mMainActivity.mdveServiceIf.reqSettingSVC( MdveUtil.ACCESSKEY, MdveUtil.VERSION, 3 );                    //車速連動音量調整機能(SVC)モード設定
            //Navigation機能関連
            mMainActivity.mdveServiceIf.reqNaviDestinationLatLon( MdveUtil.ACCESSKEY, MdveUtil.VERSION, "28.385233", "-81.566068", "Walt Disney");
            //Notification機能関連
            data = mMainActivity.mdveServiceIf.reqNotificationPopup( MdveUtil.ACCESSKEY, MdveUtil.VERSION, kind, "ポップアップ表示テスト Notification Test ポップアップ表示テスト Notification Test ポップアップ表示テスト Notification Test" );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, reqPerformanceTest." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed reqPerformanceTest.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "NotificationPopup");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "reqPerformanceTest return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqNotificationPopup", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "reqPerformanceTest Failed");
        }
        Log.d(TAG, "reqPerformanceTest()-E.");
    }



    // 戻り値をテキストボックスへ表示
    private void displayReturnValue(String returnValue, String functionName){
        String outText;
        // 戻り値表示テキストボックスを取得
        TextView text = (TextView)mView.findViewById(R.id.ReturnValue);
        // タイムスタンプ値を取得
        String timeStamp = mVehicleData.getTimestamp("[HH:mm:ss.SSS]");
        if( returnValue != null ) {
            // [タイムスタンプ][関数名][戻り値]
            outText = timeStamp + "[" + functionName + "]" + returnValue;
        }
        else{
            // [タイムスタンプ][関数名]
            outText = timeStamp + "[" + functionName + "]" + "Result is Empty";
        }
        text.setText(outText);
    }

    // 戻り値テキストボックスをクリア
    private void clearReturnValue() {
        // 戻り値表示テキストボックスを取得
        TextView text = (TextView)mView.findViewById(R.id.ReturnValue);
        text.setText("");
    }

    public IMdveCallback mIMdveCallback = new IMdveCallback.Stub(){
        @Override
        public void onNotifyVehicleData(String vehicledata){
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_NOTICE, vehicledata.toString());
            mVehicleData.setJSONStringData(vehicledata);
            mVehicleData.updateDisp();
        }
    };

    public void updateToggleButton(boolean on){
        ToggleButton tglbutton = (ToggleButton)mView.findViewById(R.id.toggleButton01);
        tglbutton.setChecked(on);
    }
}
