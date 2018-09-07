package com.honda.mdve.sampleapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter<ListViewData>{

    private LayoutInflater mLayoutInflater;

    // コンストラクタ
    public CustomListViewAdapter(Context context, int textViewResourceId, List<ListViewData> objects) {
        super(context, textViewResourceId, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 新しい一行が表示される度に呼ばれる
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        ListViewData item = (ListViewData)getItem(position);
        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る.初回はnull
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.listview_textview, null);
        }
        // リストビューのアイテムに対して交互に着色
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.drawable.listview_item_selected_first);
        }
        else {
            convertView.setBackgroundResource(R.drawable.listview_item_selected_second);
        }

        TextView listtext = (TextView)convertView.findViewById(R.id.ListViewItem);
        // テキストボックスへ文字を出力
        listtext.setText(item.getListText());
        // ListViewイベントハンドラ用にタグ登録
        listtext.setTag(item.getNo());

        return convertView;
    }
}
