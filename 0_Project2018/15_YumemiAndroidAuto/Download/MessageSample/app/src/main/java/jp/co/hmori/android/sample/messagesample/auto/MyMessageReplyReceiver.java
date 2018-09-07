package jp.co.hmori.android.sample.messagesample.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyMessageReplyReceiver extends BroadcastReceiver {
    public MyMessageReplyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 必要なら、会話の管理に使用します。
        int conversationId = NotificationHelper.getConversationId(intent);

        // ユーザーが音声入力したテキストを受けとる
        CharSequence reply = NotificationHelper.getMessageText(intent);
        if (reply != null) {
            Log.d(getClass().getSimpleName(), reply.toString());
        }
    }

}