package maps.s354378_mappe3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class EditAttractionActivity extends AppCompatActivity {
    EditText name;
    EditText description;
    List<Attraction> attractions_global;

    protected void onCreate(Bundle savedInstanceState){
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_attraction);
        

        name = findViewById(R.id.inputName);
        description = findViewById(R.id.inputDescription);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnReturn = findViewById(R.id.btnReturn);

        Bundle myBundle = getIntent().getParcelableExtra("bundle");
        name.setText(myBundle.getString("name"));
        description.setText(myBundle.getString("desc"));
        

        btnSubmit.setOnClickListener(view -> {
            Attraction a = new Attraction();
            a.setAddress(getIntent().getExtras().getString("address"));
            a.setPos(myBundle.getParcelable("lat"));
            a.setDescription(description.getText().toString());
            a.setName(name.getText().toString());
            a.set_id(getIntent().getExtras().getInt("id"));

            sendJSON task = new sendJSON();
            String latlng = a.getPos().latitude+","+a.getPos().longitude;
            System.out.println(a.get_id()+"\n"+a.getName()+"\n"+a.getDescription()+"\n"+a.getAddress()+"\n"+a.getPos().longitude+"\n"+a.getPos().latitude);
            task.execute(new String[]{"http://data1500.cs.oslomet.no/~s354378/jsonupdate.php/?Id=" + a.get_id() + "&Name=" + a.getName() + "&Description=" + a.getDescription() + "&LatLng="+latlng});


            Toast.makeText(getApplicationContext(), "Attraksjonen ble oppdatert!", Toast.LENGTH_SHORT).show();
            back();
        });
        btnReturn.setOnClickListener(view -> back());
    }
    private void back() {
        Intent myIntent = new Intent(this, MapsActivity.class);
        startActivity(myIntent);
    }

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
                    System.out.println("The request was completed.");
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String ss) {
            System.out.println("in onPostExecute");
        }
    }
}
