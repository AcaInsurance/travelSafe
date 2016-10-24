package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aca.travelsafe.Adapter.CountryAdapter;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Widget.RecyclerViewWidget;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.Country_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChooseCountryFragment extends BaseFragment {
    @Bind(R.id.fast_scroller)
    VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.listMyDraft)
    RecyclerView listMyDraft;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.btnOk)
    Button btnOk;
    @Bind(R.id.viewButtonFooter)
    LinearLayout viewButtonFooter;

    private CountryAdapter adapter;
    private ChooseCountryFragmentListener mListener;
    private List<Country> countries;

    public ChooseCountryFragment() {
    }

    public static ChooseCountryFragment newInstance() {

        Bundle args = new Bundle();

        ChooseCountryFragment fragment = new ChooseCountryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_country, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Pick Destination");
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context = (Activity) context;
            this.mListener = (ChooseCountryFragmentListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_choose_country, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fillList(newText);
                adapter.notifyDataSetChanged();

                return false;
            }
        });


    }

    @Override
    protected void init(View view) {
        bindCountry();
    }

    @Override
    protected void registerListener() {
        listMyDraft.addOnScrollListener(fastScroller.getOnScrollListener());
    }

    private void fillList (String querySearch) {
        List<Country> countryList = new Select().from(Country.class)
                .where(Country_Table.CountryName.like("%"+querySearch+"%"))
                .orderBy(Country_Table.CountryName, true)
                .queryList();

        if (countries == null) {
            countries = new ArrayList<>();
        }

        countries.clear();
        countries.addAll(countryList);

        if (adapter != null) adapter.notifyDataSetChanged();

    }

    private void bindCountry() {
        try {
            fillList("");

            adapter = new CountryAdapter(context, countries);

            RecyclerViewWidget.createNoDivider(context, listMyDraft);
            fastScroller.setRecyclerView(listMyDraft);

//            listMyDraft.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
            listMyDraft.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnOk)
    public void performOK() {
        adapter.saveSPPA();
        mListener.performOK();
    }

    @OnClick(R.id.btnCancel)
    public void performCancel() {
        mListener.performCancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface ChooseCountryFragmentListener {
        public void performOK();

        public void performCancel();
    }
}
