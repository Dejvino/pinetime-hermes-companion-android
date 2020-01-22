package hermes.companion;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import co.beeline.android.bluetooth.currenttimeservice.CurrentTimeService;

public class HermesApplication extends Application {

    private ServiceConnection notificationProcessorServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        CurrentTimeService.startServer(this);
        Intent notificationProcessorServiceIntent = new Intent(this, NotificationProcessorService.class);
        bindService(notificationProcessorServiceIntent, notificationProcessorServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        CurrentTimeService.stopServer();
        unbindService(notificationProcessorServiceConnection);
    }
}
