package maps.s354378_mappe3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;

public class CreateAttractionActivity extends AppCompatActivity {
    LatLng myLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_attraction);
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        myLatLng = new LatLng(Double.longBitsToDouble(sp.getLong("lat", 0)),
                Double.longBitsToDouble(sp.getLong("long", 0)) );
        Toast.makeText(this, "Pos: "+myLatLng, Toast.LENGTH_SHORT).show();

        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        Button btnSubmit = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
