package com.danhtran12797.thd.app_music_test.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonIntentReceiver extends BroadcastReceiver {

    public static MediaButtonListener listener;

    public interface MediaButtonListener {
        void onMediaButtonSent();
    }

    public MediaButtonIntentReceiver(Context context) {
        listener = (MediaButtonListener) context;
    }

    // Constructor is mandatory
    public MediaButtonIntentReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {

            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event == null) {
                return;
            }

            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {
                listener.onMediaButtonSent();
            }
        }
    }
}
