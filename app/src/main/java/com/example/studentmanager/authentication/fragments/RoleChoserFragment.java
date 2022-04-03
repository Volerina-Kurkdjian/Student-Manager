package com.example.studentmanager.authentication.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.studentmanager.ProfesorProfileActivity;
import com.example.studentmanager.R;
import com.example.studentmanager.StudentProfileActivity;
import com.example.studentmanager.database.models.Profesor;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class RoleChoserFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private Button professorbtn;
    private Button studentbtn;
    private SupportMapFragment mapFragment;

    private GoogleMap googleMap;
    private Location currentLocation;
    private Location ase = new Location("");

    private SharedPreferences sharedpref;
    public static final String SHARED_PREF_FILE_NAME = "loginSharedPref";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String REMEMBER = "remember";

    public RoleChoserFragment() {
        // Required empty public constructor
    }


    public static RoleChoserFragment newInstance() {
        return new RoleChoserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_role_choser, container, false);
        professorbtn = view.findViewById(R.id.profesor_button);
        studentbtn = view.findViewById(R.id.student_button);

        sharedpref = getActivity().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        String email = sharedpref.getString(EMAIL, "");
        String password = sharedpref.getString(PASSWORD, "");
        String role = sharedpref.getString("role", "");
        if (!email.equals("") && !password.equals("") && !role.equals("")) {
            if (role.equals("student")) {
                Intent studintent = new Intent(getContext(), StudentProfileActivity.class);
                Bundle bundlenou = new Bundle();
                bundlenou.putString("email", email);
                studintent.putExtras(bundlenou);
                startActivity(studintent);
                getActivity().finish();
            } else {
                Intent profintent = new Intent(getContext(), ProfesorProfileActivity.class);
                Bundle bundlenou = new Bundle();
                bundlenou.putString("email", email);
                profintent.putExtras(bundlenou);
                startActivity(profintent);
                getActivity().finish();
            }

        }

        professorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("role", "professor");
                Navigation.findNavController(view).navigate(R.id.registrationLoginFragment, bundle);

            }
        });

        studentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("role", "student");
                Navigation.findNavController(view).navigate(R.id.registrationLoginFragment, bundle);
            }
        });

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(44.44763648210836, 26.097932242447108))
                .title("Academia de Studii Economice"));

        ase.setLatitude(44.44763648210836);
        ase.setLongitude(26.097932242447108);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.44763648210836, 26.097932242447108), 20));

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(this.currentLocation == null || (this.currentLocation.getLatitude() != location.getLatitude() && currentLocation.getLongitude() != location.getLongitude())) {
            this.currentLocation = location;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20));

            Polyline path = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(ase.getLatitude(), ase.getLongitude()))
                    .width(5)
                    .color(Color.BLUE));
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("Location", "Location provider enabled");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("Location", "Location provider disabled");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location", "onRequestPermissionsResult: not granted");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
}