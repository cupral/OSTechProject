package jp.co.hmori.android.sample.mediaplayer.auto;

import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;

/**
 * Created by hmori on 2015/04/16.
 */
public class MyMediaSessionCallback extends MediaSession.Callback
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    public static final String EXTRAS_MEDIA_PATH = "EXTRAS_MEDIA_PATH";

    private MediaSession mSession;
    private MediaPlayer  mMediaPlayer;
    private int mPlayQueueIndex;

    MyMediaSessionCallback(MediaSession session) {
        mSession = session;
    }

    /**
     * 指定されたキューアイテムの音楽を再生します
     */
    private void play(MediaSession.QueueItem queueItem) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        } else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setDataSource(
                    queueItem.getDescription().getExtras().getString(EXTRAS_MEDIA_PATH));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一時停止を解除します
     */
    private void resume() {
        mMediaPlayer.start();
        setPlaybackState(PlaybackState.STATE_PLAYING,
                mMediaPlayer.getCurrentPosition(), 1.0f);
    }

    private void playFromQueue() {
        MediaSession.QueueItem queueItem
                = mSession.getController().getQueue().get(mPlayQueueIndex);
        play(queueItem);
    }

    /**
     * ユーザーが特定の楽曲を指定した場合に呼ばれます。
     */
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        int index = -1;
        final List<MediaSession.QueueItem> queue = mSession.getController().getQueue();
        final int size = queue.size();
        MediaSession.QueueItem queueItem = null;

        for(int i = 0; i < size; i++) {
            MediaSession.QueueItem item = queue.get(i);

            if (mediaId.equals(item.getDescription().getMediaId())) {
                index = i;
                queueItem = item;
                break;
            }
        }

        if (queueItem != null) {
            play(queueItem);
            mPlayQueueIndex = index;
        }
    }

    /**
     * 楽曲を再生する場合に呼ばれます
     * 一時停止からの復帰時にも呼ばれます。
     */
    public void onPlay() {
        PlaybackState state = mSession.getController().getPlaybackState();

        if (state.getState() == PlaybackState.STATE_PAUSED) {
            resume();
        } else {
            playFromQueue();
        }
    }

    /**
     * 楽曲が一時停止された場合に呼ばれます
     */
    public void onPause() {
        long position = 0L;

        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            position = mMediaPlayer.getCurrentPosition();
        }
        setPlaybackState(PlaybackState.STATE_PAUSED, position, 0f);
    }

    /**
     * 楽曲が停止された場合に呼ばれます
     */
    public void onStop() {
        if (mMediaPlayer != null) mMediaPlayer.reset();

        setPlaybackState(PlaybackState.STATE_STOPPED, 0L, 0f);
    }

    /**
     * 次の曲に進むボタンが押された場合に呼ばれます
     */
    @Override
    public void onSkipToNext() {
        mPlayQueueIndex++;
        if (mPlayQueueIndex >= getQueueSize()) {
            mPlayQueueIndex = 0;
        }
        playFromQueue();
    }

    /**
     * 前の曲に戻るボタンが押された場合に呼ばれます
     */
    @Override
    public void onSkipToPrevious() {
        mPlayQueueIndex--;
        if (mPlayQueueIndex < 0) {
            mPlayQueueIndex = 0;
        }
        playFromQueue();
    }

    /**
     * 楽曲を演奏し終えた際に呼ばれます。
     * @param mp MediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 今の楽曲の再生が終わりました。
        if (mPlayQueueIndex < getQueueSize() -1) {
            // キューが残っている場合は、次の楽曲を再生します
            mPlayQueueIndex++;
            playFromQueue();
        } else {
            // キューが残っていない場合は、停止します
            mPlayQueueIndex = 0;
            setPlaybackState(PlaybackState.STATE_STOPPED, 0L, 0f);
        }
    }

    /**
     * 楽曲を再生する準備が整った時に呼ばれます。
     * @param mp MediaPlayer
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // 再生の準備が整ったので、実際に再生します。
        mp.start();
        setPlaybackState(PlaybackState.STATE_PLAYING, 0L, 1.0f);
    }

    /**
     * 取りうるアクションを取得します。
     * 以下の定数からビットマスクで指定します。
     * <ul>
     * <li> {@link PlaybackState#ACTION_SKIP_TO_PREVIOUS}</li>
     * <li> {@link PlaybackState#ACTION_REWIND}</li>
     * <li> {@link PlaybackState#ACTION_PLAY}</li>
     * <li> {@link PlaybackState#ACTION_PAUSE}</li>
     * <li> {@link PlaybackState#ACTION_STOP}</li>
     * <li> {@link PlaybackState#ACTION_FAST_FORWARD}</li>
     * <li> {@link PlaybackState#ACTION_SKIP_TO_NEXT}</li>
     * <li> {@link PlaybackState#ACTION_SEEK_TO}</li>
     * <li> {@link PlaybackState#ACTION_SET_RATING}</li>
     * </ul>
     */
    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE;

        if (getQueueSize() > 0) {
            actions |= PlaybackState.ACTION_SKIP_TO_PREVIOUS
                    | PlaybackState.ACTION_SKIP_TO_NEXT;
        }

        return actions;
    }

    /**
     * 再生・停止など、いまどういう状態にあるかは、自分で管理する必要があります。
     * @param state　状態を示す定数を指定して下さい。
     * <ul>
     * <li> {@link PlaybackState#STATE_NONE}</li>
     * <li> {@link PlaybackState#STATE_STOPPED}</li>
     * <li> {@link PlaybackState#STATE_PLAYING}</li>
     * <li> {@link PlaybackState#STATE_PAUSED}</li>
     * <li> {@link PlaybackState#STATE_FAST_FORWARDING}</li>
     * <li> {@link PlaybackState#STATE_REWINDING}</li>
     * <li> {@link PlaybackState#STATE_BUFFERING}</li>
     * <li> {@link PlaybackState#STATE_ERROR}</li>
     * </ul>
     * @param position 現在再生中の音楽の再生位置です。ms単位で指定します。
     * @param speed 再生スピードです。通常は1.0fです。0.0fは再生停止中であることを意味します。
     */
    private void setPlaybackState(int state, long position, float speed) {
        if (mSession == null) return;

        PlaybackState playbackState = (new PlaybackState.Builder())
                .setState(state, position, speed)  // 状態、再生位置、再生速度を指定します
                .setActions(getAvailableActions()) // 今の状態で、取りうるアクションを指定します。
                .build();

        mSession.setPlaybackState(playbackState);
    }

    private int getQueueSize() {
        List<MediaSession.QueueItem> queue = mSession.getController().getQueue();

        if (queue == null) return 0;
        else return queue.size();
    }

    public void setQueue(List<MediaSession.QueueItem> queue) {
        mPlayQueueIndex = 0;
        mSession.setQueue(queue);
    }

    public void release() {
        // セッションは必ず解放して下さい。
        if (mSession != null) {
            mSession.setCallback(null);
            mSession.release();
        }

        // メディアプレイヤーも、解放して下さい。
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
