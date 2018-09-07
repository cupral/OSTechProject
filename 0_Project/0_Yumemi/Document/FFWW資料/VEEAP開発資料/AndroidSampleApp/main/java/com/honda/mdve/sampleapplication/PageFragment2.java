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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.honda.mdve.service.IMdveResCallback;
import com.honda.mdve.service.MdveUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class PageFragment2 extends Fragment{
    public ResponseData             mResponseData    = null;
    private final String            TAG              = "PageFragment2";
    public FragmentTransaction      ft;
    private static final boolean    DEBUG           = true;
    private static final boolean    DEBUG_TOAST     = true;
    private MainActivity            mMainActivity;
    public View                     mView = null;

    private static final int        data1Position = 0;
    private static final int        data2Position = 1;
    // FragmentとActivity間でやり取りできる　未使用
    private PageFragment.OnFragmentInteractionListener mListener;

    // コンストラクタ(必須)
    public PageFragment2() {}

    public static PageFragment2 newInstance() {
        PageFragment2 fragment = new PageFragment2();
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
        mResponseData = new ResponseData();
        // 子Fragmentに子Fragmentを付ける。自身のFragment Managerを取得
        // getFragmentManager()←こちらは使わないこと．
        ft = this.getChildFragmentManager().beginTransaction();
        ft.replace(R.id.responsedata_container, mResponseData, "responsedata").commit();
        // インフレーターの取得(findViewByIdを使うため)
        mView = inflater.inflate(R.layout.pagefragment2, container, false);
        // TouchUp EventHandler関数の登録
        for( int btnID: WidgetData.onTouchUpList2) {
            Button btn = (Button) mView.findViewById(btnID);
            btn.setOnTouchListener(btnTouchListener);
        }

        // Spinnerに表示するリストの登録
        for(Iterator<Integer> iterator = WidgetData.spinnerMap2.keySet().iterator(); iterator.hasNext(); ){
            Integer key = iterator.next();
            Spinner spinner = (Spinner) mView.findViewById(key);
            // MainActivityのインスタンス(Context)を渡す
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mMainActivity, WidgetData.spinnerMap2.get(key), R.layout.spinner_layout);
            // Spinner Dropdown Listのデザイン変更
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spinner.setAdapter(adapter);
        }
        // XMLの埋め込みでは効かない　テキストエディタの入力を制御
        EditText edittext = (EditText) mView.findViewById(R.id.ReturnValue);
        edittext.setInputType(0);
        return mView;
    }

    @Override
    public void onStart () {
        super.onStart();
        // 初期選択状態の設定 ※1
        // Spinnerをフォーカス有効にする
        for(Iterator<Integer> iterator = WidgetData.spinnerMap2.keySet().iterator(); iterator.hasNext(); ){
            Integer key = iterator.next();
            Spinner spinner = (Spinner) mView.findViewById(key);
            spinner.setFocusable(true);
            spinner.setFocusableInTouchMode(true);
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity();
    }

    //onAttach(Activity activity)は非推奨
    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof PageFragment.OnFragmentInteractionListener) {
            // ActivityとFragmentの紐付け
            mListener = (PageFragment.OnFragmentInteractionListener) context;
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
        ft.remove(mResponseData);
    }

    public interface OnFragmentInteractionListener {
        void onFragment2Interaction(Uri uri);
    }

    public void onitemclicklistener( String no){
        mMainActivity.startBeepTone();
        ResponseData.DataItem dataitem = mResponseData.mResponseDataItem.DataItemMap.get(no);
        if (dataitem != null) {
            if( mResponseData.mResponseDataItem.command.equals("3008") || mResponseData.mResponseDataItem.command.equals("3010") ){
                // AM/FM/SXM
                if( mResponseData.mResponseDataItem.audiosource.equals("0") || mResponseData.mResponseDataItem.audiosource.equals("1") ){
                    if (DEBUG) Log.d(TAG, "onTouchRelease AudioSelectPresetList Send on ListView");
                    reqAudioSelectPresetList_local(Integer.parseInt(mResponseData.mResponseDataItem.audiosource),
                            Integer.parseInt(dataitem.dataArray.get(data1Position).toString()),
                            Integer.parseInt(dataitem.dataArray.get(data2Position).toString()));
                }
                else if( mResponseData.mResponseDataItem.audiosource.equals("2") ){
                    if (DEBUG) Log.d(TAG, "onTouchRelease AudioSelectPresetListSXM Send on ListView");
                    reqAudioSelectPresetListSXM_local(Integer.parseInt(mResponseData.mResponseDataItem.audiosource),
                            Integer.parseInt(dataitem.dataArray.get(data1Position).toString()));
                }
                else{
                    // エラー
                    Log.e(TAG, "onItemClickLister: Failed to Call Function  command:" + mResponseData.mResponseDataItem.command +
                            " audiosouce:" + mResponseData.mResponseDataItem.audiosource);
                }
            }
            else if( mResponseData.mResponseDataItem.command.equals("3016") ){
                if (DEBUG) Log.d(TAG, "onTouchRelease AudioFreqDirect Send on ListView");
                reqAudioFreqDirect_local(Integer.parseInt(mResponseData.mResponseDataItem.audiosource),
                        Integer.parseInt(dataitem.dataArray.get(data1Position).toString()));
            }
            else {
                // エラー
                Log.e(TAG, "onItemClickLister: Failed to Call Function  command:" + mResponseData.mResponseDataItem.command +
                        " audiosouce:" + mResponseData.mResponseDataItem.audiosource);
            }
        } else {
            Log.e(TAG, "onitemclicklistener: responsedataitem class instance is null");
        }
    }

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
                        case R.id.AudioGetPresetList_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioGetPresetList Send Button");
                            reqAudioGetPresetList_local();
                            break;
                        case R.id.AudioSetPresetList_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSetPresetList Send Button");
                            reqAudioSetPresetList_local();
                            break;
                        case R.id.AudioSetPresetListSXM_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioSetPresetListSXM Send Button");
                            reqAudioSetPresetListSXM_local();
                            break;
                        case R.id.AudioGetStationList_Send:
                            if (DEBUG) Log.d(TAG, "onTouchRelease AudioGetStationList Send Button");
                            reqAudioGetStationList_local();
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

    // プリセットリスト取得要求
    private void reqAudioGetPresetList_local(){
        String  data    = null;
        int     source  = 0;
        clearReturnValue();
        mResponseData.initResponsedata();
        try {
            Spinner spinner1   = (Spinner)mView.findViewById(R.id.AudioGetPresetList_source);
            source             = WidgetData.AudioGetPresetList_source[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioGetPresetList()をコールする
            Log.d(TAG, "Called reqAudioGetPresetList()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source );
            data = mMainActivity.mdveServiceIf.reqAudioGetPresetList( mIMdveResCallback, MdveUtil.ACCESSKEY, MdveUtil.VERSION, source );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioGetPresetList()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioGetPresetList.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioGetPresetList");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioGetPresetList source:"+ source + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioGetPresetList", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioGetPresetList Failed source:" + source);
        }
    }

    // プリセット登録[AM/FM]
    private void reqAudioSetPresetList_local(){
        String  data        = null;
        int     source      = 0;
        int     preset      = 0;
        int     presetno    = 0;
        int     freq        = 0;
        clearReturnValue();
        mResponseData.initResponsedata();
        try {
            Spinner spinner1   = (Spinner)mView.findViewById(R.id.AudioSetPresetList_source);
            Spinner spinner2   = (Spinner)mView.findViewById(R.id.AudioSetPresetList_preset);
            source             = WidgetData.AudioSetPresetList_source[spinner1.getSelectedItemPosition()];
            preset             = WidgetData.AudioSetPresetList_preset[spinner2.getSelectedItemPosition()];
            EditText text1     = (EditText)mView.findViewById(R.id.AudioSetPresetList_presetno_edit);
            EditText text2     = (EditText)mView.findViewById(R.id.AudioSetPresetList_freq_edit);
            if( text1.getText().toString().equals("") ){
                presetno = 0;
            }
            else {
                presetno = Integer.parseInt(text1.getText().toString());
            }
            if( text2.getText().toString().equals("") ){
                freq = 0;
            }
            else {
                freq = Integer.parseInt(text2.getText().toString());
            }
            // IMdveService.aidl#reqAudioSetPresetList()をコールする
            Log.d(TAG, "Called reqAudioSetPresetList()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " parma4=" + preset +
                    " param5=" + presetno +
                    " param6=" + freq);
            data = mMainActivity.mdveServiceIf.reqAudioSetPresetList( mIMdveResCallback, MdveUtil.ACCESSKEY, MdveUtil.VERSION, source, preset, presetno, freq );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSetPresetList()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSetPresetList.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSetPresetList");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetPresetList source:" + source + " preset:" + preset + " presetno:" + presetno +
                    " freq:" + freq + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSetPresetList", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetPresetList Failed source:" + source + " preset:" + preset + " presetno:" + presetno +
                    " freq:" + freq);
        }
    }

    // プリセット登録[SXM]
    private void reqAudioSetPresetListSXM_local(){
        String  data        = null;
        int     source      = 0;
        int     presetno    = 0;
        int     channelno   = 0;
        clearReturnValue();
        mResponseData.initResponsedata();
        try {
            Spinner spinner1   = (Spinner)mView.findViewById(R.id.AudioSetPresetListSXM_source);
            source             = WidgetData.AudioSetPresetListSXM_source[spinner1.getSelectedItemPosition()];
            EditText text1     = (EditText)mView.findViewById(R.id.AudioSetPresetListSXM_presetno_edit);
            EditText text2     = (EditText)mView.findViewById(R.id.AudioSetPresetListSXM_channelno_edit);
            if( text1.getText().toString().equals("") ){
                presetno = 0;
            }
            else {
                presetno = Integer.parseInt(text1.getText().toString());
            }
            if( text2.getText().toString().equals("") ){
                channelno = 0;
            }
            else {
                channelno = Integer.parseInt(text2.getText().toString());
            }
            // IMdveService.aidl#reqAudioSetPresetListSXM()をコールする
            Log.d(TAG, "Called reqAudioSetPresetListSXM()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " param4=" + presetno +
                    " param5=" + channelno);
            data = mMainActivity.mdveServiceIf.reqAudioSetPresetListSXM( mIMdveResCallback, MdveUtil.ACCESSKEY, MdveUtil.VERSION, source, presetno, channelno );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSetPresetListSXM()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSetPresetListSXM.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSetPresetListSXM");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetPresetListSXM source:" + source + " preset:" + presetno + " channelno:" + channelno +
                    " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSetPresetListSXM", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSetPresetListSXM Failed source:" + source + " preset:" + presetno + " channelno:" + channelno);
        }
    }

    // ステーションリスト要求(AM/FM)
    private void reqAudioGetStationList_local(){
        String  data    = null;
        int     source  = 0;
        clearReturnValue();
        mResponseData.initResponsedata();
        try {
            Spinner spinner1   = (Spinner)mView.findViewById(R.id.AudioGetStationList_source);
            source             = WidgetData.AudioGetStationList_source[spinner1.getSelectedItemPosition()];
            // IMdveService.aidl#reqAudioGetStationList()をコールする
            Log.d(TAG, "Called reqAudioGetStationList()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source );
            data = mMainActivity.mdveServiceIf.reqAudioGetStationList( mIMdveResCallback, MdveUtil.ACCESSKEY, MdveUtil.VERSION, source );
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioGetStationList()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioGetStationList.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioGetStationList");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioGetStationList source:"+ source + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioGetStationList", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioGetStationList Failed source:" + source);
        }
    }

    // プリセット選択＆再生[AM/FM]
    private void reqAudioSelectPresetList_local(int source, int preset, int presetno){
        String data = null;
        clearReturnValue();
        try {
            // IMdveService.aidl#reqAudioSelectPresetList()をコールする
            Log.d(TAG, "Called reqAudioSelectPresetList()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " param4=" + preset +
                    " param5=" + presetno );
            data = mMainActivity.mdveServiceIf.reqAudioSelectPresetList( MdveUtil.ACCESSKEY, MdveUtil.VERSION, source , preset, presetno);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSelectPresetList()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSelectPresetList.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSelectPresetList");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSelectPresetList source:"+ source + " preset:" + preset + " presetno:"
                                            +  presetno + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSelectPresetList", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSelectPresetList Failed source:" + source + " preset:" + preset + " presetno:" +  presetno);
        }
    }

    // プリセット選択&再生[SXM]
    private void reqAudioSelectPresetListSXM_local(int source, int presetno){
        String data = null;
        clearReturnValue();
        try {
            // IMdveService.aidl#reqAudioSelectPresetListSXM()をコールする
            Log.d(TAG, "Called reqAudioSelectPresetListSXM()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " param4=" + presetno );
            data = mMainActivity.mdveServiceIf.reqAudioSelectPresetListSXM( MdveUtil.ACCESSKEY, MdveUtil.VERSION, source , presetno);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioSelectPresetListSXM()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioSelectPresetListSXM.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioSelectPresetListSXM");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSelectPresetListSXM source:"+ source + " presetno:" +  presetno + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioSelectPresetListSXM", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioSelectPresetListSXM Failed source:" + source + " presetno:" +  presetno);
        }
    }

    // 周波数指定(FM/AM)
    private void reqAudioFreqDirect_local(int source, int freq){
        String data = null;
        clearReturnValue();
        try {
            // IMdveService.aidl#reqAudioFreqDirect()をコールする
            Log.d(TAG, "Called reqAudioFreqDirect()." +
                    " param1=" + MdveUtil.ACCESSKEY +
                    " param2=" + MdveUtil.VERSION +
                    " param3=" + source +
                    " param4=" + freq );
            data = mMainActivity.mdveServiceIf.reqAudioFreqDirect( MdveUtil.ACCESSKEY, MdveUtil.VERSION, source , freq);
        } catch (Exception e) {
            Log.e(TAG, "Exeption, called mMainActivity.mdveServiceIf,reqAudioFreqDirect()." + "Exeption:" + e.toString());
            if(DEBUG_TOAST) Toast.makeText(getActivity(), "failed calling IMdveService.reqAudioFreqDirect.", Toast.LENGTH_LONG).show();
            data = "";
        }
        // テキストボックスにログを出力
        displayReturnValue(data, "AudioFreqDirect");
        if (data != null) {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioFreqDirect source:"+ source + " freq:" +  freq + " return:" + data.toString());
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDREQ, "reqAudioFreqDirect", data.toString());
        }
        else {
            // ログ出力
            if( DEBUG ) Log.d(TAG, "call reqAudioFreqDirect Failed source:" + source + " freq:" +  freq);
        }
    }

    // 戻り値表示テキストボックスをクリアする
    private void clearReturnValue() {
        EditText text = (EditText)mView.findViewById(R.id.ReturnValue);
        text.setText("");
    }

    // 戻り値をテキストボックスへ表示する
    private void displayReturnValue(String returnValue, String functionName){
        String outText;
        EditText text = (EditText)mView.findViewById(R.id.ReturnValue);
        String timeStamp = getTimestamp("[HH:mm:ss.SSS]");
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

    // 関数応答有り用コールバック関数
    public IMdveResCallback mIMdveResCallback = new IMdveResCallback.Stub(){
        @Override
        public void onNotifyResData(String responseData){
            Log.d(TAG, "onNotifyResData jsonCode:" + responseData);
            mMainActivity.mWriteLog.write_apprecv(WriteLog.MSGKIND.MSGKIND_MDRES, responseData.toString());
            mResponseData.jsonDataParse_process(responseData);
        }
    };

    // タイムスタンプ取得関数
    private String getTimestamp(String format){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }
}
