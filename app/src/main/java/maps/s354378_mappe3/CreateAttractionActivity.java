package maps.s354378_mappe3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreateAttractionActivity extends AppCompatActivity {
    LatLng myLatLng;
    EditText description;
    EditText name;
    Attraction a = new Attraction();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Objects.requireNonNull(getSupportActionBar()).hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_attraction);
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        myLatLng = new LatLng(Double.longBitsToDouble(sp.getLong("lat", 0)),
                Double.longBitsToDouble(sp.getLong("long", 0)) );
        Toast.makeText(this, "Pos: "+myLatLng, Toast.LENGTH_SHORT).show();

        description = (EditText) findViewById(R.id.input_description);
        name = (EditText) findViewById(R.id.input_name);

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.description = description.getText().toString();
                a.name = name.getText().toString();
                a.pos = myLatLng;
                GetGeo task1 = new GetGeo();
                System.out.println("latlng12: "+myLatLng);
                task1.execute();

                Toast.makeText(CreateAttractionActivity.this, "Made it this far", Toast.LENGTH_SHORT).show();

                activityMain();

            }
        });
    }



    private void activityMain() {
        Intent myIntent = new Intent(this, MapsActivity.class);
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putLong("lat", Double.doubleToRawLongBits(myLatLng.latitude));
        e.putLong("long", Double.doubleToRawLongBits(myLatLng.longitude));
        e.apply();
        startActivity(myIntent);
    }

    public class GetGeo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params){
            String query = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + myLatLng.latitude + "," + myLatLng.longitude + "&key=" + getResources().getString(R.string.key);
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
        protected void onPostExecute(String res){
            super.onPostExecute(res);
            a.address = res;
            System.out.println("InPostExecute of GetGeo");
            sendJSON task = new sendJSON();
            String latlng = a.getPos().latitude+","+a.getPos().longitude;
            task.execute(new String[]{"http://data1500.cs.oslomet.no/~s354378/jsonin.php?Name="+a.getName()+"&Description="+a.getDescription()+"&Address="+a.getAddress()+"&LatLng="+latlng});
        }
    }

    //url for later: https://stackoverflow.com/questions/42767249/android-post-request-with-json
    public class sendJSON extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    System.out.println(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk galt";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String ss) {
            System.out.println("onPostExecute: "+ss);
        }
    }
}
