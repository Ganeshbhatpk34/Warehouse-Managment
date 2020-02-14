package com.probatus.rhbus.warehouse.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.SearchView;
import android.widget.Spinner;

import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.probatus.rhbus.warehouse.Adapter.DataAdapter;
import com.probatus.rhbus.warehouse.Adapter.SimpleArrayListAdapter;
import com.probatus.rhbus.warehouse.Interface.ItemClickListener;
import com.probatus.rhbus.warehouse.Interface.productversion;
import com.probatus.rhbus.warehouse.JobViewActivity;
import com.probatus.rhbus.warehouse.JsonParse.HttpResponse;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.probatus.rhbus.warehouse.MyApplication;
import com.probatus.rhbus.warehouse.R;
import com.probatus.rhbus.warehouse.WMSPickCard;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;
import mehdi.sakout.fancybuttons.FancyButton;

import static java.lang.Boolean.TRUE;

/**
 * Created by ganapathi on 13/1/20.
 */

public class WMSPickListFragment extends Fragment
        implements ItemClickListener,SearchView.OnQueryTextListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshCollection;RecyclerView.LayoutManager layoutManager;
    LayoutAnimationController animation;
    RecyclerViewFastScroller recyclerViewFastScroller;
    FancyButton refresh;

    public productversion productversionsList;

    DataAdapter adapter;
    private SearchView editsearch;
    int  search=1,span=1;
    public static ArrayList<productversion> productversions= new ArrayList<>();
    public ArrayList<productversion> product_version = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.jobprocessrender, container, false);

        editsearch = (SearchView) view.findViewById(R.id.searchView);
        editsearch.setOnQueryTextListener(this);

        refreshCollection =(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        recyclerViewFastScroller = (RecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);

        recyclerViewFastScroller.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);

        refreshCollection =(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshCollection.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refreshPlease(true);
                }catch (Exception e){}
            }
        });

        FancyButton barcode = (FancyButton) view.findViewById(R.id.barcode_scanner);
        refresh = (FancyButton) view.findViewById(R.id.clear_button);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPlease(true);
            }
        });

        initViews();

        return view;
    }

    public void initViews(){
        if(productversions.size() > 0)
            productversions.clear();
        if(((JobViewActivity) getActivity()).checkConnection()) {
            prepareData();
        }
    }

    private ArrayList<productversion> prepareData(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(MyStatic.getTokenKey());
        product_version.clear();
        ((JobViewActivity) getActivity()).jsonResponse.JsonResponse(getActivity(), "wmsPickByLot_np", "getAllPick", list, new HttpResponse<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("result");
                    String[] suggestion = new String[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject c = array.getJSONObject(i);
                        productversion productversion = new productversion();
                        productversion.setProduct_itemcode(c.getString("DocumentNo"));

                        String code = isNullChecker(c.getString("ShiptoCode"));
                        String name = isNullChecker(c.getString("SelltoCustomerName"));
                        String address = isNullChecker(c.getString("SourceNo"));
                        String location = isNullChecker(c.getString("LocationCode"));
                        String address2 = isNullChecker(c.getString("ShiptoAddress2"));

                        productversion.setAndroid_version_name(name);
                        productversion.setProduct_description(c.getString("DocumentNo") + name + address + address2);
                        suggestion[i] = c.getString("DocumentNo");
                        productversion.setProduct_lineno(c.getString("ItemCount"));
                        productversion.setProduct_brand(c.getString("DocumentDate"));
                        productversion.setProduct_category(address + " Location :" + location);
                        productversion.setProduct_subcategory(address2);
                        productversion.setAndroid_image_url(null);
                        product_version.add(productversion);
                    }
                    ((JobViewActivity) getActivity()).setSearchViewShown(suggestion);
                    setting();
                }catch (Exception e) {
                    MDToast.makeText(getActivity(), "" + e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });
        return product_version;
    }

    public String isNullChecker(String userEmail){
        if(userEmail != null && (!TextUtils.equals(userEmail ,"null")) && (!TextUtils.isEmpty(userEmail))){
            return userEmail;
        } else {
            return "-";
        }
    }

    @Override
    public void onClick(final View view, int posi) {
        productversions.get(posi).setSelectedFlag(TRUE);
        productversionsList = product_version.get(posi);
        MyApplication.getInstance().setDocumentNumber(productversionsList.getProduct_itemcode());
        MyStatic.setProdProcess(productversionsList.getProduct_description());
        MyStatic.setLineNumber(productversionsList.getProduct_barcode());
        ((JobViewActivity) getActivity()).transferToNextForPick();
    }

    private void setting(){
        productversions = product_version;
        adapter = new DataAdapter(getActivity(), productversions);
        layoutManager = new GridLayoutManager(getActivity(),span);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(animation);
        adapter.setClickListener(this);
        this.refreshPlease(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(adapter != null)
            adapter.filter(query,search);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(adapter != null)
            adapter.filter(newText,search);
        return false;
    }

    public void onSearch(String query){
        if(adapter != null)
            adapter.filter(query,search);
    }

    public void refreshPlease(boolean isrefresh){
        refreshCollection.post(new Runnable() {
            @Override public void run() {
                refreshCollection.setRefreshing(true);
            }
        });
        if(isrefresh){
            initViews();
        }
        if(productversions.size() > 0) {
            ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < productversions.size(); i++) {
                String name = productversions.get(i).getProduct_subcategory();
                if (name == null || name.trim().isEmpty())
                    continue;

                String word = name.substring(0, 1);
                if (!strAlphabets.contains(word)) {
                    strAlphabets.add(word);
                    mAlphabetItems.add(new AlphabetItem(i, word, false));
                }
            }
            recyclerViewFastScroller.setRecyclerView(recyclerView);
            recyclerViewFastScroller.setUpAlphabet(mAlphabetItems);
        }
        adapter.notifyDataSetChanged();
        onItemsLoadComplete(refreshCollection);
    }

    private void onItemsLoadComplete(SwipeRefreshLayout rf) {
        refreshCollection.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshCollection.setRefreshing(false);
            }
        },300);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.UP, enter, 500);
        } else {
            return CubeAnimation.create(CubeAnimation.UP, enter, 500);
        }
    }
}

