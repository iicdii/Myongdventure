package com.myongji.myongdventure;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by kimharim on 2017. 12. 10..
 */

public class Constants {
    private Constants() {
    }

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 30; // 300m

    /**
     * 명지대학교 건물들에 대한 위치 정보를 저장한다.
     */
    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<>();
    static {
        LANDMARKS.put("5공학관", new LatLng(37.2219845,127.1875795));
        LANDMARKS.put("1공학관", new LatLng(37.2224715,127.1871396));
        LANDMARKS.put("함박관", new LatLng(37.2211387,127.1885236));
    }
}