package com.honda.mdve.sampleapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class VehicleData extends Fragment {
    private static final String TAG = "VehicleData";
    private static final boolean DEBUG = true;

    private ParseJsonData m_parseJsonData = new ParseJsonData();
    private ScrollView mView;
    TableLayout mVehicledata_layout = null;
    Handler mHandler;

    private static final int HANDLER_ID_UPDATE_ITEM = 1;
    private static final int HANDLER_ID_UPDATE_DISP = 2;

    private static final int LAYOUT_NAME_WIDTH = 500;
    private static final int LAYOUT_DATA_WIDTH = 600;
    private static final int LAYOUT_STATUS_WIDTH = 50;
    private static final int LAYOUT_FONT_SIZE = 10;

    private static int mVehicleNum = 0;
    private static final String SampleApplicationKey = "com.honda.mdve.sampleapplication";

    public class ItemViewLine {
        TextView tv_name;
        TextView tv_data;
        TextView tv_status;
        TableRow tb_row;
        ItemViewLine(){
            this.tv_name = new TextView(getActivity());
//            this.tv_name.setGravity(Gravity.LEFT);
            this.tv_name.setWidth(LAYOUT_NAME_WIDTH);
            this.tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, LAYOUT_FONT_SIZE);
            this.tv_name.setPadding(5, 0, 0, 0);

            this.tv_status = new TextView(getActivity());
            this.tv_status.setWidth(LAYOUT_STATUS_WIDTH);
            this.tv_status.setGravity(Gravity.LEFT);
            this.tv_status.setTextSize(TypedValue.COMPLEX_UNIT_SP, LAYOUT_FONT_SIZE);

            this.tv_data = new TextView(getActivity());
            this.tv_data.setWidth(LAYOUT_DATA_WIDTH);
            this.tv_data.setGravity(Gravity.LEFT);
            this.tv_data.setTextSize(TypedValue.COMPLEX_UNIT_SP, LAYOUT_FONT_SIZE);

            mVehicleNum++;
            if (mVehicleNum % 2 == 0) {
                this.tv_name.setBackgroundColor(Color.parseColor("#b0c4de"));
                this.tv_data.setBackgroundColor(Color.parseColor("#b0c4de"));
                this.tv_status.setBackgroundColor(Color.parseColor("#b0c4de"));
            }
            else {
                this.tv_name.setBackgroundColor(Color.parseColor("#dcdcdc"));
                this.tv_data.setBackgroundColor(Color.parseColor("#dcdcdc"));
                this.tv_status.setBackgroundColor(Color.parseColor("#dcdcdc"));
            }

            tb_row = new TableRow( getActivity() );
            tb_row.addView(this.tv_name);
            tb_row.addView(this.tv_status);
            tb_row.addView(this.tv_data);
        }
    };

    public class VehcledataItem {
        String name;
        String data;
        String status;
        ItemViewLine line;

        VehcledataItem(String name, String data, String status, ItemViewLine line) {
            this.name = name;
            this.data = data;
            this.status = status;
            this.line = line;

            if(this.line != null){
                setItemViewLine();
            }
        }

        public void replaseItemViewLine(ItemViewLine line){
            this.line = line;
            setItemViewLine();
        }

        public void setItemViewLine(){
            this.line.tv_name.setText(name);
            this.line.tv_data.setText(data);
            this.line.tv_status.setText(status);
        }
    }

    static public LinkedHashMap<String, VehcledataItem> mVehcledataItemMap = new LinkedHashMap<String, VehcledataItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mView = (ScrollView) inflater.inflate(R.layout.vehicledata_sub, container, false);
        mView.getLayoutParams().width = ScrollView.LayoutParams.MATCH_PARENT;
        mView.getLayoutParams().height = ScrollView.LayoutParams.MATCH_PARENT;
        mView.setFillViewport(true);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        init_VehcledataItemMap();

        // HandlerThread
        mHandler = new sendDataHandler();

        return;
    }

    @Override
    public void onDestroyView(){
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        mVehicledata_layout.removeAllViews();
        mVehicledata_layout = null;
        mView.removeAllViews();
    }

    private void init_VehcledataItemMap(){
        mVehicledata_layout = new TableLayout(getActivity());
        //mVehicledata_layout.setColumnStretchable(0, true);

        mView.addView(mVehicledata_layout);

        // 再描画時、改めて車両データの描画を行う. *コメントアウト
//        for(VehcledataItem vItem:mVehcledataItemMap.values()){
//            if(DEBUG)Log.d(TAG, "mVehicledataItemMap replace." );
//            vItem.replaseItemViewLine(new ItemViewLine());
//            mVehicledata_layout.addView(vItem.line.tb_row);
//        }
        // 再描画時、それまでに取得した車両データは破棄する.
        mVehcledataItemMap.clear();
    }

    public void setJSONStringData(String data){
        mVehicleNum = 0;
        m_parseJsonData.parseJSON_Vehcledata(data, new ParseJsonData.Token() {
            @Override
            public int setToken(ArrayList<String> string) {
                Bundle bundle = new Bundle();
                bundle.putStringArray(SampleApplicationKey, new String[]{string.get(0), string.get(1), string.get(2)});
                mHandler.sendMessage(mHandler.obtainMessage(HANDLER_ID_UPDATE_ITEM, bundle));

                return 0;
            }
        });
    }

    private class sendDataHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case HANDLER_ID_UPDATE_ITEM:
                    if( getActivity() == null ) {
                        Log.w(TAG, "Failed to getActivity()");
                    }
                    else {
                        String[] strings = ((Bundle) msg.obj).getStringArray(SampleApplicationKey);
                        setVehicleDataitem(strings[0], strings[1], strings[2]);
                    }
                    break;
                case HANDLER_ID_UPDATE_DISP:
                    if( getActivity() == null ) {
                        Log.w(TAG, "Failed to getActivity()");
                    }
                    else {
                        updateDisp_work();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private int setVehicleDataitem(String name, String status, String data){
        int ret;
        ret = setVehicleDataitem_common(name, status, data);
        if( ret != 0){
            return ret;
        }

        return ret;
    }

    private int setVehicleDataitem_common(String name, String status, String data){
        String newdata;
        String newstatus;

        // アイテムが未登録の場合, TextViewの追加登録.
        if( !mVehcledataItemMap.containsKey(name)){
            Log.d(TAG, "new item add. name:" + name);

            if (status.equals("1")) {
                newdata = data;
                newstatus = status;
            } else if (status.equals("0")) {
                newdata = data;
                newstatus = status;
            } else {
                Log.e(TAG, "status check error name:" + name + ", status:" + status + ", data:" + data);
                newdata = "0";
                newstatus = "0";
            }
            mVehcledataItemMap.put(name, new VehcledataItem(name, newdata, newstatus, new ItemViewLine()));
            VehcledataItem item = mVehcledataItemMap.get(name);
            mVehicledata_layout.addView(item.line.tb_row);
            return 0;
        }

        // アイテムが登録済みの場合, ItemMapの更新のみ
        if (status.equals("1")) {
            newdata = data;
            newstatus = status;
        } else if (status.equals("0")) {
            newdata = data;
            newstatus = status;
        } else {
            Log.e(TAG, "status check error name:" + name + ", status:" + status + ", data:" + data);
            newdata = "0";
            newstatus = "0";
        }

        ItemViewLine tb = mVehcledataItemMap.get(name).line;
        mVehcledataItemMap.put(name, new VehcledataItem(name, newdata, newstatus, tb));

        return 0;
    }

    public void updateDisp() {
        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_ID_UPDATE_DISP));
    }

    private void updateDisp_work(){
        int updateTimeviewid = R.id.viewupdateTime;

        Iterator entries = mVehcledataItemMap.entrySet().iterator();
        while (entries.hasNext())
        {
            String data;
            String status;
            String measure;

            Map.Entry entry = (Map.Entry) entries.next();
            VehicleData.VehcledataItem item = (VehicleData.VehcledataItem) entry.getValue();
            data = item.data;
            status = item.status;
            TextView textview_tag = item.line.tv_data;
            TextView textview_status = item.line.tv_status;

            if ((textview_tag != null) && (textview_status != null)) {
                textview_tag.setText(data);
                textview_status.setText(status);
            }
        }

        if (!(isDetached() || getActivity() == null) ){
            TextView updateTimeview = (TextView) getActivity().findViewById(updateTimeviewid);
            String timestamp = getTimestamp("HH:mm:ss.SSS");
            updateTimeview.setText(timestamp);
        }
    }

    public String getTimestamp(String format){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }
}
