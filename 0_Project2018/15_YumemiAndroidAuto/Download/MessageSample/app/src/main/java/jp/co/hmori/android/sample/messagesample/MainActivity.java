package jp.co.hmori.android.sample.messagesample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import jp.co.hmori.android.sample.messagesample.auto.NotificationHelper;


public class MainActivity extends Activity implements View.OnClickListener{

    private EditText mNameTextBox;
    private EditText mMessageTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameTextBox    = (EditText)findViewById(R.id.name);
        mMessageTextBox = (EditText)findViewById(R.id.message);

        findViewById(R.id.button_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        NotificationHelper.notifyMessage(this, mNameTextBox.getText().toString(),
                mMessageTextBox.getText().toString());

    }
}
