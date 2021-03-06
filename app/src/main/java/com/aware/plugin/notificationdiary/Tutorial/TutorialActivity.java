package com.aware.plugin.notificationdiary.Tutorial;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.plugin.notificationdiary.AppManagement;
import com.aware.plugin.notificationdiary.MainTabs;
import com.aware.plugin.notificationdiary.R;
import com.aware.ui.PermissionsHandler;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private static final String TAG = "TutorialActivity";

    ScrollView parent;
    Button next;
    Button skip;

    private static ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();

    int page;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        if (!researchConditionsAccepted(this)) page = 0;
        else page = 1;

        setContentView(R.layout.activity_tutorial);
        parent = (ScrollView) findViewById(R.id.tutorial_parent);
        next = (Button) findViewById(R.id.tutorial_next_button);
        skip = (Button) findViewById(R.id.tutorial_skip_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                AppManagement.setTutorialPage(context, page);
                content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_out_left));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshView(context);
                    }
                }, 500);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManagement.setTutorialPage(context, 1);
                AppManagement.setFirstLaunch(context);
                Intent intent = new Intent(context, MainTabs.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_WIFI_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_NETWORK_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.VIBRATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        page = AppManagement.getTutorialPage(this);
        refreshView(this);
    }

    LinearLayout content;

    Button permissions;
    Button accessibility_access;
    Button notification_access;
    Button accept_conditions;
    Button battery_optimisation;
    boolean permissions_ok;
    boolean accessibility_ok;
    boolean notification_ok;
    private void refreshView(final Context c) {
        try {
            parent.removeAllViews();
            switch (page) {
                case 0:
                    skip.setEnabled(researchConditionsAccepted(c) && setupComplete(c));
                    next.setEnabled(false);
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page0, null);
                    accept_conditions = (Button) content.findViewById(R.id.accept_conditions);
                    accept_conditions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppManagement.acceptConditions(context);
                            accept_conditions.setEnabled(false);
                            accept_conditions.invalidate();
                            next.setEnabled(true);
                        }
                    });
                    parent.addView(content);
                    checkConditions();
                    break;
                case 1:
                    skip.setEnabled(researchConditionsAccepted(c) && setupComplete(c));
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page1, null);
                    parent.addView(content);
                    checkConditions();
                    next.setEnabled(true);
                    break;
                case 2:
                    skip.setEnabled(researchConditionsAccepted(c) && setupComplete(c));
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page2, null);
                    parent.addView(content);

                    permissions = (Button) content.findViewById(R.id.tutorial_permissions);
                    accessibility_access = (Button) content.findViewById(R.id.tutorial_accessibility);
                    notification_access = (Button) content.findViewById(R.id.tutorial_notification_access);
                    battery_optimisation = (Button) content.findViewById(R.id.tutorial_battery_saver);
                    permissions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionsClick(c);
                        }
                    });
                    accessibility_access.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            accessibilityClick(c);
                        }
                    });
                    notification_access.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            notificationClick(c);
                        }
                    });
                    if (android.os.Build.VERSION.SDK_INT >= 23)
                        battery_optimisation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent powerUsageIntent = new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                startActivity(powerUsageIntent);
                            }
                        });
                    else {
                        battery_optimisation.setEnabled(false);
                        battery_optimisation.setText("Incorrect android version for battery optimisation");
                    }
                    permissions_ok = checkPermissions(c);
                    accessibility_ok = checkAccessibility(c);
                    notification_ok = checkNotification(c);
                    permissions.setEnabled(!permissions_ok);
                    accessibility_access.setEnabled(!accessibility_ok);
                    notification_access.setEnabled(!notification_ok);
                    next.setEnabled(permissions_ok && accessibility_ok && notification_ok);
                    checkConditions();
                    break;
                case 3:
                    skip.setEnabled(true);
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page3, null);
                    parent.addView(content);
                    checkConditions();
                    next.setEnabled(true);
                    break;
                case 4:
                    skip.setEnabled(true);
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page4, null);
                    parent.addView(content);
                    checkConditions();
                    next.setEnabled(true);
                    break;
                case 5:
                    skip.setEnabled(true);
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page5, null);
                    parent.addView(content);
                    checkConditions();
                    next.setEnabled(true);
                    break;
                case 6:
                    skip.setEnabled(true);
                    content = (LinearLayout) getLayoutInflater().inflate(R.layout.tutorial_page6, null);
                    parent.addView(content);
                    checkConditions();
                    next.setEnabled(true);
                    break;
                case 7:
                    finish();
                    return;
            }
            parent.invalidate();
            parent.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
        }
        catch (Exception e) {
            Toast.makeText(this, "Something went wrong with tutorial - try again please", Toast.LENGTH_LONG);
            finish();
            return;
        }
    }

    private void checkConditions() {
        if (page < 6) {
            next.setText("Next");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page++;
                    AppManagement.setTutorialPage(context, page);
                    content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_out_left));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshView(context);
                        }
                    }, 500);
                }
            });
        }
        else {
            next.setText("Finish");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppManagement.setTutorialPage(context, 1);
                    AppManagement.setFirstLaunch(context);
                    Intent intent = new Intent(context, MainTabs.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            });
        }
    }

    private boolean researchConditionsAccepted(Context c) {
        return Aware.isStudy(c);
    }

    static boolean permissionsOk;
    static boolean notificationOk;
    static boolean accessibilityOk;
    private boolean setupComplete(Context c) {
        return permissionsOk && notificationOk && accessibilityOk;
    }

    private boolean checkPermissions(Context c) {
        boolean allPermissionsOk = true;
        PackageManager pm = getPackageManager();
        for (String perm : REQUIRED_PERMISSIONS) {
            int hasPerm = pm.checkPermission(
                    perm,
                    getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                allPermissionsOk = false;
                break;
            }
        }
        permissionsOk = allPermissionsOk;
        return allPermissionsOk;
    }

    private void permissionsClick(Context c) {
        PackageManager pm = getPackageManager();
        for (String perm : REQUIRED_PERMISSIONS) {
            int hasPerm = pm.checkPermission(
                    perm,
                    getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                Intent permissions = new Intent(this, PermissionsHandler.class);
                permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
                permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getClass().getName());
                startActivity(permissions);
                break;
            }
        }
    }

    private boolean checkAccessibility(Context c) {
        return isAccessibilityEnabled(c);
    }

    private void accessibilityClick(Context c) {
        Intent accessibilitySettings = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        accessibilitySettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accessibilitySettings);
    }

    private synchronized static boolean isAccessibilityEnabled(Context c) {
        boolean enabled = false;

        AccessibilityManager accessibilityManager = (AccessibilityManager) c.getSystemService(ACCESSIBILITY_SERVICE);

        //Try to fetch active accessibility services directly from Android OS database instead of broken API...
        String settingValue = android.provider.Settings.Secure.getString(c.getContentResolver(), android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (settingValue != null) {
            if (settingValue.contains(c.getPackageName())) {
                enabled = true;
            }
        }
        if (!enabled) {
            try {
                List<AccessibilityServiceInfo> enabledServices = AccessibilityManagerCompat.getEnabledAccessibilityServiceList(accessibilityManager, AccessibilityEventCompat.TYPES_ALL_MASK);
                if (!enabledServices.isEmpty()) {
                    for (AccessibilityServiceInfo service : enabledServices) {
                        if (service.getId().contains(c.getPackageName())) {
                            enabled = true;
                            break;
                        }
                    }
                }
            } catch (NoSuchMethodError e) {
            }
        }
        if (!enabled) {
            try {
                List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
                if (!enabledServices.isEmpty()) {
                    for (AccessibilityServiceInfo service : enabledServices) {
                        if (service.getId().contains(c.getPackageName())) {
                            enabled = true;
                            break;
                        }
                    }
                }
            } catch (NoSuchMethodError e) {
            }
        }

        //Keep the global setting up-to-date
        Aware.setSetting(c, Applications.STATUS_AWARE_ACCESSIBILITY, enabled, "com.aware.phone");
        accessibilityOk = enabled;
        return enabled;
    }

    private boolean checkNotification(Context c) {
        String notificationListenerString = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
        //Check notifications access permission
        notificationOk = !(notificationListenerString == null || !notificationListenerString.contains(getPackageName()));
        return notificationOk;
    }

    private void notificationClick(Context c) {
        Intent requestIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(requestIntent);
    }
}
