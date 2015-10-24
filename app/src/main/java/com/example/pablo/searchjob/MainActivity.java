package com.example.pablo.searchjob;

import android.content.ContentValues;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pablo.searchjob.data.JobPostDbHelper;

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

import static com.example.pablo.searchjob.data.JobPostDbContract.*;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private itemsAdapter arrayAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ArrayList<job> hola= new ArrayList<>();
        arrayAdapter = new itemsAdapter(this, hola);

        listView = (ListView)findViewById(R.id.job_post_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(MainActivity.this,job_detail.class);
                Bundle bolsa=new Bundle();
                    bolsa.putInt("id", ((job)arrayAdapter.getItem(position)).getId());
                    bolsa.putString("title", ((job) arrayAdapter.getItem(position)).getTitle());
                    bolsa.putString("description", ((job)arrayAdapter.getItem(position)).getDescription());
                    bolsa.putString("posted_date", ((job) arrayAdapter.getItem(position)).getPosted_date());
                intento.putExtras(bolsa);
                startActivity(intento);
            }
        });
        LoadDBAsyncTask asyncTask = new LoadDBAsyncTask();
        asyncTask.execute();

    }

    /*public void syncAction(View view) {
        NetworkOperationAsyncTask asyncTask = new NetworkOperationAsyncTask();
        asyncTask.execute();
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_sync:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                NetworkOperationAsyncTask asyncTask = new NetworkOperationAsyncTask();
                asyncTask.execute();
                Toast.makeText(MainActivity.this, R.string.synchronizing, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_post_job:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Intent intento = new Intent(MainActivity.this,post_job.class);

                startActivity(intento);
                return true;
        }
        return false;
    }

    private class NetworkOperationAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // This is my URL: http://dipandroid-ucb.herokuapp.com/work_posts.json
            HttpURLConnection urlConnection = null; // HttpURLConnection Object
            BufferedReader reader = null; // A BufferedReader in order to read the data as a file
            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon() // Build the URL using the Uri class
                    .appendPath("work_posts.json").build();
            try {
                URL url = new URL(buildUri.toString()); // Create a new URL

                urlConnection = (HttpURLConnection) url.openConnection(); // Get a HTTP connection
                urlConnection.setRequestMethod("GET"); // I'm using GET to query the server
                urlConnection.addRequestProperty("Content-Type", "application/json"); // The MIME type is JSON

                urlConnection.connect(); // Connect!! to the cloud!!!

                // Methods in order to read a text file (In this case the query from the server)
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                // Save the data in a String
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                saveJSONToDatabase(buffer.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }

                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
            return null;
        }

        private void saveJSONToDatabase(String json) throws JSONException   {
            JSONArray array = new JSONArray(json);
            JobPostDbHelper dbHelper = new JobPostDbHelper(MainActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (int i = 0; i < array.length(); i++)
            {
                JSONObject jobPostJSON = array.getJSONObject(i);
                int id = jobPostJSON.getInt("id");
                String title = jobPostJSON.getString("title");
                String description = jobPostJSON.getString("description");
                String postedDate = jobPostJSON.getString("posted_date");
                //String contacts = jobPostJSON.getString("contacts");

                JSONArray contacts = jobPostJSON.getJSONArray("contacts");


                ContentValues contentValues = new ContentValues();

                contentValues.put(JobEntry._ID, id);
                contentValues.put(JobEntry.COLUMN_TITLE, title);
                contentValues.put(JobEntry.COLUMN_DESCRIPTION, description);
                contentValues.put(JobEntry.COLUMN_POSTED_DATE, postedDate);
                db.insert(JobEntry.TABLE_NAME, null, contentValues);


                if (contacts != null) {
                    for (int k = 0; k < contacts.length(); k++) {
                        String elem = contacts.getString(k);
                        if (elem != null) {
                            try {
                                ContentValues contentValuesContacts = new ContentValues();


                                contentValuesContacts.put(ContactEntry.COLUMN_JOB_ID, id);
                                contentValuesContacts.put(ContactEntry.COLUMN_NUMBER, elem);

                                db.insert(ContactEntry.TABLE_NAME, null, contentValuesContacts);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

            db.close();
            dbHelper.close();
            LoadDBAsyncTask asyncTask = new LoadDBAsyncTask();
            asyncTask.execute();
        }
    }

    private class LoadDBAsyncTask extends AsyncTask<Void, Void, ArrayList<job>> {

        @Override
        protected ArrayList<job> doInBackground(Void... params) {
            ArrayList<job> result = new ArrayList<>();
            JobPostDbHelper dbHelper = new JobPostDbHelper(MainActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(JobEntry.TABLE_NAME,
                                new String[] {JobEntry._ID,JobEntry.COLUMN_TITLE,JobEntry.COLUMN_DESCRIPTION, JobEntry.COLUMN_POSTED_DATE},
                                null, null, null, null, JobEntry._ID + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    job Job = new job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
                    result.add(Job);
                } while (cursor.moveToNext());
            }

            cursor.close();



            cursor.close();

            db.close();
            dbHelper.close();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<job> jobs) {
            arrayAdapter.clear();
            for (job Job : jobs) {
                arrayAdapter.add(Job);
            }
            listView.setAdapter(arrayAdapter);
            Toast.makeText(MainActivity.this, R.string.endsynchronizing, Toast.LENGTH_SHORT).show();
        }
    }
}
