package com.example.test_complete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("zxl--->CustomComplete");
        Toast.makeText(context, "开机自启动", Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

        NotificationUtils notificationUtils = new NotificationUtils(context.getApplicationContext());
        String content = "fullscreen intent test";
        notificationUtils.sendNotificationFullScreen(context.getApplicationContext().getString(R.string.app_name), content, "type");
    }
}
