package com.example.a1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);

        // Find the TextView
        TextView helpTextView = rootView.findViewById(R.id.Help_text);

        // Get the help text content from strings.xml
        String helpTextContent = getString(R.string.help_text);

        // Set the HTML content to the TextView
        helpTextView.setText(Html.fromHtml(helpTextContent, Html.FROM_HTML_MODE_LEGACY));

        return rootView;
    }
}
