package jp.co.hmori.android.sample.messagesample.auto;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import jp.co.hmori.android.sample.messagesample.R;

public class NotificationHelper {
    public static final int HEAR_ID  = 100;
    public static final int REPLY_ID = 101;

    public static final String EXTRA_KEY_ID = "conversation_id";
    public static final String MY_VOICE_REPLY_KEY = "reply_key";

    private static final String ACTION_MESSAGE_HEARD = "jp.co.hmori.android.sample.messagesample.MY_ACTION_MESSAGE_HEARD";
    private static final String ACTION_MESSAGE_REPLY = "jp.co.hmori.android.sample.messagesample.MY_ACTION_MESSAGE_REPLY";

    public static void notifyMessage(Context context, String name, String message) {
        // 既読用インテント
        Intent msgHeardIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(ACTION_MESSAGE_HEARD)
                .putExtra(EXTRA_KEY_ID, HEAR_ID);

        PendingIntent msgHeardPendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        // ここでは固定で入れていますが、どの会話か同定できるようにしましょう。
                        HEAR_ID,
                        msgHeardIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // 返信用インテント
        Intent replyIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(ACTION_MESSAGE_REPLY)
                .putExtra(EXTRA_KEY_ID, REPLY_ID);

        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                // これも固定で入れていますが、どの会話か同定できるようにしましょう。
                REPLY_ID,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 入力を受け付けるためのクラス
        RemoteInput remoteInput = new RemoteInput.Builder(MY_VOICE_REPLY_KEY)
                .setLabel(context.getString(R.string.app_name))
                .build();

        // 一連の会話をグループ化するためのクラス
        UnreadConversation.Builder unreadConvBuilder =
                new UnreadConversation.Builder(name)
                        // 既読用のPendingIntent
                        .setReadPendingIntent(msgHeardPendingIntent)
                        // 返信用のPendingIntent
                        .setReplyAction(replyPendingIntent, remoteInput)
                        // 通知内容
                        .addMessage(message)
                        // タイムスタンプをつけましょう
                        .setLatestTimestamp(System.currentTimeMillis());

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context.getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .extend(new NotificationCompat.CarExtender()
                                .setUnreadConversation(unreadConvBuilder.build()));

        NotificationManagerCompat manager
                = NotificationManagerCompat.from(context.getApplicationContext());
        manager.notify(HEAR_ID, notificationBuilder.build());
    }

    /**
     * 返信受け取り用のBroadcastReceiverが受け取ったIntentから、返信内容を取り出す
     */
    public static CharSequence getMessageText(Intent intent) {
        Bundle remoteInput =
                RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(MY_VOICE_REPLY_KEY);
        }
        return null;
    }

    public static int getConversationId(Intent intent) {
        return intent.getIntExtra(NotificationHelper.EXTRA_KEY_ID, -1);
    }


}
