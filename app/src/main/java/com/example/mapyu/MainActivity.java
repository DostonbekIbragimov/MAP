package com.example.mapyu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.yandex.mapkit.GeoObject;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.search.*;
import android.widget.Toast;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private SearchManager searchManager;
    private Session searchSession;
    private MapView mapview;
    private final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("1f3291c1-3bb0-4181-87ec-f3707346d145");
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);

        setContentView(R.layout.activity_main);

        final AutoCompleteTextView textView = findViewById(R.id.search);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapview = findViewById(R.id.mapview);

        textView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                SearchLocation(textView.getText().toString(),textView);
            }
            @Override
            public void afterTextChanged(Editable editable)
            {
                SearchLocation(textView.getText().toString(),textView);
            }
        });

        ImageButton button = findViewById(R.id.bttn);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MoveLoacation(textView.getText().toString());
                textView.clearFocus();
            }
        });

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                MoveLoacation(textView.getText().toString());
                textView.clearFocus();
            }
        });

    }

    String search;

    private void SearchLocation(String query, final AutoCompleteTextView searchView)
    {
        searchView.clearListSelection();
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapview.getMap().getVisibleRegion()),
                new SearchOptions(), new Session.SearchListener()
                {
                    @Override
                    public void onSearchResponse(@NonNull Response response)
                    {
                        List<String> set = new ArrayList<>();
                        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
                            set.add(searchResult.getObj().getName());
                            GeoObject geo = searchResult.getObj();

                        }
                        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(searchView.getContext(), android.R.layout.simple_dropdown_item_1line,set);
                        searchView.setAdapter(Adapter);
                        //searchView.setSuggestionsAdapter();
                    }

                    @Override
                    public void onSearchError(@NonNull Error error)
                    {

                    }
                }
        );
    }

    private void MoveLoacation(String Name)
    {
        searchSession = searchManager.submit(
                Name,
                VisibleRegionUtils.toPolygon(mapview.getMap().getVisibleRegion()),
                new SearchOptions(), new Session.SearchListener()
                {
                    @Override
                    public void onSearchResponse(@NonNull Response response)
                    {
                        GeoObject geo = null;
                        List<String> set = new ArrayList<>();
                        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
                            geo = searchResult.getObj();
                            break;
                        }
                        Point destinaton = geo.getGeometry().get(0).getPoint();
                        mapview.getMap().move(
                                new CameraPosition(destinaton, 14.0f, 0.0f, 0.0f),
                                new Animation(Animation.Type.SMOOTH, 4.5f),
                                null);
                        MapObjectCollection mapObjects = mapview.getMap().getMapObjects();
                        mapObjects.clear();
                        mapObjects.addPlacemark(destinaton, ImageProvider.fromResource(mapview.getContext(), R.drawable.place));
                    }

                    @Override
                    public void onSearchError(@NonNull Error error)
                    {

                    }
                }
        );
    }

    @Override
    protected void onStop()
    {
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();
    }
}