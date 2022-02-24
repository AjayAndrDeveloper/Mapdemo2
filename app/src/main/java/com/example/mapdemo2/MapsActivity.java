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
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
//import com.example.mapdemo2.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity {
   Button zoom_In, zoom_out;
   SearchView searchView;

   Boolean routeMode = false, clearOn = true;
   private GoogleMap mMap;
   Marker marker, markerFirst, markerSecond;
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


      String api_key = getString(R.string.api_Key);
//      if (!Places.isInitialized()){
//         Places.initialize(this,api_key);
//      }
//      PlacesClient   placesClient =Places.createClient(this);
//      // Initialize the AutocompleteSupportFragment.
//      AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//              getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
////      autocompleteFragment.setLocationBias(RectangularBounds.newInstance(new LatLng()));
//
//      // Specify the types of place data to return.
//      autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
//
//      // Set up a PlaceSelectionListener to handle the response.
//      autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//         @Override
//         public void onPlaceSelected(@NonNull Place place) {
//
//            final LatLng latLng = place.getLatLng();
//            // TODO: Get info about the selected place.
//            Log.d("places", "Place: " + place.getName() + ", " + place.getId()+" ," + latLng);
//         }
//
//
//         @Override
//         public void onError(@NonNull Status status) {
//            // TODO: Handle the error.
////            Log.i(TAG, "An error occurred: " + status);
//         }
//      });


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
            if (location != null)
               mapFragment.getMapAsync(new OnMapReadyCallback() {

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
                        int count = 0;

                        @Override
                        public void onMapClick(LatLng latlng) {
                           Log.d("TAG", "onMapClick: " + routeMode);
                           // TODO Auto-generated method stub
                           if (!routeMode) {
                              if (marker != null) {
                                 marker.remove();
                              }
                              try {
                                 myAddress = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                              } catch (IOException ioException) {
                                 ioException.printStackTrace();
                              }
//                        String address = myAddress.get(0).getFeatureName();
                              Log.d("address", "onMapClick: " + myAddress.get(0).getAdminArea() + "  " + myAddress.get(0).getSubAdminArea() +
                                      "     p " + myAddress.get(0).getPremises() + "  myAddress" + myAddress + "    SUBLOCALOITY" + myAddress.get(0).getSubLocality() + "  FACE" + myAddress.get(0).getThoroughfare());
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
                           } else {
                              if (clearOn) {
                                 googleMap.clear();
                              }
                              if (count < 2) {
                                 clearOn = false;
                                 LatLng first = null;
                                 LatLng second = null;
                                 if (count == 0) {

                                    first = new LatLng(latlng.latitude, latlng.longitude);
                                    markerFirst = googleMap.addMarker(new MarkerOptions()
                                            .position(first)
                                            .title(myAddress.get(0).getAddressLine(0))
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                    Log.d("LatLng", "onMapClick:1 " + first);
                                 }
                                 if (count == 1) {
                                    second = new LatLng(latlng.latitude, latlng.longitude);
                                    markerSecond = googleMap.addMarker(new MarkerOptions()
                                            .position(second)
                                            .title(myAddress.get(0).getAddressLine(0))
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                    Log.d("LatLng", "onMapClick: 2 " + second);

                                 }
//                              Log.d("LatLng", "onMapClick:123 "+ first  + " " + second);
                                 count++;
                              } else {
                                 googleMap.clear();
                                 count = 0;
                              }

                           }
                        }
                     });


                     spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                           switch (position) {
                              case 0:
                                 googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                              googleMap.setBuildingsEnabled(true);
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
                              try {
                                 Address address1 = addressList.get(0);
                                 LatLng latLng1 = new LatLng(address1.getLatitude(), address1.getLongitude());

                                 googleMap.addMarker(new MarkerOptions().position(latLng1).title(Location));
                                 googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 10));
                              } catch (Exception e) {
                                 Toast.makeText(MapsActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                                 searchView.setQuery(" ", false);
                              }


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