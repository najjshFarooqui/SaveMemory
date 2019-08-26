package com.smnetinfo.savememory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smnetinfo.savememory.AddWillActivity;
import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.adapter.WillListRecyclerAdapter;
import com.smnetinfo.savememory.database.UserDataSource;
import com.smnetinfo.savememory.extras.ProgressDialog;
import com.smnetinfo.savememory.extras.WebConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WillFragment extends Fragment implements WebConstants {

    CardView fragmentWillAddCV;
    TextView fragmentWillEmptyTV;
    RecyclerView fragmentWillRecyclerView;

    ProgressDialog progressDialog;
    UserDataSource userDataSource;

    WillListRecyclerAdapter willListRecyclerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_will, container, false);

        progressDialog = new ProgressDialog(getContext());
        userDataSource = UserDataSource.sharedInstance(getContext());

        fragmentWillAddCV = view.findViewById(R.id.fragmentWillAddCV);

        fragmentWillEmptyTV = view.findViewById(R.id.fragmentWillEmptyTV);

        fragmentWillRecyclerView = view.findViewById(R.id.fragmentWillRecyclerView);

        willListRecyclerAdapter = new WillListRecyclerAdapter(getContext(), new JSONArray());
        fragmentWillRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fragmentWillRecyclerView.setAdapter(willListRecyclerAdapter);

        fragmentWillAddCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddWillActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getWillsApi();
    }

    private void getWillsApi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_WILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("API-Posts", response);
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(STATUS)) {
                                    if (jsonObject.getJSONArray(DATA).length() > 0) {
                                        fragmentWillEmptyTV.setVisibility(View.GONE);
                                        willListRecyclerAdapter.notifyList(jsonObject.getJSONArray(DATA));
                                    } else {
                                        fragmentWillEmptyTV.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(USER_ID, userDataSource.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
