package maps.s354378_mappe3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public BottomSheetFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String lat;
        String lng;

        if(getArguments() != null){
            TextView address = getView().findViewById(R.id.popupaddress);
            TextView pos = getView().findViewById(R.id.pos);
            address.setText(getArguments().getString("title"));
            LatLng position = getArguments().getParcelable("pos");
            lat = formatPosition(position.latitude);
            lng = formatPosition(position.longitude);
            String res = "Position: " + lat + ", " + lng;
            pos.setText(res);


            EditText name = getView().findViewById(R.id.inputName);
            EditText desc = getView().findViewById(R.id.inputDesc);
            TextView textName = getView().findViewById(R.id.textname);
            TextView textDesc = getView().findViewById(R.id.textviewdesc);
            Button edit = getView().findViewById(R.id.edit);
            Button delete = getView().findViewById(R.id.delete);
            Button submit = getView().findViewById(R.id.btnSubmitForm);

            System.out.println(getArguments().getBoolean("new"));
            if(!getArguments().getBoolean("new")){
                name.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                submit.setVisibility(View.INVISIBLE);
                textDesc.setText(getArguments().getString("desc"));
                textName.setText(getArguments().getString("name"));

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(view.getContext(), EditAttractionActivity.class);
                        intent.putExtra("id", getArguments().getInt("id"));
                        intent.putExtra("name", getArguments().getString("name"));
                        intent.putExtra("address", getArguments().getString("title"));
                        intent.putExtra("desc", getArguments().getString("desc"));
                        Bundle b = new Bundle();
                        LatLng m = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        b.putParcelable("lat", m);
                        intent.putExtra("bundle", b);
                        view.getContext().startActivity(intent);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(getArguments().getInt("id"));
                        sendJSON task = new sendJSON();
                        task.execute(new String[]{"http://data1500.cs.oslomet.no/~s354378/jsondelete.php?Id="+getArguments().getInt("id")});
                        reload();
                        Toast.makeText(getContext(), "Attraction was deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                textName.setVisibility(View.GONE);
                textDesc.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String latlng = lat+","+lng;
                        sendJSON task = new sendJSON();
                        task.execute(new String[]{"http://data1500.cs.oslomet.no/~s354378/jsonin.php?Name="+name.getText().toString()+"&Description="+desc.getText().toString()+"&Address="+getArguments().getString("title")+"&LatLng="+latlng});
                        reload();
                        Toast.makeText(getContext(), "Attraction was saved!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    public void reload(){
        Intent myIntent = new Intent(getContext(), MapsActivity.class);
        startActivity(myIntent);
    }

    public String formatPosition(double d){
        if(d < -10) return String.valueOf(d).substring(0,6);
        else if(d < 0 || d > 10) return String.valueOf(d).substring(0,5);
        else return String.valueOf(d).substring(0,4);
    };


    public class sendJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";

            try {
                URL urlen = new URL(urls[0]);
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
                return "Noe gikk galt i sendJSON";
            }
        }

        @Override
        protected void onPostExecute(String ss) {
            System.out.println("onPostExecute: "+ss);
        }
    }
}
