package com.toggle.katana2d;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class GameActivity  extends Activity {
    protected Engine mEngine = new Engine();
    public Handler handler;

    public static class HandlerClass extends Handler {
        private Context mContext;
        private Dialog dialog;
        public HandlerClass(Context context) {
            super();
            mContext = context;
        }
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.basic_dialog);
                dialog.show();
            } else if (msg.what == 2) {
                if (dialog != null)
                    dialog.dismiss();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine.init(this);

        init();
        handler = new HandlerClass(this);
    }

    protected void init() {
        start();
    }

    protected void start() {
        mEngine.start();
    }

    public void onGamePreStart() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mEngine.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mEngine.onResume();
    }

    public void onGameStart() {

    }
}
