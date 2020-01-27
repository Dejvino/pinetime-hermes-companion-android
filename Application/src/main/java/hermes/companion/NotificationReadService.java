package hermes.companion;

import android.app.Notification;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationReadService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();

    public static final String NOTIFICATION_POSTED_EVENT =
            BuildConfig.APPLICATION_ID + ".NOTIFICATION_POSTED_EVENT";
    public static final String NOTIFICATION_REMOVED_EVENT =
            BuildConfig.APPLICATION_ID + ".NOTIFICATION_REMOVED_EVENT";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Notification read service started.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Notification read service destroyed.");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        Intent i = new  Intent(NOTIFICATION_POSTED_EVENT);
        i.putExtra("text", String.valueOf(notif.extras.get(Notification.EXTRA_TEXT)));
        i.putExtra("title", String.valueOf(notif.extras.get(Notification.EXTRA_TITLE)));
        i.putExtra("ticker", sbn.getNotification().tickerText);

        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNotificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        Intent i = new  Intent(NOTIFICATION_REMOVED_EVENT);
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
