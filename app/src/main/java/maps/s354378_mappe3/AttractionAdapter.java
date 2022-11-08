package maps.s354378_mappe3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import java.util.concurrent.TimeUnit;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {
    private List<Attraction> mAttractions;
    Context savedContext;
    int pos_global;

    public AttractionAdapter (List<Attraction> attractions){
        mAttractions = attractions;
    }

    @NonNull
    @Override
    public AttractionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        savedContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(savedContext);

        View attractionView = inflater.inflate(R.layout.item_attraction, parent, false);

        return new AttractionAdapter.ViewHolder(attractionView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Attraction attraction = mAttractions.get(position);

        TextView textView1 = holder.name;
        textView1.setText(attraction.getName());
        TextView textView2 = holder.address;
        textView2.setText(attraction.getAddress());
        TextView textView3 = holder.pos;
        textView3.setText(attraction.getPos().toString());
        TextView textView4 = holder.desc;
        textView4.setText(attraction.getDescription());

        Button button1 = holder.delete;
        System.out.println("See this: "+attraction.get_id());
        button1.setTag(attraction.get_id());
        Button edit = holder.edit;
        edit.setTag(holder.edit);


    }

    @Override
    public int getItemCount() {
        return mAttractions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name;
        public TextView desc;
        public TextView address;
        public TextView pos;
        public Button delete;
        public Button edit;

        public ViewHolder(View itemView){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            address = (TextView) itemView.findViewById(R.id.address);
            desc = (TextView) itemView.findViewById(R.id.description);
            pos = (TextView) itemView.findViewById(R.id.pos);
            delete = (Button) itemView.findViewById(R.id.delete);
            edit = (Button) itemView.findViewById(R.id.edit);

            delete.setOnClickListener(this);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Edit: " + edit.getTag());
                    Intent intent = new Intent(view.getContext(), EditAttractionActivity.class);
                    intent.putExtra("id", delete.getTag().toString());
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("address", address.getText().toString());
                    intent.putExtra("desc", desc.getText().toString());
                    intent.putExtra("latlng", pos.getText().toString());
                    String[] ll = pos.getText().toString().split("\\(")[1].split("\\)")[0].split(",");
                    Bundle b = new Bundle();
                    LatLng m = new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1]));
                    b.putParcelable("lat", m);
                    intent.putExtra("bundle", b);
                    view.getContext().startActivity(intent);
                }
            });

        }

        @Override
        public void onClick(View view) {
            System.out.println(delete.getTag());
            int pos = getAdapterPosition();
            sendJSON task = new sendJSON();
            task.execute(new String[]{"http://data1500.cs.oslomet.no/~s354378/jsondelete.php?Id="+delete.getTag()});
            mAttractions.clear();
            getJSON task2 = new getJSON();
            task2.execute( new String[] {"http://data1500.cs.oslomet.no/~s354378/jsonout.php"});
            pos_global = pos;
        }

    }
    public class sendJSON extends AsyncTask<String, Void, String> {

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
                myAttraction.setDescription(newList[2]);
                myAttraction.setAddress("Jordbærstien 2");
                String[] ll = newList[3].split("\\.");
                if(ll.length > 1)
                    myAttraction.setPos(new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1])));
                else myAttraction.setPos(new LatLng(1,1));
                attractionList.add(myAttraction);
            }

            mAttractions = attractionList;
            notifyItemRemoved(pos_global);
        }
    }
}
