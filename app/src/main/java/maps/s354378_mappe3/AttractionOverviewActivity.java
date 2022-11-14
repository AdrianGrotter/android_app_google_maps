package maps.s354378_mappe3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttractionOverviewActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attraction_overview);

        Button btnMain = findViewById(R.id.btnMain);

        btnMain.setOnClickListener(view -> activityMain());

        getJSON task = new getJSON();
        task.execute( new String[] {"http://data1500.cs.oslomet.no/~s354378/jsonout.php"});
    }
    private void activityMain() {
        Intent myIntent = new Intent(this, MapsActivity.class);
        startActivity(myIntent);
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
                            String id = jsonObject.getString("id");
                            String name = jsonObject.getString("name");
                            String description = jsonObject.getString("description");
                            String pos = jsonObject.getString("latlng");
                            retur = retur + id + "," + name + ","+description+","+pos+",-,-,";
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

            String[] myList = ss.split(",-,-,");

            List<Attraction> attractionList = new ArrayList<>();

            for (String s : myList){
                String[] newList = s.split(",");
                System.out.println(newList[0]);
                Attraction myAttraction = new Attraction();
                myAttraction.set_id(Integer.parseInt(newList[0]));
                myAttraction.setName(newList[1]);
                myAttraction.setDescription(newList[3]);
                myAttraction.setAddress(newList[2]);
                String[] ll = newList[4].split("\\.");
                if(ll.length > 1)
                myAttraction.setPos(new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1])));
                else myAttraction.setPos(new LatLng(1,1));
                attractionList.add(myAttraction);
            }

            RecyclerView rvAttractions = findViewById(R.id.rvAttractions);

            AttractionAdapter adapter = new AttractionAdapter(attractionList);
            rvAttractions.setAdapter(adapter);
            rvAttractions.setLayoutManager(new LinearLayoutManagerWrapper(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        }
    }

    public class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}
