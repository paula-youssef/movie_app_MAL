package com.example.bassemsarhan.mai_p_app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {
    View rootView;
    static String id="";
    static String url="";
    static String title="";
    private ArrayAdapter<String> mAdapter;
    static ArrayList<String> key = new ArrayList<>();
    ImageView imageview;
    private Context context;
    static String author = "", content = "";
    static ArrayList<String> rev = new ArrayList<>();
    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String temp = intent.getStringExtra(Intent.EXTRA_TEXT);
            GetDetials d = new GetDetials();
            String[] s=temp.split("-_-");
            id=s[1];

            d.execute(id);
        }else{
            Bundle arguments = getArguments();
            Log.e(LOG_TAG, "d");
            if (arguments != null) {
                String forecastStr1 = arguments.get("data").toString();
                Log.e(LOG_TAG, forecastStr1);
                String[] s = forecastStr1.split("-_-");
                id = s[1];
                GetDetials d = new GetDetials();
                d.execute(id);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {

            String  forecastStr1 = arguments.get("data").toString();
            String[] s=forecastStr1.split("-_-");
            String forecastStr=s[0];
            id=s[1];
            url = forecastStr;
            Log.e(LOG_TAG, " asdasdasd :  " + forecastStr);
            imageview = (ImageView) rootView.findViewById(R.id.imageView);
            Picasso.with(context)
                    .load(forecastStr)
                    .into(imageview);

            ArrayList<String> d = new ArrayList<>();
            d.add(s[2]);
            d.add(s[3]);
            d.add(s[4]);
            d.add(s[5]);
            mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.infolist,
                    R.id.info, d);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_info);
            listView.setAdapter(mAdapter);

        }



        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String forecastStr1 = intent.getStringExtra(Intent.EXTRA_TEXT);
            String[] s=forecastStr1.split("-_-");
            String forecastStr=s[0];
            id=s[1];
            url = forecastStr;
            Log.e(LOG_TAG, " selected URL :  " + forecastStr);
            // String temp = forecastStr.substring(31, forecastStr.length());
            // Log.e(LOG_TAG, " new URL :  " + temp);

            imageview = (ImageView) rootView.findViewById(R.id.imageView);
            Picasso.with(context)
                    .load(forecastStr)
                    .into(imageview);
            //imageview.setVisibility(View.VISIBLE);

            ArrayList<String> d = new ArrayList<>();
            d.add(s[2]);
            d.add(s[3]);
            d.add(s[4]);
            d.add(s[5]);
            mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.infolist,
                    R.id.info, d);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_info);
            listView.setAdapter(mAdapter);

        }



            final Button button = (Button) rootView.findViewById(R.id.trailer);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openPreferedTrailer();
                }
            });

            final Button button2 = (Button) rootView.findViewById(R.id.reviews);
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openPreferedReviews();
                }
            });

            final Button button3 = (Button) rootView.findViewById(R.id.favourite);
            button3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    storedata();
                }
            });


        return rootView;
    }
    String c = "no";
   public void checkTable(String ch){
       c = ch;
   }
    private void openPreferedTrailer() {

        // if(c.equals("yes")){
        Intent intent = new Intent(getActivity(), trailerActivity.class)
                .putExtra(Intent.EXTRA_TEXT, key);
        startActivity(intent);
       // }else{
          ///   trailerActivityFragment trailerActivityFragment = new trailerActivityFragment();
           //  trailerActivityFragment.trailarKeyys(key);

        // }
    }

    private void openPreferedReviews() {

        try {

            Intent intent = new Intent(getActivity(), ReviewsActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, rev);
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {

        }
    }

    private void storedata(){
      //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_WORLD_READABLE);
        SharedPreferences sharedPref =PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FAV_MOVIES_PREFIX+title, url);
        editor.commit();


    }
    public class GetDetials extends AsyncTask<String, String[], ArrayList<String>> {
        private final String LOG_TAG = GetDetials.class.getSimpleName();


        private void getVideoKey(String path)
                throws JSONException {

            JSONObject jsonObj = new JSONObject(path);
            JSONArray arr = jsonObj.getJSONArray("results");

            key.clear();
            if (arr==null)
                key.add("");
            else {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject pathJson = arr.getJSONObject(i);
                    key.add(pathJson.getString("key"));

                }
            }
        }

        private void getReviews(String path)
                throws JSONException {

            JSONObject jsonObj = new JSONObject(path);
            JSONArray arr = jsonObj.getJSONArray("results");
            rev.clear();
            if (arr==null){
                rev.add("no reviews");
            }
            else {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject pathJson = arr.getJSONObject(i);

                    author = pathJson.getString("author");
                    content = pathJson.getString("content");
                    rev.add("author : " + author + " \ncontent : " + content + "\n \n");

                }
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

                HttpURLConnection urlConnection2 = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String key2 = null;
                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are available at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast  http://api.themoviedb.org/3/movie/281957/videos?api_key=907cc1cee1a92ee5b70dfc2b446b8793
                    String IMG_BASE_URL = "http://api.themoviedb.org/3/movie/";
                    IMG_BASE_URL += id + "/videos?";
                    //   final String Type_Param="sort_by";
                    final String API_Param = "api_key";
                    String API = "907cc1cee1a92ee5b70dfc2b446b8793";
                    Uri builUri2 = Uri.parse(IMG_BASE_URL).buildUpon().appendQueryParameter(API_Param, API)
                            .build();

                    URL url2 = new URL(builUri2.toString());
                    //  URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=48f656695739b4bf7ddae4995170b9b3");

                    Log.v(LOG_TAG, "Built URI " + builUri2.toString());
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection2 = (HttpURLConnection) url2.openConnection();
                    urlConnection2.setRequestMethod("GET");
                    urlConnection2.connect();

                    // Read the input stream into a String
                    InputStream inputStream2 = urlConnection2.getInputStream();
                    StringBuffer buffer2 = new StringBuffer();
                    if (inputStream2 == null) {
                        // Nothing to do.
                        key2 = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream2));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer2.append(line + "\n");
                    }

                    if (buffer2.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        key2 = null;
                    }
                    key2 = buffer2.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attempting
                    // to parse it.
                    key2 = null;
                } finally {
                    if (urlConnection2 != null) {
                        urlConnection2.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            try {
                getVideoKey(key2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////

                HttpURLConnection urlConnection3 = null;
                BufferedReader reader3 = null;

                // Will contain the raw JSON response as a string.
                String key3 = null;
                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are available at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast  http://api.themoviedb.org/3/movie/281957/videos?api_key=907cc1cee1a92ee5b70dfc2b446b8793
                    String IMG_BASE_URL = "http://api.themoviedb.org/3/movie/";
                    IMG_BASE_URL += id + "/reviews?";
                    //   final String Type_Param="sort_by";
                    final String API_Param = "api_key";
                    String API = "907cc1cee1a92ee5b70dfc2b446b8793";
                    Uri builUri2 = Uri.parse(IMG_BASE_URL).buildUpon().appendQueryParameter(API_Param, API)
                            .build();

                    URL url2 = new URL(builUri2.toString());
                    //  URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=48f656695739b4bf7ddae4995170b9b3");

                    Log.v(LOG_TAG, "Built URI " + builUri2.toString());
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection2 = (HttpURLConnection) url2.openConnection();
                    urlConnection2.setRequestMethod("GET");
                    urlConnection2.connect();

                    // Read the input stream into a String
                    InputStream inputStream3 = urlConnection2.getInputStream();
                    StringBuffer buffer3 = new StringBuffer();
                    if (inputStream3 == null) {
                        // Nothing to do.
                        key3 = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream3));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer3.append(line + "\n");
                    }

                    if (buffer3.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        key3 = null;
                    }
                    key3 = buffer3.toString();
                    Log.v(LOG_TAG, "img Json String:  " + key3);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attempting
                    // to parse it.
                    key2 = null;
                } finally {
                    if (urlConnection3 != null) {
                        urlConnection3.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            try {
                getReviews(key3);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


    }
public static final String FAV_MOVIES_PREFIX = "favmov$_" ;

}

