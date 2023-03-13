package com.viavoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.Locale;

public class TutorialsFragment extends Fragment {

    WebView tutorial;

    public TutorialsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(){
        return new TutorialsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.menu_guides);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorials, container, false);
        tutorial = view.findViewById(R.id.tutorial);
        tutorial.getSettings().setJavaScriptEnabled(true);
        createContent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.menu_guides);
    }

    private void createContent(){
        tutorial.loadUrl("file:///android_asset/index.html");
    }
}
