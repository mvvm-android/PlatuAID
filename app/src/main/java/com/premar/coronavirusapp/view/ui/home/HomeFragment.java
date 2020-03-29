package com.premar.coronavirusapp.view.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.premar.coronavirusapp.R;
import com.premar.coronavirusapp.data.api.ApiClient;
import com.premar.coronavirusapp.data.api.ApiService;
import com.premar.coronavirusapp.model.CoronaCountry;
import com.premar.coronavirusapp.model.CountryInfo;
import com.premar.coronavirusapp.model.Covid;
import com.premar.coronavirusapp.view.ui.PreventionActivity;
import com.premar.coronavirusapp.view.ui.SymptomsActivity;
import com.premar.coronavirusapp.view.ui.TreatmentActivity;
import com.premar.coronavirusapp.view.ui.countries.CountriesFragment;
import com.premar.coronavirusapp.viewmodel.CovidGlobalViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.premar.coronavirusapp.Utils.Constants.NIGERIA;
import static com.premar.coronavirusapp.Utils.Constants.UGANDA;
import static com.premar.coronavirusapp.Utils.Constants.formatNumber;

public class HomeFragment extends Fragment {
    public static String countryInfoSize;

    private TextView tvCases, tvDeaths, tvRecovered, ugCases, ugDeaths, ugRecovered, ugCasesToday, ugDeathsToday, moreCountries;
    private ImageView ugandaFlag;
    private TextView countryName;
    private CardView symptomCard, treatmentCard, faqCard, preventionCard;
    private HomeViewModel viewModel;
    private CountriesFragment countriesFragment;
    private FragmentManager fragmentManager;
    private Covid covid;
    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //reference
        tvCases = root.findViewById(R.id.cases);
        tvDeaths = root.findViewById(R.id.deaths);
        tvRecovered = root.findViewById(R.id.recovered);
        ugCases = root.findViewById(R.id.uganda_cases);
        ugDeaths = root.findViewById(R.id.uganda_deaths);
        ugRecovered = root.findViewById(R.id.uganda_recovered);
        moreCountries = root.findViewById(R.id.more_countries);
        ugCasesToday = root.findViewById(R.id.uganda_cases_today);
        ugDeathsToday = root.findViewById(R.id.uganda_deaths_today);
        ugandaFlag = root.findViewById(R.id.uganda_flag);
        symptomCard = root.findViewById(R.id.symptom_card);
        treatmentCard = root.findViewById(R.id.treatment_card);
        faqCard = root.findViewById(R.id.faq_card);
        preventionCard = root.findViewById(R.id.prevention_card);
        countryName = root.findViewById(R.id.country_name_status);

        moreCountries.setOnClickListener(v -> openCountryFragement());
        symptomCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), SymptomsActivity.class)));
        treatmentCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), TreatmentActivity.class)));
        preventionCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), PreventionActivity.class)));

        HomeViewModelFactory factory = new HomeViewModelFactory(this.getActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
        getStats();
        getUgandaCoronaStats();

        return root;

    }

    private void openCountryFragement() {
        countriesFragment = new CountriesFragment();
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, countriesFragment);
        transaction.commit();
    }



    private void populateStats(Covid covid) {

        tvCases.setText(formatNumber(covid.getCases()));
        tvDeaths.setText(formatNumber(covid.getDeaths()));
        tvRecovered.setText(formatNumber(covid.getRecovered()));
    }

    private void ugandaStats(CoronaCountry country){
        CountryInfo countryInfo = country.getCountryInfo();
        Picasso.get()
                .load(countryInfo.getFlag())
                .placeholder(R.drawable.ic_flag)
                .error(R.drawable.ic_flag)
                .into(ugandaFlag);
        ugCases.setText(formatNumber(country.getCases()));
        ugDeaths.setText(formatNumber(country.getDeaths()));
        ugRecovered.setText(formatNumber(country.getRecovered()));

        Resources res = getResources();

        countryName.setText(String.format(res.getString(R.string.country_corona_status), country.getCountry()));

        ugCasesToday.setText(String.format(res.getString(R.string.today), country.getTodayCases()));
        ugDeathsToday.setText(String.format(res.getString(R.string.today), country.getTodayDeaths()));
    }

    private void getStats(){
        ApiService service = ApiClient.getApiService(ApiService.class);
        Call<Covid> call = service.getWorldStats();
        call.enqueue(new Callback<Covid>() {
            @Override
            public void onResponse(Call<Covid> call, Response<Covid> response) {
                if (response.isSuccessful()){
                    Covid covids = response.body();
                    Log.d(TAG, "Covid cases: "+covids.getCases());
                    populateStats(covids);
                }
            }

            @Override
            public void onFailure(Call<Covid> call, Throwable t) {
                Log.e(TAG, "onFailure: ",t );
            }
        });
    }

    private void getUgandaCoronaStats(){
        ApiService service = ApiClient.getApiService(ApiService.class);
        Call<CoronaCountry> call = service.getOneCountry(NIGERIA);
        call.enqueue(new Callback<CoronaCountry>() {
            @Override
            public void onResponse(Call<CoronaCountry> call, Response<CoronaCountry> response) {
                if (response.isSuccessful()){
                    CoronaCountry country = response.body();
                    if (country != null) {
                        ugandaStats(country);
                    }
                }
            }

            @Override
            public void onFailure(Call<CoronaCountry> call, Throwable t) {
                Log.e(TAG, "onFailure: ",t );
            }
        });

    }
}
