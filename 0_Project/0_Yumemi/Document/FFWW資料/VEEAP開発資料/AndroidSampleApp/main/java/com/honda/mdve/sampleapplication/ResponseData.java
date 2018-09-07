package com.honda.mdve.sampleapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResponseData extends Fragment {
    private static final String     TAG                     = "ResponseData";
    private static final boolean    DEBUG                   = true;
    private static final boolean    DEBUG_TOAST             = true;
    private ListView                mListView               = null;

    private ParseJsonData m_parseJsonData         = new ParseJsonData();
    Handler mHandler;
    public  static ResponseDataItem mResponseDataItem       = null;

    private static final String     SampleApplicationKey    = "com.honda.mdve.sampleapplication";

    private static final int     noDataPosition             = 0;
    private static final int     listTextDataPosition       = 1;
    public ResponseData(){}

    // Inner Class SingleTon
    public class ResponseDataItem {
        String code;
        String command;
        String count;
        String audiosource;

        // noがkey dataがdata1,data2,...,dataXX
        LinkedHashMap<String, DataItem> DataItemMap = new LinkedHashMap<String, DataItem>();
    }
    // Inner Class Multiton
    public class DataItem {
        ArrayList<String> dataArray = new ArrayList<String>();
        String listText;
        // コンストラクタ
        DataItem(ArrayList<String> data, String listText) {
            this.listText = listText;
            for(String dataValue: data) {
                dataArray.add(dataValue);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mResponseDataItem = new ResponseDataItem();
        mListView = (ListView)inflater.inflate(R.layout.responsedata_sub, container, false);
        mListView.setOnItemClickListener(onitemclicklistener);
        return mListView;
    }

    private ListView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick");
            // 色付け
            view.setSelected(true);
            String no = String.valueOf(view.getTag());
            if (no != null) {
                // 親FragmentのInstanceを取得し、ItemClickイベントのコールバックを親Fragmentへ行う．
                ((PageFragment2) getParentFragment()).onitemclicklistener(no);
            }
            // 選択されたアイテム(position)に対してレイアウトを変更
            parent.getAdapter().getView(position, view, parent);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mResponseDataItem.DataItemMap.clear();
        // HandlerThread
        mHandler = new Handler();

        return;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        ArrayAdapter adapter = (ArrayAdapter) mListView.getAdapter();
        if( adapter != null ) {
            adapter.clear();
            // アダプターに対し、更新を報告
            adapter.notifyDataSetChanged();
        }
        //mListView.removeAllViews();
        mHandler = null;
        mListView = null;
    }

    // 画面UIの初期化処理と内部保持変数の初期化
    public void initResponsedata() {
        View view = ((PageFragment2)getParentFragment()).mView;
        ArrayAdapter adapter = (ArrayAdapter) mListView.getAdapter();
        if( adapter != null ) {
            adapter.clear();
            // アダプターに対し、更新を報告
            adapter.notifyDataSetChanged();
        }
        mListView.setAdapter(null);

        TextView code                    = (TextView) view.findViewById(R.id.code);
        TextView command                 = (TextView) view.findViewById(R.id.command);
        TextView count                   = (TextView) view.findViewById(R.id.count);
        TextView audiosource             = (TextView) view.findViewById(R.id.audiosource);
        mResponseDataItem.code           = "-";
        mResponseDataItem.command        = "-";
        mResponseDataItem.count          = "-";
        mResponseDataItem.audiosource    = "-";

        code.setText(mResponseDataItem.code);
        command.setText(mResponseDataItem.command);
        count.setText(mResponseDataItem.count);
        audiosource.setText(mResponseDataItem.audiosource);
        mResponseDataItem.DataItemMap.clear();
    }

    // JSON DataのParse処理
    public void setJSONStringData(String data) {
        m_parseJsonData.parseJSON_Responsedata(data, new ParseJsonData.Token() {
            @Override
            public int setToken(ArrayList<String> string) {
                String num = string.get(noDataPosition);
                String listtext = string.get(listTextDataPosition);
                // ArrayListからnoとlistTextに該当する文字列データを削除、先頭詰め
                string.remove(0);
                string.remove(0);
                mResponseDataItem.DataItemMap.put(num, new DataItem(string, listtext));
                return 0;
            }
        });
    }

    public static void setCode(String code){
        mResponseDataItem.code = code;
    }
    public static void setCommand(String command){
        mResponseDataItem.command = command;
    }
    public static void setCount(String count){
        mResponseDataItem.count = count;
    }
    public static void setAudiosource(String audiosource){
        mResponseDataItem.audiosource = audiosource;
    }

    // メインスレッドとは非同期のサブスレッドで描画処理を行いたいが、サブスレッドで画面上UIに対しアクセスを行うと例外落ちが発生する．
    // その対策として、サブスレッドからメインスレッドへの橋渡し役としてHandler classを利用する．
    public void jsonDataParse_process(final String jsonData){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initResponsedata();
                        setJSONStringData(jsonData);
                        updateDisp();
                    }
                });
            }
        }).start();
    }

    // Parse処理結果を、画面上UIへ表示する
    private void updateDisp() {
        View view = ((PageFragment2)getParentFragment()).mView;
        TextView code           = (TextView) view.findViewById(R.id.code);
        TextView command        = (TextView) view.findViewById(R.id.command);
        TextView count          = (TextView) view.findViewById(R.id.count);
        TextView audiosource    = (TextView) view.findViewById(R.id.audiosource);
        Log.d(TAG, "updateDisp_work: code=" + mResponseDataItem.code + " command=" + mResponseDataItem.command + " count=" + mResponseDataItem.count + " audiosource=" + mResponseDataItem.audiosource);
        code.setText(mResponseDataItem.code);
        command.setText(mResponseDataItem.command);
        count.setText(mResponseDataItem.count);
        audiosource.setText(mResponseDataItem.audiosource);

        List<ListViewData> objects = new ArrayList<ListViewData>();
        for (String key : mResponseDataItem.DataItemMap.keySet()) {
            DataItem dataitem = mResponseDataItem.DataItemMap.get(key);
            // 受信したJson Codeをそのまま出力
            String listText = key + ":" + "\n" + dataitem.listText;
            ListViewData item = new ListViewData();
            item.setListText(listText);
            item.setNo(key);
            objects.add(item);
        }
        if( objects.isEmpty() != true ){
            CustomListViewAdapter customlistviewadapter = new CustomListViewAdapter(getActivity(), 0, objects);
            mListView.setAdapter(customlistviewadapter);
            // アダプターに対し、更新を報告
            customlistviewadapter.notifyDataSetChanged();
        }
    }
}
