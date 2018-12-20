package com.library.apple.food;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class HomeFragment extends Fragment {

    View myView;

    RelativeLayout relativeLayout;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private List<Res> resList;
    private List<Category> cateList;

    String auth_token;

    private ShimmerFrameLayout mShimmerViewContainer;

    private String res_url = "https://www.hungermela.com/api/v1/restaurants/";


    RecyclerView recyclerView_cat;
    RecyclerView.Adapter adapter_cat;

    ImageView burger12,snack12;
    TextView explore_cate12,explore_rest12;

//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);

 //   }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_home,container,false);

        mShimmerViewContainer = myView.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();

        burger12 = (ImageView)myView.findViewById(R.id.leftye);
        snack12 = (ImageView)myView.findViewById(R.id.leftye1);
        explore_cate12 = (TextView)myView.findViewById(R.id.explore_cate);
        explore_rest12 = (TextView)myView.findViewById(R.id.explore_rest);


        burger12.setVisibility(View.GONE);
        snack12.setVisibility(View.GONE);
        explore_cate12.setVisibility(View.GONE);
        explore_rest12.setVisibility(View.GONE);













//        Toolbar toolbar = (Toolbar)myView.findViewById(R.id.toolbar_home);
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(toolbar);

        auth_token = getActivity().getIntent().getStringExtra("token");



        resList = new ArrayList<>();

        recyclerView = (RecyclerView)myView.findViewById(R.id.rv_res_name);
        adapter = new ResAdapter(getActivity(),resList,auth_token);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);


        cateList = new ArrayList<>();

        recyclerView_cat = (RecyclerView)myView.findViewById(R.id.rv_home_cate);
        adapter_cat = new categoryAdapter(getActivity(),cateList);

        recyclerView_cat.setAdapter(adapter_cat);

        recyclerView_cat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView_cat.setVisibility(View.GONE);


        cateData();

        getData();




        return myView;

    }


    private void cateData(){

        String cate_url = "https://www.hungermela.com/api/v1/categories/";

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(cate_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length(); i++){

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Category category = new Category();

                        category.setCate_id(jsonObject.getString("id"));
                        category.setCate_name(jsonObject.getString("name"));
                        category.setCate_picture(jsonObject.getString("picture"));

                        cateList.add(category);


                        adapter_cat.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();

            }
        });

        queue.add(jsonArrayRequest1);




    }




    private void getData() {


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(res_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        final JSONObject jsonObject = response.getJSONObject(i);

                        final Res res = new Res();

                        res.setRes_id(jsonObject.getString("id"));

                        res.setRes_phone_number(jsonObject.getString("phone_number"));
                        res.setRes_line_1(jsonObject.getString("line_1"));
                        res.setRes_city(jsonObject.getString("mainlocation"));
                        res.setRes_zip_code(jsonObject.getString("zip_code"));
                        res.setRes_chain(jsonObject.getString("chain"));
                        res.setRes_part_of(jsonObject.getString("part_of"));



                        res.setRes_name(jsonObject.getString("name"));
                        String cat1 = jsonObject.getString("category");


                        //Json object request for category

                        String cat_url = "https://www.hungermela.com/api/v1/categories/"+cat1;


                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                                cat_url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response1) {
                                Log.d(TAG, response1.toString());

                                try {

                                    res.setRes_cat(response1.getString("name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                                Toast.makeText(getActivity(),
                                        error.getMessage(), Toast.LENGTH_SHORT).show();
                                // hide the progress dialog

                            }
                        });

                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                                30000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                        MySingleton1.getInstance(getActivity()).addToRequestQueue(jsonObjReq);













                        //Ends here




                        res.setRes_image(jsonObject.getString("image"));

                        res.setRes_loc(jsonObject.getString("city"));
                        res.setRes_open(jsonObject.getString("open_time"));
                        res.setRes_close(jsonObject.getString("close_time"));







                        resList.add(res);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                burger12.setVisibility(View.VISIBLE);
                snack12.setVisibility(View.VISIBLE);
                explore_cate12.setVisibility(View.VISIBLE);
                explore_rest12.setVisibility(View.VISIBLE);

                recyclerView_cat.setVisibility(View.VISIBLE);




                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

    }




//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_offers) {
//
//
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginToken", Context.MODE_PRIVATE);
//            String name = sharedPreferences.getString("id","error");
//            Toast.makeText(getActivity(),name,Toast.LENGTH_LONG).show();
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
