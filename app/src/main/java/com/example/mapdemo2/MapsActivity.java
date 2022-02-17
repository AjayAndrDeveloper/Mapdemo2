package com.example.mapdemo2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mapdemo2.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
//import com.example.mapdemo2.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
   Button normal_map_Btn, hybrid_map_btn, satellite_map_btn, terrain_map_Btn;
   private GoogleMap mMap;
   Marker marker;
   Geocoder geocoder;
   CameraUpdate centre,zoom;
   List<Address> myAddress;

//   @Override
//   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//      if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//
//         if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
//
//            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            centreMapOnLocation(lastKnownLocation,"Your Location");
//         }
//      }
//   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

//      binding = ActivityMapsBinding.inflate(getLayoutInflater());
      setContentView(R.layout.activity_maps);
      geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);

      normal_map_Btn = findViewById(R.id.MapNormalBtn);
      hybrid_map_btn = findViewById(R.id.MapHybridBtn);
      satellite_map_btn = findViewById(R.id.MapSatelliteBtn);
      terrain_map_Btn = findViewById(R.id.MapTerrainBtn);
      normal_map_Btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
         }
      });
      hybrid_map_btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
         }
      });
      satellite_map_btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
         }
      });
      terrain_map_Btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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
      MarkerOptions markerOptions = new MarkerOptions();
//       Add a marker in Sydney and move the camera
//      LatLng sydney = new LatLng(-34, 151);
//      mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//      mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more details.
         return;
      }
      mMap.setMyLocationEnabled(true);
      mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

         @Override
         public void onMapClick(LatLng latlng) {
            // TODO Auto-generated method stub

            if (marker != null) {
               marker.remove();
            }

            try {
               myAddress = geocoder.getFromLocation(latlng.latitude, latlng.longitude,1);
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
            String address = myAddress.get(0).getPremises();
            Log.d("address", "onMapClick: " + myAddress.get(0).getAdminArea() + "  "+ myAddress.get(0).getSubAdminArea()+
                    "     p " + myAddress.get(0).getPremises()+"  myAddress"+ myAddress  +"    SUBlOCALOITY"+ myAddress.get(0).getSubLocality() + "  FACE"+ myAddress.get(0).getThoroughfare());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(address)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            centre = CameraUpdateFactory.newLatLng(new LatLng(latlng.latitude, latlng.longitude));
            zoom = CameraUpdateFactory.zoomTo(10);
            mMap.moveCamera(centre);
            mMap.animateCamera(zoom);
            System.out.println(latlng);

         }
      });
//      mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//         @Override
//         public boolean onMyLocationButtonClick() {
//
//            return false;
//         }
//      });
      mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

         @Override
         public void onMyLocationChange(Location location) {

       centre = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
              zoom = CameraUpdateFactory.zoomTo(10);

            markerOptions.position(new LatLng(location.getLatitude(),location.getLongitude()));

            try {
                 myAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
            String address = myAddress.get(0).getAddressLine(0);
            markerOptions.title(address);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(centre);
            mMap.animateCamera(zoom);
         }
      });
//      mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//         @Override
//         public boolean onMyLocationButtonClick() {
//
//            return false;
//         }
//      });
   }

}