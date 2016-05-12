package com.example.bassemsarhan.mai_p_app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class trailerActivityFragment extends Fragment {
    private ArrayAdapter<String> mAdapter;
    private final String LOG_TAG = trailerActivityFragment.class.getSimpleName();
    public trailerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_trailer, container, false);
        ArrayList<String> trailers=new ArrayList<>();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            trailers.clear();
            trailers = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
        }

        for (int i=0;i<trailers.size();i++){
            trailers.set(i,"http://www.youtube.com/watch?v="+trailers.get(i));
        }

        mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.trailers,
                R.id.text, trailers);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_trailers);
        listView.setAdapter(mAdapter);


       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Context context = getActivity();
                String trailer = mAdapter.getItem(position);
                trailer=trailer.substring(31,trailer.length());

                try {
                   //
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "http://www.youtube.com/watch?v=" + trailer));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(trailer));
                    startActivity(intent);
                }

            }
        }
       );
        return  rootView;
    }

}
