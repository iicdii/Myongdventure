package com.myongji.myongdventure.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.BuildConfig;
import com.myongji.myongdventure.Constants;
import com.myongji.myongdventure.DBHelper;
import com.myongji.myongdventure.GeofenceTransitionsIntentService;
import com.myongji.myongdventure.dialog.MenuDialog;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.dialog.QuestDialog;
import com.myongji.myongdventure.enums.Building;
import com.myongji.myongdventure.enums.QuestType;
import com.myongji.myongdventure.schema.Quest;
import com.myongji.myongdventure.schema.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, OnCompleteListener<Void>, GoogleMap.OnMarkerClickListener {
    // Internal List of Geofence objects. In a real app, these might be provided by an API based on
    // locations within the user's proximity.
    ArrayList<Geofence> mGeofenceList;

    private static final LatLng DEFAULT_LOCATION = new LatLng(37.2232318, 127.1880301);
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int DEFAULT_ZOOM = 10;
    private static final int UPDATE_INTERVAL_MS = 10000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 9000;

    private GoogleMap mMap;
    private GeofencingClient mGeofencingClient;
    private Marker currentMarker = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private PendingIntent mGeofencePendingIntent;
    private LocationCallback mLocationCallback;
    private User currentUser = null;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            // 유저정보 가져오기
            myRef.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    currentUser = dataSnapshot.getValue(User.class);

                    if (currentUser != null) {
                        String level = String.valueOf(currentUser.level);
                        String name = String.valueOf(currentUser.name);
                        String levelName = "Lv. " + level + " " + name;
                        String exp = String.valueOf(currentUser.exp);
                        String nextExp = String.valueOf((currentUser.level + 2) * currentUser.level * 5);
                        String fullExp = exp + " / " + nextExp;

                        TextView usernameTextView = findViewById(R.id.tv_username);
                        usernameTextView.setText(levelName);
                        TextView userexpTextView = findViewById(R.id.tv_userexp);
                        userexpTextView.setText(fullExp);

                        Uri imageUrl = firebaseUser.getPhotoUrl();
                        if (imageUrl != null) {
                            ImageView userImageView = findViewById(R.id.iv_profile);
                            Picasso.with(getApplicationContext()).load(imageUrl).into(userImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("myRef", "Failed to read value.", error.toException());
                }
            });
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치가 바뀔때마다 위치 정보 업데이트
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
            };
        };

        LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(this);
        GoogleReceiver googleReceiver = new GoogleReceiver(this);
        lbc.registerReceiver(googleReceiver, new IntentFilter("googlegeofence"));


    }

    static class GoogleReceiver extends BroadcastReceiver {
        MainActivity mActivity;

        public GoogleReceiver(Activity activity){
            mActivity = (MainActivity) activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Handle the intent here
            String actionName = intent.getAction();

            if (actionName == null)
                return;

            if (actionName.equals("googlegeofence")) {
                String building = intent.getStringExtra("building");
                String status = intent.getStringExtra("status");

                if (building.isEmpty() || status.isEmpty()) return;

                if (intent.getStringExtra("status").equals("enter")) {
                    if (mActivity.currentMarker == null) {
                        LatLng location = Constants.LANDMARKS.get(building);
                        Log.i("building", String.valueOf(building));
                        Log.i("LANDMARKS", String.valueOf("5공학관"));
                        if (location == null)
                            return;

                        Log.i("건물위치 x:", String.valueOf(location.latitude));
                        Log.i("건물위치 y:", String.valueOf(location.longitude));

                        Location buildingLocation = new Location(building);
                        buildingLocation.setLatitude(location.latitude);
                        buildingLocation.setLongitude(location.longitude);
                        mActivity.setBuildingMarker(buildingLocation, building);
                    }
                } else if (intent.getStringExtra("status").equals("exit")) {
                    if (mActivity.currentMarker != null) {
                        mActivity.currentMarker.remove();
                    }
                }


            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            startLocationUpdates();
            addGeofences();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private boolean checkPermissions() {
        String[] permissions =
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};

        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    result = false;
            }
        }

        return result;
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } else {
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    private void requestPermissions() {
        final String[] permissions =
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};

        final Activity thisActivity = this;

        if (ActivityCompat.shouldShowRequestPermissionRationale
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale
                        (this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(thisActivity, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void onComplete(@NonNull Task<Void> task) {
        Log.d("onComplete", "지오펜스를 add했습니다.");
        drawGeofence();
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {
        Log.d("그리기", "지오펜스를 그립니다.");

        if (geoFenceLimits != null)
            geoFenceLimits.remove();

        // 건물 별로 루프를 돌면서 원을 그린다.
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(entry.getValue())
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS);
            geoFenceLimits = mMap.addCircle( circleOptions );
        }
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            Log.i("지오펜스 추가", "지오펜스를 추가할 권한이 없어요.");
            showSnackbar(getString(R.string.insufficient_permissions));
        } else {
            Log.i("지오펜스 추가", "지오펜스를 추가합니다.");
            mGeofencingClient
                    .addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnCompleteListener(this);
        }
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent());
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("permission", "onRequestPermissionResult");

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i("cancel", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("granted", "Permission granted.");
                startLocationUpdates();
                addGeofences();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 퍼미션 요청 전에 지도의 초기위치를 명지대로 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
        // 매끄럽게 이동함
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            // Set the map's camera position to the current location of the device.
            if (mLastLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastLocation.getLatitude(),
                                mLastLocation.getLongitude()), DEFAULT_ZOOM));
            }
        } else {
            requestPermissions();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String building = marker.getTitle();
        QuestDialog questDialog = new QuestDialog(this, building);
        questDialog.show();

        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("위치 변경", "위치가 변경되었습니다.");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("onStatusChanged", "위치 상태가 변경되었습니다.");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("onProviderEnabled", "위치 추적을 시작합니다.");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("onProviderDisabled", "위치 추적을 종료합니다.");
    }

    // 해당 location에 마커 찍기
    public void setBuildingMarker(Location location, String title) {
        if (currentMarker != null) currentMarker.remove();

        if (location != null) {
            //현재위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.title(title);
            currentMarker = mMap.addMarker(markerOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }

    public void showDialog(View view) {
        MenuDialog menuDialog = new MenuDialog(this);
        menuDialog.show();
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}
