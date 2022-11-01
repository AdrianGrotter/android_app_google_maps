package maps.s354378_mappe3;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;
import java.util.logging.SocketHandler;

import maps.s354378_mappe3.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    TextView textView;
    MarkerOptions myMarker;
    Marker m;
    LatLng latLng_global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        latLng_global = new LatLng(Double.longBitsToDouble(sp.getLong("lat", 0)),
                Double.longBitsToDouble(sp.getLong("long", 0)));

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        textView = (TextView)findViewById(R.id.textView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println("This was run. "+latLng_global);

    }

    @Override
    public void onMapClick(LatLng point) {
        String output = "Tapped on "+point+"!";
        textView.setText(output);
        Toast.makeText(this, "Tapped on "+point+"!", Toast.LENGTH_SHORT).show();
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

        // Add a marker in Sydney and move the camera
        if(latLng_global != null){
            myMarker = new MarkerOptions().position(latLng_global).title("Marker in Sydneyz");
        }else{
            LatLng sydney = new LatLng(-34, 151);
            myMarker = new MarkerOptions().position(sydney).title("Marker in Sydney");
            latLng_global = sydney;
        }

        m = mMap.addMarker(myMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_global));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng){
        String output = "Long-pressed location: "+latLng;
        latLng_global = latLng;

        Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> res = coder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(!res.isEmpty()) output += "\nCurrent address is: "+res.get(0).getAddressLine(0);
            else output+= "\nIngen res";
            textView.setText(output);
            m.remove();
            m  = mMap.addMarker(new MarkerOptions().position(latLng).title("Your new marker"));
        } catch (Exception e) {
            System.out.println(e.getCause());
        }

        //textView.setText(output);


        //Check if location is an actual address

        //Display button to save location as new attraction

        //Create new attraction (address, pos, description) string, LatLng, String

        //Send object to database
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_content).setTitle("MyTitle");
        builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activityCreateAttraction();

            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    private void activityCreateAttraction() {
        Intent myIntent = new Intent(this, CreateAttractionActivity.class);
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putLong("lat", Double.doubleToRawLongBits(latLng_global.latitude));
        e.putLong("long", Double.doubleToRawLongBits(latLng_global.longitude));
        e.apply();
        startActivity(myIntent);
    }
}