package maps.s354378_mappe3;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import maps.s354378_mappe3.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Marker m;
    LatLng latLng_global;
    List<Attraction> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myList = new ArrayList<Attraction>();

        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        latLng_global = new LatLng(Double.longBitsToDouble(sp.getLong("lat", 0)),
                Double.longBitsToDouble(sp.getLong("long", 0)));

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getJSON task = new getJSON();
        task.execute( new String[] {"http://data1500.cs.oslomet.no/~s354378/jsonout.php"});

    }

    @Override
    public void onMapClick(LatLng point) {
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

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.91, 10.76),4));

        System.out.println("this");
        for(Attraction a : myList){
            MarkerOptions myMarker = new MarkerOptions().position(a.pos).title(a.address);
            mMap.addMarker(myMarker);
        }
        m = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("placeholder").visible(false));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng){
        latLng_global = latLng;
        System.out.println(latLng_global);

        if(m!= null){
            m.remove();
        }
        GetGeo task1 = new GetGeo();
        task1.execute();

        Toast.makeText(MapsActivity.this, "Fetching address...", Toast.LENGTH_SHORT).show();

    }

    public class GetGeo extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void ... Voids) {
            String query = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng_global.latitude + "," + latLng_global.longitude + "&key=" + getResources().getString(R.string.key);
            String output = "";
            String s = "";
            System.out.println("Fetching address..." + "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng_global.latitude + "," + latLng_global.longitude + "&key=" + getResources().getString(R.string.key));
            try {
                URL urlen = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1500);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                JSONObject jsonObject = new JSONObject(output);
                conn.disconnect();
                return ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
            } catch (Exception e) {
                return "Failed to fetch address :(";
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            System.out.println("Created marker with address: "+res);
            m = mMap.addMarker(new MarkerOptions().position(latLng_global).title(res));

        }
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
        Bundle mybundle = new Bundle();
        mybundle.putString("title", marker.getTitle());
        mybundle.putParcelable("pos", marker.getPosition());
        if(m!=null) mybundle.putBoolean("new", marker.getId().equals(m.getId()));
        else mybundle.putBoolean("new", false);
        if(!marker.getId().equals(m.getId())){
            for (Attraction a : myList){
                if(a.address.equals(marker.getTitle())){
                    mybundle.putString("desc", a.description);
                    mybundle.putString("name", a.name);
                    mybundle.putInt("id", a.get_id());
                }
            }
        }
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.setArguments(mybundle);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());



        return false;
    }



    public class getJSON extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            try {
                URL urlen = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)
                        urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                System.out.println("Output from Server .... \n");
                while ((s = br.readLine()) != null) {
                    output = output + s;
                }
                conn.disconnect();
                try {
                    JSONArray mat = new JSONArray(output);
                    for (int i = 0; i < mat.length(); i++) {
                        Attraction myAttraction = new Attraction();
                        jsonObject = mat.getJSONObject(i);

                        myAttraction.set_id(Integer.parseInt(jsonObject.getString("id")));
                        myAttraction.setName(jsonObject.getString("name"));
                        myAttraction.setDescription(jsonObject.getString("description"));
                        myAttraction.setAddress(jsonObject.getString("address"));

                        String[] l = jsonObject.getString("latlng").split(",");
                        LatLng myLatLng = new LatLng(Double.parseDouble(l[0]), Double.parseDouble(l[1]));
                        myAttraction.setPos(myLatLng);;

                        myList.add(myAttraction);
                    }

                    return retur;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return retur;
            } catch (Exception e) {
                return "Noe gikk feil";
            }
        }

        @Override
        protected void onPostExecute (String ss){
            System.out.println("Printing");
        }
    }
}