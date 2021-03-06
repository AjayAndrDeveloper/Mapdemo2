package com.example.mapdemo2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
   private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =100 ;
   private static final int ERROR_DIALOG_REQUEST =1010 ;
   Button btn_distance,btn_mainMap,btn_find_Route;
   boolean mLocationPermissionGranted;
   private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 100;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      btn_distance = findViewById(R.id.btn_distance);
      btn_mainMap = findViewById(R.id.btn_mainMap);
      btn_find_Route = findViewById(R.id.find_route);



      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

//         requestPermission(MainActivity.this,PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
         return;
      }else{
         // Write you code here if permission already given.
         Toast.makeText(this, "location permission already given", Toast.LENGTH_SHORT).show();
      }

      btn_mainMap.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (isConnected()) {
               Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this,MapsActivity.class));

            } else {
               Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
         }
      });
      btn_distance.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (isConnected()) {
               Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this,Find_Distance_Activity.class));

            } else {
               Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }

         }
      });
      btn_find_Route.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (isConnected()) {
               Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this,Display_Route_Activity.class));

            } else {
               Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
         }
      });

   }

   public boolean isConnected() {
      boolean connected = false;
      try {
         Log.d("isConnected", "isConnected: " + ":");
         ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo nInfo = cm.getActiveNetworkInfo();
         connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
         return connected;
      } catch (Exception e) {
         Log.e("Connectivity Exception", e.getMessage());
      }
      return connected;
   }
}

      //  CHECK FOR LOCATION PERMISSION
//      public static boolean checkPermission(Activity activity){
//         int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//         if (result == PackageManager.PERMISSION_GRANTED){
//
//            return true;
//
//         } else {
//            requestPermission(activity,PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            Toast.makeText(activity.getApplicationContext(), "Plz accept that", Toast.LENGTH_SHORT).show();
//            return false;
//
//         }
//      }
//
//      //REQUEST FOR PERMISSSION
//      public static void requestPermission(Activity activity, final int code){
//
//         if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)){
//
//            Toast.makeText(activity,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
//
//         } else {
////                  requestPermission(activity,PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
////            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},code);
//         }
//


