package com.alexy.mapsforgeapisample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.alexy.mapsforgeapilibrary.MapsforgeAPI;
import com.alexy.mapsforgeapilibrary.Marker;

import org.oscim.layers.PathLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 5555;

    private MapsforgeAPI mapsforgeAPI;
    private CheckBox checkBox;

    private List<Pair<Double, Double>> markerCoords = new ArrayList<>();

    private int numMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void showMap() {
        mapsforgeAPI = (MapsforgeAPI)findViewById(R.id.mapView);
        checkBox = (CheckBox)findViewById(R.id.animCheckBox);

        findViewById(R.id.buttonMinus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsforgeAPI.zoomOut(isAnimated());
            }
        });

        findViewById(R.id.buttonPlus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsforgeAPI.zoomIn(isAnimated());
            }
        });

        findViewById(R.id.buttonZoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsforgeAPI.zoomTo(12, isAnimated());
            }
        });

        findViewById(R.id.buttonLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsforgeAPI.rotate(-Math.PI/4, 0,0, isAnimated());
            }
        });

        findViewById(R.id.buttonRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsforgeAPI.rotate(Math.PI/4, 0,0, isAnimated());
            }
        });

        findViewById(R.id.buttonLine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsforgeAPI.addLine(markerCoords);
            }
        });

        mapsforgeAPI.loadMap(Environment.getExternalStorageDirectory().getAbsolutePath() + "/massachusetts.map");
        mapsforgeAPI.moveTo(42.358056, -71.063611);
        mapsforgeAPI.zoomTo(10, false);

        mapsforgeAPI.setOnTapListener(new MapsforgeAPI.OnTapListener() {
            @Override
            public boolean onTap(double lat, double lon) {
                if (isAnimated()) {
                    mapsforgeAPI.animateTo(lat, lon);
                } else {
                    mapsforgeAPI.moveTo(lat, lon);
                }
                return true;
            }

            @Override
            public boolean onLongPress(double lat, double lon) {
                Marker marker = new Marker(UUID.randomUUID(), "Marker"+numMarkers, "Marker number " + numMarkers, lat, lon);
                mapsforgeAPI.addMarker(marker);
                markerCoords.add(new Pair<Double, Double>(lat, lon));
                numMarkers++;
                return true;
            }
        });

        mapsforgeAPI.setOnMarkerClickListener(new MapsforgeAPI.OnMarkerClickListener() {
            @Override
            public void onClick(Marker marker) {
                Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongPress(Marker marker) {
                mapsforgeAPI.removeMarker(marker);
            }
        });
    }



    private boolean isAnimated() {
        return checkBox.isChecked();
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showMap();
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapsforgeAPI.destroy();
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showMap();
                } else {
                    finish();
                }
            }
        }
    }
}
