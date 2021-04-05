package com.example.googlemaptest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private GoogleMap mMap;
    //private GoogleMapClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient; //현재 위치를 얻기 위해 필요

    @Override
    protected void onCreate(Bundle savedInstanceState) { //구글맵을 생성하는 코드
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //if(mGoogleApiClient == null) {
        //    mGoogleApiClient
        // }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this::onMapReady);//여기부터 아래 두 줄은 현재위치정보 얻고, 여러가지 위치정보 제공
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //구글맵이 준비가 되면 호출되는 부분 (구글맵 객체가 들어옴)
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 125.97); //LatLng은 구글 라이브러리에서 제공하는 위경도객체
        mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul")); //마커표시
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f)); //2~21사이의 값을 줄 수 있음.

        //mMap.setOnInfoWindowClickListener();
    }

    public void onLastLocationButtonClicked(View view) { //현재 위치를 얻는 코드. 67번째 라인부터는 권한부여 버전별 정책에 따른 코드
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //사용자가 현재 위치 정보를 제공하는 거에 허락을 함 && 그 외의 정밀도가 좀 더 낮은 위치를 얻어오는 권한. 이게 사용자한테 허락을 받았냐? 그러면 동작
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS); //string배열로 permission을 나열
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override //권한이 있다면 동작하게 될 코드
            public void onSuccess(Location location) {
                if (location != null) { //위치가 null일경우 체크 꼭 해야 됨.
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    //map에 위치 표시하기
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("현재 위치"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(2.0f));
                }
            }
        });//mFused~라는 얘가 마지막 위치정보를 얻음 -> 성공(사용자가 권한 허락을 해줘야 함)하면 Listener가 동작 -> @Override밑으로의 코드가 호출됨.
    }

    @Override //사용자 요청을 처리하는 코드
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show(); //권한이 없다면 띄울 것
                }
        }
    }
}