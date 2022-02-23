package com.example.mapdemo2;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
   LatLng second = null;
   Double distance = 0.0;
   List<Address> myAddress1;
   List<Address> myAddress2;
   List<LatLng> latLng_List = new ArrayList<>();
   List<Marker> marker_list = new ArrayList<>();
   Geocoder geocoder;
   Marker markerFirst, markerSecond;
   Polyline polyline = null;
   PolygonOptions polygonOptions;
   TextView tv_distance;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

//      binding = ActivityFindDistanceBinding.inflate(getLayoutInflater());
      try {
         setContentView(R.layout.activity_find_distance);
      } catch (Exception e) {
         Log.e("TAG", "onCreate: " + e.getMessage());
      }


      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map_fragment);
      mapFragment.getMapAsync(this);
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
      btn_Undo = findViewById(R.id.btnUndo);
      tv_distance = findViewById(R.id.distanceView);
      mMap = googleMap;
      myAddress1 = new ArrayList<>();
      myAddress2 = new ArrayList<>();
      polygonOptions = new PolygonOptions();
      geocoder = new Geocoder(Find_Distance_Activity.this, Locale.getDefault());
      // Add a marker in Sydney and move the camera
//      LatLng sydney = new LatLng(-34, 151);
//      mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//      mMap.animateCamera(CameraUpdateFactory.zoomTo(  20));
//      mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
      googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

         @Override
         public void onMapClick(LatLng latLng) {

            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            Marker marker = googleMap.addMarker(markerOptions);

            latLng_List.add(latLng);
            marker_list.add(marker);
            if (polyline!= null) polyline.remove();
            googleMap.addPolyline(new PolylineOptions()
                          .addAll(latLng_List)
                          .width(5)
                          .color(Color.RED));

            Log.d("listSize", "onMapClick: " + latLng_List.size()   );
               if(latLng_List.size()>=2) {
                  first =new LatLng(latLng_List.get(latLng_List.size() - 2).latitude,latLng_List.get(latLng_List.size() - 2).longitude);
                  distance = distance + SphericalUtil.computeDistanceBetween(latLng,first);
                  tv_distance.setText(distance/1000+ "   " );
               }
//               btn_Undo.setOnClickListener(new View.OnClickListener() {
//                  @Override
//                  public void onClick(View v) {
//                     if (latLng_List.size()>0){
//                        try {
//                           latLng_List.remove(latLng_List.size()-1);
////                           marker.v( );
//
//                        }
//                        catch (ArrayIndexOutOfBoundsException  e){
//                           e.printStackTrace();
//
//                        }
//                     }
//                  }
//               });
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