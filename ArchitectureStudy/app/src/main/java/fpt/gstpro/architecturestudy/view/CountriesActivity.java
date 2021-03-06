package fpt.gstpro.architecturestudy.view;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fpt.gstpro.architecturestudy.R;
import fpt.gstpro.architecturestudy.controller.CountriesController;
import fpt.gstpro.architecturestudy.model.Country;
import fpt.gstpro.architecturestudy.networking.CountriesApi;
import fpt.gstpro.architecturestudy.networking.CountriesService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CountriesActivity extends AppCompatActivity {

    CountriesController controller;

    private final CountriesAdapter countriesAdapter;
    private ProgressBar progress;
    private RecyclerView listView;
    private EditText searchField;
    private CountriesApi apiService;
    private List<Country> countries;

    public CountriesActivity() {
        super();
        controller = new CountriesController(this);
        countriesAdapter = new CountriesAdapter();
        countries = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);

        progress = findViewById(R.id.progress);
        listView = findViewById(R.id.listView);
        searchField = findViewById(R.id.searchField);

        apiService = CountriesService.getInstance().getService();
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(countriesAdapter);
        countriesAdapter.setOnItemClickListener(country -> Toast.makeText(CountriesActivity.this, country.getCountryInfo(), Toast.LENGTH_SHORT).show());
        searchField.addTextChangedListener(new TextWatcher() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().trim().isEmpty()) {
                    List<Country> filteredCountries = countries.stream().filter(country -> country.getName().toLowerCase().contains(s.toString().trim())).collect(Collectors.toList());
                    countriesAdapter.updateCountries(filteredCountries);
                } else {
                    countriesAdapter.updateCountries(countries);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        controller.onFetchCountries();
    }

    public void onSuccess(List<Country> result){
        progress.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        searchField.setEnabled(true);

        countries = result;
        countriesAdapter.updateCountries(result);
    }
    public void onError(Throwable e) {
        listView.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        searchField.setEnabled(false);

        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}