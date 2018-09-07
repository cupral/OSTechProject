package com.honda.mdve.sampleapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.honda.mdve.service.*;

import static com.honda.mdve.sampleapplication.VehcleDataDef.*;
// ★Tips - ライフサイクルは基本的に”開始”がActivityが先、Fragmentが後
//                                  ”終了”がFragmentが先、Activityが後

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, PageFragment.OnFragmentInteractionListener, PageFragment2.OnFragmentInteractionListener{
    private static final String[] pageTitle      = {"車両データ確認画面", "イベント応答確認画面"};
    private static PageFragment  mPageFragment   = null;
    private static PageFragment2 mPageFragment2  = null;
    public static final String   TAG             = "MainActivity";
    private static final boolean  DEBUG          = true;
    private static final boolean  DEBUG_TOAST    = true;
    public ServiceConnection     mdveServiceConn = null;
    public IMdveService          mdveServiceIf   = null;
    public WriteLog              mWriteLog;
    private ToneGenerator        mToneGenerator = null;

    /*
        注意：findViewByIdは、Viewの置かれているXMLを指定する必要有り
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // アクションバーをツールバーに変更する
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.inflateMenu(R.menu.menu_toolbar);
//        setSupportActionBar(toolbar);
        // Activity配下に置くFragmentを管理
        FragmentPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // ViewPagerにページを設定
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        // ViewPagerをTabLayoutを設定 横スワイプでTabと連動表示
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // ログ設定
        mWriteLog = new WriteLog(this);
        // ボタン動作音設定
        mToneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
        // MDVE接続処理
        isConnectservice();
    }
    // innerclass
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem:position" + String.valueOf(position));
            switch (position) {
                case 0:
                    // PageFragment1
                    mPageFragment = PageFragment.newInstance();
                    return mPageFragment;
                case 1:
                    // PageFragment2
                    mPageFragment2 = PageFragment2.newInstance();
                    return mPageFragment2;
                default:
                    Log.e(TAG, "getItem:Error position" + String.valueOf(position));
                    return null;
            }
        }

        // タブ名表示
        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }

        // タブ表示個数を返すこと
        @Override
        public int getCount() {
            return pageTitle.length;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
//        return true;
//    }

    // Interface
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // FragmentとActivityでやり取り可能
    }

    @Override
    public void onFragment2Interaction(Uri uri) {
        // FragmentとActivityでやり取り可能
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        Log.d(TAG, savedInstanceState.toString());
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mPageFragment  = null;
        mPageFragment2 = null;
        if(mToneGenerator !=  null) {
            // Releases resources associated with this ToneGenerator object
            mToneGenerator.release();
            mToneGenerator = null;
        }
    }

    private void registservice() {
        mdveServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mdveServiceIf = IMdveService.Stub.asInterface(service);
                if(DEBUG) Log.d(TAG, "onServiceConnected mdveServiceIf=" + mdveServiceIf);
                if(DEBUG_TOAST) Toast.makeText(MainActivity.this, "MdveService connect", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mdveServiceIf = null;
                if( mPageFragment != null ) {
                    if (DEBUG) Log.d(TAG, "onServiceDisconnected mdveServiceIf=" + mdveServiceIf);
                    mPageFragment.updateToggleButton(false);
                    mPageFragment2.mResponseData.initResponsedata();
                    if (DEBUG_TOAST)
                        Toast.makeText(MainActivity.this, "MdveService disconnected", Toast.LENGTH_LONG).show();
                }
                else{
                    if (DEBUG) Log.d(TAG,"onServiceDisconncted tab1fragment=null");
                }
            }
        };

        Intent intent = new Intent(IMdveService.class.getName());
        intent.setPackage("com.honda.mdve.service");
        this.bindService(intent, mdveServiceConn, Context.BIND_AUTO_CREATE);
        if(DEBUG) Log.d(TAG, "mdveServiceConn=" + mdveServiceConn);
    }

    public void unregistservice() {
        if(mdveServiceConn != null) {
            this.unbindService(mdveServiceConn);
            mdveServiceConn = null;
        }
        if(DEBUG_TOAST) Toast.makeText(MainActivity.this, "MdveService disconnect", Toast.LENGTH_LONG).show();
    }

    public boolean isConnectservice(){
        if(mdveServiceConn == null){
            registservice();
        }

        if(mdveServiceIf == null){
            return false;
        }else{
            return true;
        }
    }

    /*
     *  ボタン押下音を鳴動させる
     */
    public void startBeepTone(){
        // ボタン動作音設定
        if(mToneGenerator !=  null) {
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        }
    }
}

