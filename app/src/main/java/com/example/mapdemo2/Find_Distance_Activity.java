package com.example.mapdemo2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Find_Distance_Activity extends FragmentActivity implements OnMapReadyCallback {
   Button btn_Undo;
   private GoogleMap mMap;
   SupportMapFragment mapFragment;
   int count = 0;
   LatLng first = null;
   double floatDistance;

   LatLng second = null;
   Double distance = 0.0;
   List<Address> myAddress1;
   List<Address> myAddress2;
   List<LatLng> latLng_List = new ArrayList<>();
   List<Marker> marker_list = new ArrayList<>();
   List<Double> distance_list = new ArrayList<>();
   List<Polyline> polyline_list = new ArrayList<>();
   Geocoder geocoder;
   Marker markerFirst, markerSecond;
   Polyline polyline;
   PolygonOptions polygonOptions;
   TextView tv_distance;
   Marker marker;
      Location currentLocation;
   FusedLocationProviderClient fusedLocationProviderClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

       try {
         setContentView(R.layout.activity_find_distance);
      } catch (Exception e) {
         Log.e("TAG", "onCreate: " + e.getMessage());
      }

      fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
         fetchLocation();
//
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map_fragment);
      mapFragment.getMapAsync(this);
   }

   private void fetchLocation() {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

         return;
      }
      Task<Location> task = fusedLocationProviderClient.getLastLocation();
      task.addOnCompleteListener(new OnCompleteListener<Location>() {
         @Override
         public void onComplete(@NonNull Task<Location> task) {
            if (task.isSuccessful()) {
               // Set the map's camera position to the current location of the device.
               currentLocation = task.getResult();
               if (currentLocation != null) {
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                          new LatLng(currentLocation.getLatitude(),
                                  currentLocation.getLongitude()), 15));
               }
            } else {
//               Log.d(TAG, "Current location is null. Using defaults.");
//               Log.e(TAG, "Exception: %s", task.getException());
               mMap.moveCamera(CameraUpdateFactory
                       .newLatLngZoom(  new LatLng(currentLocation.getLatitude(),
                               currentLocation.getLongitude()), 15));
               mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
         }
      });
    }



   @Override
   public void onMapReady(GoogleMap googleMap) {
      btn_Undo = findViewById(R.id.btnUndo);
      tv_distance = findViewById(R.id.distanceView);
      mMap = googleMap;
      myAddress1 = new ArrayList<>();
      myAddress2 = new ArrayList<>();
      polygonOptions = new PolygonOptions();
      geocoder = new Geocoder(Find_Distance_Activity.this, Locale.getDefault());



//
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
      googleMap.setMyLocationEnabled(true);



      googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

         @Override
         public void onMapClick(LatLng latLng) {

            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            marker = googleMap.addMarker(markerOptions);

            latLng_List.add(latLng);
            marker_list.add(marker);
//            if (polyline != null) polyline.remove();
            polyline = googleMap.addPolyline(new PolylineOptions()
                    .addAll(latLng_List)
                    .width(5)
                    .color(Color.RED));
            polyline_list.add(polyline);
            Log.d("listSize", "onMapClick: " + latLng_List.size());
            if (latLng_List.size() >= 2) {
               first = new LatLng(latLng_List.get(latLng_List.size() - 2).latitude, latLng_List.get(latLng_List.size() - 2).longitude);
               if (marker_list.size()==2){
                  distance = distance + SphericalUtil.computeDistanceBetween(latLng, first);
               }else {
               distance = distance_list.get(distance_list.size()-1) + SphericalUtil.computeDistanceBetween(latLng, first);}
               distance_list.add(distance);
               float floatDistance = distance_list.get(distance_list.size()-1).floatValue();
               float f = floatDistance / 1000;
               if (f < 1) {
                  tv_distance.setText(floatDistance + " Meter");
               } else {
                  tv_distance.setText(f + " KM");
               }

            }
            btn_Undo.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  Log.d("marker", "onClick: markerlistsize " + marker_list.size() + "   distance list  " + distance_list.size() + " latlnglist " + latLng_List.size() + "plyline list" + polyline_list.size());

                  if (latLng_List.size() > 0) {
                     try {
                        latLng_List.remove(latLng_List.size() - 1);
                        marker = marker_list.get(marker_list.size() - 1);
                        polyline = polyline_list.get(latLng_List.size());
                        if (distance_list.size()>1) {
                           floatDistance = distance_list.get(distance_list.size() - 2);
                           distance_list.remove(distance_list.size() - 1);
                        }

                     Log.d("marker", "onClick: "+ floatDistance);
                        remove(marker, polyline);
//                        LatLng lastOne = new LatLng(latLng_List.get(latLng_List.size() - 1).latitude, latLng_List.get(latLng_List.size() - 1).longitude);
//                        distance = distance - SphericalUtil.computeDistanceBetween(latLng, lastOne);

//                           distance_list.remove(distance_list.size() - 1);


//                        Log.d("distance1233", "onClick: " + distance_list.size() + " distance" + distance_list.get(distance_list.size() - 1));


                        float f1 = (float) floatDistance;
                        float f = f1 / 1000;
                        Log.d("marker", "onClick: markerlistsize " + marker_list.size() + "   distance list  " + distance_list.size() + " latlnglist " + latLng_List.size()+   "plyline list" + polyline_list.size());
                        if (marker_list.size() != 1) {

                           if (f < 1) {
                              tv_distance.setText(f1 + " Meter");
                              Log.d("123AJ", "onClick: 1");
                           } else {
                              tv_distance.setText(f + " KM");
                              Log.d("123", "onClick: 2");

                           }
                        } else {
                           distance_list.clear();
                           distance = 0.0;
                           f=0;
                           floatDistance=0;
                           f1=0;
                           tv_distance.setText(distance_list.size() + " KM123");
                        }


                     } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        Log.e("TAG", "onClick: " + e+ "  latlnglist  " + latLng_List.size() +  "  markerlist  " + marker_list.size()+ "  distance_list  " + distance_list.size());

                     }

                  }
                  Toast.makeText(Find_Distance_Activity.this, "ajay", Toast.LENGTH_SHORT).show();
               }
            });
//            }
//            else {
//            latLng_List.clear();
//            marker_list.clear();
//            googleMap
////            polyline.remove();
//            }

//                  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(second,2));

//            if (count < 2) {
//               if (count == 0) {
//                  first = new LatLng(latLng.latitude, latLng.longitude);
//                  try {
//                     myAddress1 = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//                  } catch (IOException ioException) {
//                     ioException.printStackTrace();
//                  }
//                  markerFirst = googleMap.addMarker(new MarkerOptions()
//                          .position(first)
//                          .title(myAddress1.get(0).getAddressLine(0))
//                          .icon(BitmapDescriptorFactory
//                                  .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//
//               }
//               if (count == 1) {
//                  second = new LatLng(latLng.latitude, latLng.longitude);
//                  try {
//                     myAddress2 = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//                  } catch (IOException ioException) {
//                     ioException.printStackTrace();
//                  }
//                  markerSecond = googleMap.addMarker(new MarkerOptions()
//                          .position(second)
//                          .title(myAddress2.get(0).getAddressLine(0))
//                          .icon(BitmapDescriptorFactory
//                                  .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                  Log.d("LatLng", "onMapClick: 2 " + second);
//
//               }
//               count++;
//               if (second != null) {
////                  String url= getURL(markerFirst.getPosition(),markerSecond.getPosition(),"driving");
//                  distance = SphericalUtil.computeDistanceBetween(first, second);
//                  float floatDistance = distance.floatValue();
//                  Toast.makeText(Find_Distance_Activity.this, " " + floatDistance / 1000, Toast.LENGTH_SHORT).show();
//
//                  if (polyline != null) {
//                     polyline.remove();
//                  }
//
//
//                  googleMap.addPolyline(new PolylineOptions()
//                          .add(first,second)
//                          .width(5)
//                          .color(Color.RED));
//                  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(second,15));
//                   float f = floatDistance/1000;
//                   if (f<1){
//                      tv_distance.setText(floatDistance +" Meter");
//                   }else {
//                      tv_distance.setText(f +" KM");
//                   }
//
//               }
//               Log.d("hello", "onMapClick: " + "first" + first + "second" + second);
//
//            } else {
//               count = 0;
//               second = null;
//               mMap.clear();
//            }


         }
      });
//      Log.d("hello", "onMapClick: " + "first" + first + "second"+ second);
   }

   public boolean remove(Marker marker, Polyline polyline) {
      if (marker_list.remove(marker) && polyline_list.remove(polyline)) {
         marker_list.remove(marker);
         polyline_list.remove(polyline);
         marker.remove();
         polyline.remove();
         return true;
      }
      return false;
   }

   //   private String getURL(LatLng origin, LatLng dest, String directionMode) {
////      ORIGIN OF ROUTE
//      String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
////      DESTINATION ORIGIN
//      String str_dest = "destination" + dest.latitude + "," + dest.longitude;
////      MODE
//         String mode= "mode" + directionMode;
////       building the parameters| the services;
//      String parameters = str_origin +"&"+str_dest+"&"+mode;
////      output
//      String outPut = "json";
////      building the url to the web server
//      String url = "https://msp.googlespis.com/maps/api/direction/" + outPut + "?" + parameters+ "&key"+" AIzaSyBCEK76PVAr2KwoDy1B_60xfi0DRJZsqCY"  ;
//      return  url;
//
//   }

}