package com.aware.plugin.notificationdiary;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.aware.Aware;
import com.aware.plugin.notificationdiary.Providers.Provider;
import com.aware.utils.Aware_Plugin;

import static com.aware.plugin.notificationdiary.AppManagement.isMyServiceRunning;

public class Plugin extends Aware_Plugin {

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::"+getResources().getString(R.string.app_name);

        //Any active plugin/sensor shares its overall context using broadcasts
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Add permissions you need (Support for Android M). By default, AWARE asks access to the #Manifest.permission.WRITE_EXTERNAL_STORAGE
        //REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        //To sync data to the server, you'll need to set this variables from your ContentProvider
        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{
                Provider.Notifications_Data.CONTENT_URI,
                Provider.Predictions_Data.CONTENT_URI
        }; //this syncs dummy Notifications_Table to server

        //Activate plugin -- do this ALWAYS as the last thing (this will restart your own plugin and apply the settings)
        //Aware.startPlugin(this, "com.aware.plugin.notificationdiary");
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Plugin onstart");
        Aware.startAWARE(this);

        Aware.setSetting(this, Settings.STATUS_PLUGIN_NOTIFICATIONDIARY, true);
        if (AppManagement.getSoundControlAllowed(this) && !isMyServiceRunning(NotificationAlarmManager.class, this)) startService(new Intent(this, NotificationAlarmManager.class));
        if (!isMyServiceRunning(NotificationListener.class, this)) startService(new Intent(this, NotificationListener.class));

        startService(new Intent(this, ProviderSyncService.class));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aware.setSetting(this, Settings.STATUS_PLUGIN_NOTIFICATIONDIARY, false);
        Log.d(TAG, "onDestroy");
        //Stop AWARE
        Aware.stopAWARE(this);
    }

}