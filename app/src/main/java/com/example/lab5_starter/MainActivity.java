package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private Button addCityButton;

    private Button deleteCityButton ;

    private ListView cityListView;

    private ArrayList<City> cityArrayList;
    private FirebaseFirestore db ;
    private ArrayAdapter<City> cityArrayAdapter;

    int selectedIndex = -1 ;

    private CollectionReference citiesRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set views
        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);
        deleteCityButton = findViewById(R.id.buttonDeleteCity);

        // create city array
        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList);
        cityListView.setAdapter(cityArrayAdapter);




        db = FirebaseFirestore.getInstance() ;
        citiesRef = db.collection("cities") ;

        //view is the entire collection of docs in the collection. error is returned
        //if something went wrong
        citiesRef.addSnapshotListener( (value, error) -> {

            if( error != null ){
                Log.e("Firestore", error.toString()) ;
            }

            if( value != null && !value.isEmpty())
            {
                cityArrayList.clear() ;
                for(QueryDocumentSnapshot snapshot : value)
                {
                    String name = snapshot.getString("name") ;
                    String province = snapshot.getString("province") ;
                    cityArrayList.add( new City(name, province)) ;

                }
                cityArrayAdapter.notifyDataSetChanged();
            }

        });


        //addDummyData();

        // set listeners
        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(),"Add City");
        });

        cityListView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedIndex = i ;
            City city = cityArrayAdapter.getItem(i);
            CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
            cityDialogFragment.show(getSupportFragmentManager(),"City Details");
        });

        deleteCityButton.setOnClickListener(view -> {
                    if(selectedIndex != -1){
                        City city = cityArrayList.get(selectedIndex);
                        cityArrayList.remove( selectedIndex ) ;
                        cityArrayAdapter.notifyDataSetChanged();
                        selectedIndex = -1 ;
                        DocumentReference docRef = citiesRef.document( city.getName()) ;
                        docRef.delete() ;
                    }


                }
        );

    }

    @Override
    public void updateCity(City city, String title, String year) {
        city.setName(title);
        city.setProvince(year);
        cityArrayAdapter.notifyDataSetChanged();

        // Updating the database using delete + addition
    }

    @Override
    public void addCity(City city){
        cityArrayList.add(city);
        cityArrayAdapter.notifyDataSetChanged();
        DocumentReference docref = citiesRef.document(city.getName()) ;
        docref.set(city) ;

    }
/*
    public void addDummyData(){
        City m1 = new City("Edmonton", "AB");
        City m2 = new City("Vancouver", "BC");
        cityArrayList.add(m1);
        cityArrayList.add(m2);
        cityArrayAdapter.notifyDataSetChanged();
    }

 */
}