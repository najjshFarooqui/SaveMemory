package com.smnetinfo.savememory.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smnetinfo.savememory.R;
import com.smnetinfo.savememory.extras.WebConstants;

public class OnBoard2Fragment extends Fragment implements WebConstants {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboard2, container, false);

        return view;
    }


}
