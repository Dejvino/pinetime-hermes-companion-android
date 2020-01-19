package hermes.companion;

import android.app.Application;

import co.beeline.android.bluetooth.currenttimeservice.CurrentTimeService;

public class HermesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CurrentTimeService.startServer(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        CurrentTimeService.stopServer();
    }
}
