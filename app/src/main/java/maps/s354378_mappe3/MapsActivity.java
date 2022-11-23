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
import android.os.AsyncTask;
import android.os.Bundle;
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

import maps.s354378_mappe3.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    TextView textView;
    MarkerOptions myMarker;
    Marker m;
    LatLng latLng_global;
    List<Attraction> myList;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myList = new ArrayList<Attraction>();

        Attraction myAttraction = new Attraction();
        myAttraction.setName("Soldier of the golden tides");
        myAttraction.setDescription("Brass statue showing a soldier in combat");
        myAttraction.setAddress("Statueveien 15C");
        myAttraction.setPos(new LatLng(5,5));
        myList.add(myAttraction);

        Attraction myAttraction2 = new Attraction();
        myAttraction2.setName("Noble steed");
        myAttraction2.setDescription("Majestic steed carrying its master into battle");
        myAttraction2.setAddress("Ole Henders alle 11");
        myAttraction2.setPos(new LatLng(-20,110));
        myList.add(myAttraction2);

        Attraction myAttraction3 = new Attraction();
        myAttraction3.setName("Coral Reef");
        myAttraction3.setDescription("Artwork on display");
        myAttraction3.setAddress("Tollbugaten 2");
        myAttraction3.setPos(new LatLng(-50,110));
        myList.add(myAttraction3);

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

        getJSON task = new getJSON();
        task.execute( new String[] {"http://data1500.cs.oslomet.no/~s354378/jsonout.php"});

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
            myMarker = new MarkerOptions().position(latLng_global).title("Recreated marker");
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

        for(Attraction a : myList){
            myMarker = new MarkerOptions().position(a.pos).title(a.name);
            mMap.addMarker(myMarker);
        }

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng){
        latLng_global = latLng;
        System.out.println(latLng_global);
        m.remove();
        m  = mMap.addMarker(new MarkerOptions().position(latLng_global).title("New marker"));
        GetGeo task1 = new GetGeo();
        task1.execute();




        //textView.setText(output);

        //Check if location is an actual address

        //Display button to save location as new attraction

        //Create new attraction (address, pos, description) string, LatLng, String

        //Send object to database
    }

    public class GetGeo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String query = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng_global.latitude + "," + latLng_global.longitude + "&key=" + getResources().getString(R.string.key);
            String output = "";
            String s = "";
            String res = "";
            JSONObject jsonObject;

            System.out.println("Attempting to fetch address...");
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
                jsonObject = new JSONObject(output.toString());
                conn.disconnect();
                res = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
            } catch (Exception e) {
                e.printStackTrace();
                res = "catch";
            }

            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            address = res;

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
     public void toOverview(View v){
        Intent intent = new Intent(getApplicationContext(), AttractionOverviewActivity.class);
        startActivity(intent);

     }

    public class getJSON extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
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
                            jsonObject = mat.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String description = jsonObject.getString("description");
                            String pos = jsonObject.getString("latlng");
                            retur = retur + name + "\n"+description+"\n"+pos;
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
            return retur;
        }

        @Override
        protected void onPostExecute (String ss){
            System.out.println("Printing");
            textView.setText(ss);
        }
    }
}