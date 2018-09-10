package jp.co.hmori.android.sample.mediaplayer.auto;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaDescription;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import jp.co.hmori.android.sample.mediaplayer.R;

public class MyMediaBrowserService extends android.service.media.MediaBrowserService {
    private static final String MEDIA_SESSION_TAG   = "tag for debug";
    private static final String MEDIA_BROWSER_ROOT  = "ROOT";
    private static final String MEDIA_BROWSER_CHILD = "CHILD";

    private static final String ANDROID_AUTO_PACKAGE_NAME = "some.specific.package.to.handle";
    private static final String SIMULATOR_PACKAGE_NAME = "com.google.android.mediasimulator";

    private static final String[] COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
    };

    private MyMediaSessionCallback mSessionCallback;

    public void onCreate() {
        super.onCreate();

        // メディアセッションを作成します。
        MediaSession session = new MediaSession(this, MEDIA_SESSION_TAG);
        setSessionToken(session.getSessionToken());

        // 再生、停止などのイベントを受け取るためのコールバックを設定します。
        mSessionCallback = new MyMediaSessionCallback(session);
        session.setCallback(mSessionCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSessionCallback != null) {
            mSessionCallback.release();
            mSessionCallback = null;
        }
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        // MediaBrowserServiceは、exportedをtrueにしておく必要があります。
        // それはつまり、無関係の、使用してほしくないアプリからでも呼び出せかねないということです。
        // 呼び出しの許可不許可を、ここでコントロールします。
        if (!isCallerAllowed(clientPackageName, clientUid)) {
            // nullを返した場合、不許可であることを意味します。
            return null;
        }

        if (ANDROID_AUTO_PACKAGE_NAME.equals(clientPackageName)) {
            // パッケージによって動作を変えたい場合も、ここでハンドリングします
            // 広告や、音楽ライブラリなどの目的を想定しているようです。
        }

        return new BrowserRoot(MEDIA_BROWSER_ROOT, null);
    }

    /**
     * このパッケージ／UIDからの呼び出しに応じるか、チェックする
     * @param packageName 呼び出し元のパッケージ
     * @param clientUid 呼び出し元のUID
     * @return このパッケージ/UIDから使用して良い場合はtrueを返却します。
     */
    private boolean isCallerAllowed(String packageName, int clientUid) {
        // 今回は自アプリと、シミュレータのみを許可します。
        return (getApplicationInfo().uid == clientUid
                || getPackageName().equals(packageName)
                || SIMULATOR_PACKAGE_NAME.equals(packageName));
    }

    /**
     * onGetRootのあと、またはMediaItem.FLAG_BROWSABLEのフラグが付いている
     * MediaItemを選択した時に呼ばれます。
     * parentMediaIdで親のIDをチェックして、動作を変更します。
     */
    @Override
    public void onLoadChildren(final String parentMediaId,
                               final Result<List<MediaItem>> result)  {
        // Assume for example that the music catalog is already loaded/cached.
        List<MediaItem> mediaItems = new ArrayList<>();

        if (MEDIA_BROWSER_ROOT.equals(parentMediaId)) {
            // ルートIDの場合、サブメニューを表示するようにします。
            for(int i = 0; i < 5; i++) {
                MediaDescription mediaDescription
                        = (new MediaDescription.Builder())
                        .setTitle(getString(R.string.test_title, i))
                        .setSubtitle(getString(R.string.test_subtitle, i))
                        // 実際には、個別にメディアIDを変えて下さい
                        .setMediaId(MEDIA_BROWSER_CHILD)
                        .setDescription(null)
                        .setIconBitmap(null)
                        .build();

                MediaItem item = new MediaItem(mediaDescription,
                        // 曲自体ではなく、リストメニューなどの閲覧用のアイテムであることを示します
                        MediaItem.FLAG_BROWSABLE);

                mediaItems.add(item);
            }

        } else if (MEDIA_BROWSER_CHILD.equals(parentMediaId)) {
            // サブメニューを選択すると、楽曲を表示するようにします。
            List<MediaSession.QueueItem> playQueue = new ArrayList<>();

            ContentResolver resolver = getContentResolver();

            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    COLUMNS,
                    null, // 検索条件。実際には、parentMediaIdによって分ける。
                    null, // 検索条件に入れる、クエリパラメータ
                    null); // ソート順。

            int index_of_id    = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int index_of_title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int index_of_data  = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            try {
                while(cursor.moveToNext()) {
                    Bundle extras = new Bundle();
                    extras.putString(MyMediaSessionCallback.EXTRAS_MEDIA_PATH,
                            cursor.getString(index_of_data));

                    MediaDescription description
                            = (new MediaDescription.Builder())
                            .setTitle(cursor.getString(index_of_title))
                            .setSubtitle(null)
                            .setMediaId(cursor.getString(index_of_data))
                            .setDescription(null)
                            .setIconBitmap(null)
                            .setExtras(extras)
                            .build();

                    MediaItem item = new MediaItem(description,
                            // 再生用のアイテムであることを示します
                            MediaItem.FLAG_PLAYABLE);
                    mediaItems.add(item);

                    // キューにも追加する
                    MediaSession.QueueItem queueItem
                            = new MediaSession.QueueItem(description,
                            cursor.getLong(index_of_id));
                    playQueue.add(queueItem);
                }
            } finally {
            cursor.close();
        }

        mSessionCallback.setQueue(playQueue);
    }

    // sendResult()の前に、detach()を呼びます。
    result.detach();
    result.sendResult(mediaItems);

    }

}
