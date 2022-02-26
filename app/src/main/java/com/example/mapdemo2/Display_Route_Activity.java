package com.example.mapdemo2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mapdemo2.Helpers.FetchURL;
import com.example.mapdemo2.Helpers.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Display_Route_Activity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

   private GoogleMap mMap;
   Location currentLocation;
   LatLng currentLatLng, destination_LatLng;
   EditText et_Location;
   Button btn_getDirection;
   Geocoder geocoder;
   TextView tv_distance;
   String str_location;
   MarkerOptions starting_Location, destination_Location;
   Polyline currentPolyline;
   Marker marker;
   Spinner spinner;
   String mode;
   List<Address> myAddress;
   private static final String[] drivingModes = {"DRIVING", "WALKING", "TRANSIT"};

   FusedLocationProviderClient fusedLocationProviderClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_display_route);
      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
      fetchLocation();
      et_Location = findViewById(R.id.et_location);
      btn_getDirection = findViewById(R.id.btn_getDirection);
      tv_distance = findViewById(R.id.tv_distance);
      spinner = findViewById(R.id.drivingModeOptions);
      str_location = et_Location.getText().toString();
      geocoder = new Geocoder(Display_Route_Activity.this, Locale.getDefault());
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(Display_Route_Activity.this,
              android.R.layout.simple_spinner_item, drivingModes);
      spinner.setAdapter(adapter);

      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
               case 0:

                  mode = "driving";
                  Toast.makeText(Display_Route_Activity.this, mode + " Clicked", Toast.LENGTH_SHORT).show();
                  break;
               case 1:

                  mode = "walking";
                  Toast.makeText(Display_Route_Activity.this, mode + " Clicked", Toast.LENGTH_SHORT).show();
                  break;
               case 2:
                  mode = "TRANSIT";
                  Toast.makeText(Display_Route_Activity.this, mode + " Clicked", Toast.LENGTH_SHORT).show();
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(Display_Route_Activity.this, "Driving", Toast.LENGTH_SHORT).show();
            mode = "driving";
         }
      });
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.route_map_fragment);
      mapFragment.getMapAsync(this);

   }

   private void fetchLocation() {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

         return;
      }
      Task<Location> task = fusedLocationProviderClient.getLastLocation();
      task.addOnCompleteListener(new OnCompleteListener<Location>() {
         @Override
         public void onComplete(@NonNull Task<Location> task) {
            if (task.isSuccessful()) {
               // Set the map's camera position to the current location of the device.

               currentLocation = task.getResult();
               currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
               if (currentLocation != null) {

                  try {
                     myAddress = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                     String address = myAddress.get(0).getAddressLine(0);
                     mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).position(currentLatLng).title(address));
                  } catch (IOException ioException) {
                     ioException.printStackTrace();
                  }
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                          new LatLng(currentLocation.getLatitude(),
                                  currentLocation.getLongitude()), 15));
               }
            } else {


               mMap.addMarker(new MarkerOptions().position(currentLatLng));
               mMap.moveCamera(CameraUpdateFactory
                       .newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                               currentLocation.getLongitude()), 20));
               mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
         }
      });
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

      btn_getDirection.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            str_location = et_Location.getText().toString();
//            tv_location.setText(str_location);


            List<Address> addressList = null;
            if (marker != null) {
               marker.remove();
            }
            if (str_location != null && !str_location.equals("")) {
               try {
                  Log.d("hello", "onClick: " + str_location);
                  addressList = geocoder.getFromLocationName(str_location, 1);
               } catch (IOException ioException) {
                  ioException.printStackTrace();
               }

               try {
                  Address address = addressList.get(0);
                  destination_LatLng = new LatLng(address.getLatitude(), address.getLongitude());
                  Log.d("location_dest", "onClick: " + destination_LatLng + " " + currentLatLng);
                  marker = googleMap.addMarker(new MarkerOptions().position(destination_LatLng).title(str_location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                  googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination_LatLng, 15));
                  Log.d("jio", "onClick: " + " " + currentLatLng + "  " + destination_LatLng);
                  starting_Location = new MarkerOptions().position(currentLatLng).title("starting Location");
                  destination_Location = new MarkerOptions().position(destination_LatLng).title("Destination Location");

                  String url = getUrl(starting_Location.getPosition(), destination_Location.getPosition(), mode);
                  new FetchURL(Display_Route_Activity.this).execute(url, mode);
                  details();
                  Log.d("url", "onClick: " + url + "  " + mode);

               } catch (Exception e) {
                  Toast.makeText(Display_Route_Activity.this, "Location not found", Toast.LENGTH_SHORT).show();
               }

            } else {
               Toast.makeText(Display_Route_Activity.this, "Please enter the location", Toast.LENGTH_SHORT).show();
            }

         }


      });
//

      // Add a marker in Sydney and move the camera
//      LatLng sydney = new LatLng(-34, 151);
//      mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//      mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
   }

   private void details() {
//      Volley.newRequestQueue(Display_Route_Activity.this);
      Volley.newRequestQueue(Display_Route_Activity.this).add(new StringRequest("https://maps.googleapis.com/maps/api/directions/json?origin=" + currentLocation.getLatitude() + "," +
              currentLocation.getLongitude() + "&destination=" + destination_LatLng.latitude + "," + destination_LatLng.longitude + "&mode=" + mode + "&key=AIzaSyDvuH_pG-kn1XZsYvKkiu-RxLeEK9G-9NY", new Response.Listener<String>() {
         public void onResponse(String str) {
            try {
               JSONObject jSONObject = new JSONObject(str);
               JSONObject jDuration, jDistance;

               int i = 0;
               if (jSONObject.getString(NotificationCompat.CATEGORY_STATUS).equals("OK")) {
                  //getting routes JSONARRAY
                  JSONArray jSONArray = jSONObject.getJSONArray("routes");
                  if (jSONArray != null) {
                     // FROM ROUTE WE GETTING LEGS JSONARRAY
                     JSONArray legsArray = jSONArray.getJSONObject(0).getJSONArray("legs");
                     // FROM LEGSARRAY WE GET JSONOBJECT THE FROMTHIS OBJECT WE GET STRING OF DISTANCE & DURATION
                     JSONObject jSONObject2 = legsArray.getJSONObject(0);
                     jDistance = jSONObject2.getJSONObject("distance");
                     jDuration = jSONObject2.getJSONObject("duration");
                     String distance = jDistance.getString("text");
                     String duration = jDuration.getString("text");
                     Log.d("TAG", "onResponse: " + jSONObject2.toString());
                     tv_distance.setText(distance);
                     Log.d("TAG", "onResponse: Duration : " + duration);
                     ((TextView) findViewById(R.id.tv_duration)).setText(duration);
                  }
               }
            } catch (Exception e) {
               Log.d("TAG", "onResponse: Exception " + e.getMessage());
               Toast.makeText(Display_Route_Activity.this, "Try Exception", Toast.LENGTH_SHORT).show();
            }
         }

      }, new Response.ErrorListener() {
         public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(Display_Route_Activity.this, "Unable to Find Route", Toast.LENGTH_SHORT).show();
         }
      }));

   }

   private String getUrl(LatLng origin, LatLng destination, String directionMode) {
      String str_origin = "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
      String str_dest = "destination=" + destination_LatLng.latitude + "," + destination_LatLng.longitude;
      String mode = "mode=" + directionMode;
      String paraMeter = str_origin + "&" + str_dest + "&" + mode;
      String output = "json";
      String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + paraMeter + "&key=" + getString(R.string.api_Key);
      return url;
   }

   @Override
   public void onTaskDone(Object... values) {
      if (currentPolyline != null)

         currentPolyline.remove();
      currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
   }
}