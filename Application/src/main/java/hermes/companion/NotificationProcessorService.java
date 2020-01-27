package hermes.companion;

import android.app.IntentService;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static hermes.companion.NotificationReadService.NOTIFICATION_POSTED_EVENT;

public class NotificationProcessorService extends IntentService {

    private String TAG = this.getClass().getSimpleName();
    private BluetoothLeService bluetoothLeService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ticker = intent.getStringExtra("ticker");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String log = "Received broadcast.\n" +
                    "Ticker: " + ticker + "\n" +
                    "Title: " + title + "\n" +
                    "Text: " + text + "\n";
            Log.d(TAG, log);

            List<String> msgParts = new ArrayList<>(3);
            if (ticker != null) {
                msgParts.add(ticker);
            }
            if (title != null && !title.equals(ticker)) {
                msgParts.add(title);
            }
            if (text != null && !text.equals(ticker)) {
                msgParts.add(text);
            }
            String msg = String.join("\n", msgParts);

            if (msg.length() >= 64) {
                msg = msg.substring(0, 58) + "[...]";
            }
            sendNotificationToBluetooth(msg);
        }
    };

    public NotificationProcessorService() {
        this("notification-processor-service");
    }

    public NotificationProcessorService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(NOTIFICATION_POSTED_EVENT));

        Intent bluetoothIntent = new Intent(this, BluetoothLeService.class);
        bindService(bluetoothIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // not used, just to be sure...
        Log.e(TAG, "Notification intent received and ignored.");
    }

    private void sendNotificationToBluetooth(String text)
    {
        String cleansedText = ("" + text).replaceAll("[^\\x09-\\x7E]", "#").concat("\n");
        bluetoothLeService.sendNotification(cleansedText);
    }
}
