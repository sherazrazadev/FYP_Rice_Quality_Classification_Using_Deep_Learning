package com.example.a1;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PrivacyPolicyFragment extends Fragment {

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        // Find the TextView
        TextView privacyPolicyTextView = rootView.findViewById(R.id.privacy_policy_text);

        // Get the privacy policy content from strings.xml
        String privacyPolicyContent = getString(R.string.privacy_policy_content);

        // Set the HTML content to the TextView
        privacyPolicyTextView.setText(Html.fromHtml(privacyPolicyContent, Html.FROM_HTML_MODE_LEGACY));

        return rootView;
    }
}
