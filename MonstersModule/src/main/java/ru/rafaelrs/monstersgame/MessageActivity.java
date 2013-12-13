package ru.rafaelrs.monstersgame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by rafaelrs on 07.12.13.
 */
public class MessageActivity extends Activity {

    public static final String KEY_TITLE = "ru.rafaers.monstersgame.msg_title";
    public static final String KEY_MESSAGE = "ru.rafaers.monstersgame.msg_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView activityContent = (TextView)findViewById(R.id.text_description);
        activityContent.setText(getIntent().getStringExtra(KEY_MESSAGE));
        setTitle(getIntent().getStringExtra(KEY_TITLE));
    }
}
