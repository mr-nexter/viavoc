package com.viavoc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportFragment extends Fragment {

    public static final String TAG = "ReportFragment";

    private FrameLayout reportDrawer;
    private EditText problemDescription;
    private TextView requiredDescription;
    private ImageButton sendButton;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public ReportFragment() {
        // Required empty public constructor
    }

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReportFragment.
     */
    public static ReportFragment newInstance() {
        ReportFragment fragment = new ReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        reportDrawer = view.findViewById(R.id.report_drawer);
        problemDescription = (EditText) view.findViewById(R.id.problemDescription);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String errors = sharedPref.getString(Utils.ERROR_QUEUE, "");
        if (!errors.isEmpty()) {
            problemDescription.setText(getString(R.string.hello)
                                + "\n" + getString(R.string.bug_found) + ":\n"
                                + errors + ".\n" + getString(R.string.inform_me));
        } else {
            problemDescription.setText(errors);
        }
        requiredDescription = (TextView) view.findViewById(R.id.requiredDescription);
        problemDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = problemDescription.getText().toString();
                if (!text.isEmpty()) {
                    if (requiredDescription.getVisibility() == View.VISIBLE) {
                        requiredDescription.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendButton = (ImageButton) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFields()) {
                    if (problemDescription.getText().toString().isEmpty()) {
                        requiredDescription.setText(R.string.required_field);
                        requiredDescription.setVisibility(View.VISIBLE);
                        TransitionManager.beginDelayedTransition(reportDrawer);
                    }
                } else {
                    if (requiredDescription.getVisibility() == View.VISIBLE) requiredDescription.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(reportDrawer);

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"viavoc.developers@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Some bug is found");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        Log.i("Report", "Finished sending email...");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.menu_report);
    }

    private boolean validateFields() {
        if (problemDescription.getText().toString().isEmpty()) return false;

        return true;
    }
}
