package com.example.gpsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.overlay.Marker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION_ID = 44;

    private TextView bestprovider;
    private TextView longitude;
    private TextView latitude;
    private TextView archivaldata;
    private TextView text_network;
    private TextView text_gps;

    private LocationManager locationManager;
    private Criteria criteria;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Location location;
    private MapView osm;
    private MapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        osm = findViewById(R.id.osm);
        osm.setTileSource(TileSourceFactory.MAPNIK);
        osm.setMultiTouchControls(true);

        mapController = (MapController) osm.getController();
        mapController.setZoom(16.0);

        bestprovider = findViewById(R.id.bestprovider);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        archivaldata = findViewById(R.id.archival_data);
        text_network = findViewById(R.id.text_network);
        text_gps = findViewById(R.id.text_gps);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            text_network.setText("internet connect");
            text_network.setTextColor(Color.GREEN);
        });

        swipeRefreshLayout.setColorSchemeColors(Color.YELLOW);

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            startGPS();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ID);
    }

    private void startGPS() {
        String provider = locationManager.getBestProvider(criteria, true);
        bestprovider.setText("Best provider: " + provider);

        if (!checkPermissions()) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 1000, 0, this);
        Location last = locationManager.getLastKnownLocation(provider);
        if (last != null) updateLocation(last);
    }

    private void updateLocation(Location loc) {
        this.location = loc;

        longitude.setText("Longitude: " + loc.getLongitude());
        latitude.setText("Latitude: " + loc.getLatitude());
        text_gps.setText("GPS: OK");
        text_gps.setTextColor(Color.GREEN);

        GeoPoint point = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        addMarker(point);
        mapController.setCenter(point);
    }

    private void addMarker(GeoPoint point) {
        Marker marker = new Marker(osm);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Twoja pozycja");
        marker.setIcon(getDrawable(R.drawable.baseline_add_location_alt_24));
        osm.getOverlays().clear();
        osm.getOverlays().add(marker);
        osm.invalidate();
    }

    @Override
    public void onLocationChanged(@NonNull Location loc) {
        updateLocation(loc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sms) sendSmsWithCoords();
        else if (id == R.id.menu_save) saveMapSnapshot();
        else if (id == R.id.menu_share) shareResults();
        else if (id == R.id.menu_weather) openWeatherAppOrWebsite();

        return true;
    }

    private void openWeatherAppOrWebsite() {
        if (location == null) {
            Toast.makeText(this, "Brak danych GPS", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        startActivity(intent);
    }

    private void sendSmsWithCoords() {
        if (location == null) {
            Toast.makeText(this, "Brak danych GPS", Toast.LENGTH_SHORT).show();
            return;
        }
        String msg = "Lat: " + location.getLatitude() + " Lon: " + location.getLongitude();
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", msg);
        startActivity(intent);
    }

    private void saveMapSnapshot() {
        osm.post(() -> {
            Bitmap bitmap = Bitmap.createBitmap(osm.getWidth(), osm.getHeight(), Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            osm.draw(canvas);

            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GPSApp");
            if (!path.exists()) path.mkdirs();

            String filename = "map_" + System.currentTimeMillis() + ".png";
            File file = new File(path, filename);

            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(file));
                sendBroadcast(mediaScanIntent);

                Toast.makeText(this, "Zapisano: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Błąd podczas zapisu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareResults() {
        if (location == null) {
            Toast.makeText(this, "Brak lokalizacji", Toast.LENGTH_SHORT).show();
            return;
        }
        String msg = "Moje koordynaty:\n" + location.getLatitude() + ", " + location.getLongitude();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, "Udostępnij"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        if (requestCode == PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startGPS();
        } else {
            Toast.makeText(this, "Brak uprawnień!", Toast.LENGTH_SHORT).show();
        }
    }
}