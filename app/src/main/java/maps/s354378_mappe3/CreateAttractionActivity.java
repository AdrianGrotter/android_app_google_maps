package maps.s354378_mappe3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreateAttractionActivity extends AppCompatActivity {
    LatLng myLatLng;
    EditText description;
    EditText name;

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
                Attraction a = new Attraction();
                a.description = description.getText().toString();
                a.name = name.getText().toString();
                a.pos = myLatLng;
                Geocoder coder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> res = coder.getFromLocation(myLatLng.latitude, myLatLng.longitude, 1);
                    a.address = res.get(0).getAddressLine(0);
                    Toast.makeText(CreateAttractionActivity.this, "Registered new Attraction!\nName: " + a.getName() + "\nDesc: "+a.getDescription() +
                            "\nAdress: " + a.getAddress() + "\nLoc: " + a.getPos(), Toast.LENGTH_SHORT).show();
                    activityMain();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateAttractionActivity.this, "Houston, we have a problem...", Toast.LENGTH_SHORT).show();
                }
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
}
