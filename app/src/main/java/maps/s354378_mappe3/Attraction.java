package maps.s354378_mappe3;

import com.google.android.gms.maps.model.LatLng;

public class Attraction {
     int _id;
     String address;
     LatLng pos;
     String description;

     public Attraction(String address, LatLng pos, String description) {
          this.address = address;
          this.pos = pos;
          this.description = description;
     }

     public Attraction(int _id, String address, LatLng pos, String description) {
          this._id = _id;
          this.address = address;
          this.pos = pos;
          this.description = description;
     }

     public int get_id() {
          return _id;
     }

     public void set_id(int _id) {
          this._id = _id;
     }

     public String getAddress() {
          return address;
     }

     public void setAddress(String address) {
          this.address = address;
     }

     public LatLng getPos() {
          return pos;
     }

     public void setPos(LatLng pos) {
          this.pos = pos;
     }

     public String getDescription() {
          return description;
     }

     public void setDescription(String description) {
          this.description = description;
     }
}


