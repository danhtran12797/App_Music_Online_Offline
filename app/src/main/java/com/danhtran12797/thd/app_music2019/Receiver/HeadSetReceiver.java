package com.danhtran12797.thd.app_music2019.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HeadSetReceiver extends BroadcastReceiver {

    public static IHeadSet listenerHeadSet;

    public interface IHeadSet{
        void onHeadSetOffline(int state);
        void onHeadSetOnline(int state);
    }

    public HeadSetReceiver(Context context) {
        listenerHeadSet= (IHeadSet) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            Log.d("kkk", "onReceive: "+state);
            listenerHeadSet.onHeadSetOnline(state);
            listenerHeadSet.onHeadSetOffline(state);
        }
    }
}
