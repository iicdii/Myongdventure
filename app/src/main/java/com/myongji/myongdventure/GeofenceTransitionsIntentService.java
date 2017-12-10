package com.myongji.myongdventure;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.myongji.myongdventure.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitionsIS";

    private static final String CHANNEL_ID = "channel_01";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "지오펜스에 들어오거나 나갔습니다.");

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(triggeringGeofences);

            // 사용자가 지오펜스에 진입한 건물명을 local broadCast에 보낸다.

            Intent buildingIntent = new Intent("googlegeofence");
            buildingIntent.putExtra("building", geofenceTransitionDetails);
            String status = "";
            switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    status = "enter";
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    status = "exit";
                    break;
                default:
                    break;
            }
            buildingIntent.putExtra("status", status);
            LocalBroadcastManager.getInstance(this).sendBroadcast(buildingIntent);

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, "geofence_transition_invalid_type");
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(List<Geofence> triggeringGeofences) {
        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }

        // 트리거 된 지오펜스 중 첫번째 지오펜스를 반환
        return triggeringGeofencesIdsList.get(0);
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("geofence_transition_notification_text")
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "geofence_transition_entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "geofence_transition_exited";
            default:
                return "unknown_geofence_transition";
        }
    }
}