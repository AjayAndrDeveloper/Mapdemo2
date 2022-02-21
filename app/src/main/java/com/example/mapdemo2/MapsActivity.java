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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.mapdemo2.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
//import com.example.mapdemo2.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity {
   Button zoom_In, zoom_out;
   SearchView searchView;
   private GoogleMap mMap;
   Marker marker;
   Geocoder geocoder;
   CameraUpdate centre, zoom;
   List<Address> myAddress;
   SupportMapFragment mapFragment;
   private Spinner spinner;
   private static final String[] paths = {"NORMAL MAP ", "HYBRID MAP", "SATELLITE MAP", "TERRAIN MAP"};
   FusedLocationProviderClient fusedLocationProviderClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_maps);
      zoom_In = findViewById(R.id.zoomIn);
      zoom_out = findViewById(R.id.zoomOut);
      spinner = (Spinner) findViewById(R.id.spinner);
      searchView = findViewById(R.id.search_bar);
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivity.this,
              android.R.layout.simple_spinner_item, paths);
      spinner.setAdapter(adapter);
      geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map);


      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         // TODO: Consider calling
         //    ActivityCompat#requestPermissions
         // here to request the missing permissions, and then overriding
         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
         //                                          int[] grantResults)
         // to handle the case where the user grants the permission. See the documentation
         // for ActivityCompat#requestPermissions for more details.
         getCurrentLocation();
         return;
      } else {
         ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

      }


   }


   private void getCurrentLocation() {

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
//      mMap.setMyLocationEnabled(true);

      Task<Location> task = fusedLocationProviderClient.getLastLocation();

      task.addOnSuccessListener(new OnSuccessListener<Location>() {
         @Override
         public void onSuccess(Location location) {
            if (location != null) mapFragment.getMapAsync(new OnMapReadyCallback() {
               @Override
               public void onMapReady(GoogleMap googleMap) {


                  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                  try {
                     myAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                  } catch (IOException ioException) {
                     ioException.printStackTrace();
                  }
                  String address = myAddress.get(0).getAddressLine(0);
                  MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(address);
                  googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                  googleMap.addMarker(markerOptions);


                  googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                     @Override
                     public void onMapClick(LatLng latlng) {
                        // TODO Auto-generated method stub

                        if (marker != null) {
                           marker.remove();
                        }

                        try {
                           myAddress = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                        } catch (IOException ioException) {
                           ioException.printStackTrace();
                        }
                        String address = myAddress.get(0).getFeatureName();
                        Log.d("address", "onMapClick: " + myAddress.get(0).getAdminArea() + "  " + myAddress.get(0).getSubAdminArea() +
                                "     p " + myAddress.get(0).getPremises() + "  myAddress" + myAddress + "    SUBlOCALOITY" + myAddress.get(0).getSubLocality() + "  FACE" + myAddress.get(0).getThoroughfare());
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(myAddress.get(0).getAddressLine(0))
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        centre = CameraUpdateFactory.newLatLng(new LatLng(latlng.latitude, latlng.longitude));
                        zoom = CameraUpdateFactory.zoomTo(15);
                        googleMap.moveCamera(centre);
                        googleMap.animateCamera(zoom);
                        System.out.println(latlng);

                     }
                  });

                  spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                     @Override
                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                           case 0:
                              googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                              googleMap.setBuildingsEnabled(true);
                              // Whatever you want to happen when the first item gets selected
                              break;
                           case 1:
                              googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                              // Whatever you want to happen when the second item gets selected
                              break;
                           case 2:
                              googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                              // Whatever you want to happen when the thrid item gets selected
                              break;
                           case 3:
                              googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                              break;

                        }
                     }

                     @Override
                     public void onNothingSelected(AdapterView<?> parent) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                     }
                  });
                  zoom_In.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                     }
                  });
                  zoom_out.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                        googleMap.animateCamera(CameraUpdateFactory.zoomOut());
                     }
                  });
                  searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                     @Override
                     public boolean onQueryTextSubmit(String query) {
                        String Location = searchView.getQuery().toString();
                        List<Address> addressList = null;
                        if (Location != null || !Location.equals("")) {
                           try {
                              addressList = geocoder.getFromLocationName(Location, 1);
                           } catch (IOException ioException) {
                              ioException.printStackTrace();
                           }
                           Address address1 = addressList.get(0);
                           LatLng latLng1 = new LatLng(address1.getLatitude(), address1.getLongitude());

                           googleMap.addMarker(new MarkerOptions().position(latLng1).title(Location));
                           googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 10));

                        }
                        return false;
                     }

                     @Override
                     public boolean onQueryTextChange(String newText) {
                        googleMap.clear();
                        return false;
                     }
                  });

               }
            });
         }
      });
   }


   //
//
//
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == 44) {
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
         }
      }

   }
}