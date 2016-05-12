package com.example.bassemsarhan.mai_p_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static int size = 0;
    private GridViewAdapter img;
    final static String dilm="-_-";
    public static ArrayList<String> id = new ArrayList<>();
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String detail);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    public MainActivityFragment() {
    }

    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        ArrayList<String> p = new ArrayList<>();

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        img = new GridViewAdapter(getActivity(), p);
        gridView.setAdapter(img);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String cursor =  adapterView.getItemAtPosition(position).toString();

                if (cursor != null) {
                    Context context = getActivity();
                    String detail = img.getItem(position);

                    for (int i=0;i<id.size();i++){
                        String[] s=id.get(i).split(dilm);
                        if (detail.equals(s[0])){
                            detail=id.get(i);
                            Log.e(LOG_TAG, detail);
                        }

                    }
                    ((Callback) getActivity())
                            .onItemSelected(detail);
                }

            }
        });
        return rootView;
    }




    private void updateMovies() {
        FetchMovies movieTask = new FetchMovies();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        movieTask.execute(sort);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMovies extends AsyncTask<String, String[], ArrayList<String>> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();


        private ArrayList<String> getImgPath(String img)
                throws JSONException {

            JSONObject jsonObj = new JSONObject(img);

            JSONArray arr = jsonObj.getJSONArray("results");

            size = arr.length();
            ArrayList<String> path = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject pathJson = arr.getJSONObject(i);
                String overview = pathJson.getString("overview");
                String releaseDate = pathJson.getString("release_date");
                String originalTitle = pathJson.getString("original_title");
                String voteAvg = pathJson.getString("vote_average");
                String idd = pathJson.getString("id");
                String poster="http://image.tmdb.org/t/p/w185/" + pathJson.getString("poster_path");
                id.add(poster + dilm + idd + dilm + originalTitle
                        + dilm + overview + dilm + releaseDate + dilm + voteAvg );
                path.add(poster);

            }
            return path;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String img = null;
           // String type = "popularity.desc";
            String API = "907cc1cee1a92ee5b70dfc2b446b8793";

            if (params[0].equals("favourites") == false) {
                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are available at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast

                    final String IMG_BASE_URL = "http://api.themoviedb.org/3/movie/" +  params[0] +"?";
                  //  final String Type_Param = "sort_by";
                    final String API_Param = "api_key";

                    Uri builUri = Uri.parse(IMG_BASE_URL).buildUpon()//.appendQueryParameter(Type_Param, params[0])
                            .appendQueryParameter(API_Param, API)
                            .build();

                    URL url = new URL(builUri.toString());
                    //  URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=48f656695739b4bf7ddae4995170b9b3");

                    Log.v(LOG_TAG, "Built URI " + builUri.toString());

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        img = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        img = null;
                    }
                    img = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attempting
                    // to parse it.
                    img = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
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
                    ArrayList<String> path = null;


                    // u.setP(getImgPath(img));
                    return getImgPath(img);

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }else{
                ArrayList<String>u=new ArrayList<>();
              //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            //    SharedPreferences prefs = getActivity().getPreferences(Context.MODE_WORLD_READABLE) ;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
              //  String s=prefs.getString("The Revenant","d");
                Map<String,?> urls= prefs.getAll();

                for (Map.Entry<String, ?> entry : urls.entrySet()) {
                    if(entry.getKey().startsWith(MovieDetailsFragment.FAV_MOVIES_PREFIX)) {
                        u.add(entry.getValue().toString());
                        Log.e(LOG_TAG, entry.getValue().toString());
                    }
                }
                return u;
            }


            return null;
        }

        protected void onPostExecute(ArrayList<String> path) {
            if (path != null) {
                img.clear();
                img.addAll(path);
            }
        }

    }
}
