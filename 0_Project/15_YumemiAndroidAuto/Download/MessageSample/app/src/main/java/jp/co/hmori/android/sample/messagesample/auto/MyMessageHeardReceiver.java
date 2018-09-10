package jp.co.hmori.android.sample.messagesample.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

public class MyMessageHeardReceiver extends BroadcastReceiver {
    public MyMessageHeardReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int conversationId = NotificationHelper.getConversationId(intent);

        // 通知は読まれたので、既読として通知を削除します。
        // メールなどの場合、ここで既読フラグもつけましょう。
        NotificationManagerCompat manager
                = NotificationManagerCompat.from(context.getApplicationContext());

        manager.cancel(conversationId);
    }
}
