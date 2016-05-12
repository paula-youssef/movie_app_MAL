package com.example.bassemsarhan.mai_p_app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewsActivityFragment extends Fragment {
    private ArrayAdapter<String> mAdapter;
    public ReviewsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_reviews, container, false);
        ArrayList<String> reviews=new ArrayList<>();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            reviews.clear();
            reviews = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
        }

        mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.reviewslist,
                R.id.rev, reviews);

        ListView listView =(ListView) rootView.findViewById(R.id.listview_reviews);

        listView.setAdapter(mAdapter);
        return rootView;
    }
}
