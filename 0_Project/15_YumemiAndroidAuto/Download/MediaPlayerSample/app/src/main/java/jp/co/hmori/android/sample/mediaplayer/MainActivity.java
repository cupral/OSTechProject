package jp.co.hmori.android.sample.mediaplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends Activity implements MediaPlayer.OnPreparedListener{
    private final static String[] COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
    };

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, COLUMNS, null, null, null);

        ListView listView = (ListView)findViewById(R.id.play_list);
        listView.setAdapter(new SimpleCursorAdapter(
                this, R.layout.list_at, cursor, new String[]{MediaStore.Audio.Media.TITLE},
                new int[]{R.id.title}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                Cursor cursor = (Cursor)listView.getItemAtPosition(position);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                try {
                    if (mMediaPlayer == null) {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setOnPreparedListener(MainActivity.this);
                    } else {
                        mMediaPlayer.reset();
                    }

                    mMediaPlayer.setDataSource(path);
                    mMediaPlayer.prepareAsync();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
