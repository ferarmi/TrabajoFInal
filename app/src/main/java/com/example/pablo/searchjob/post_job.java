package com.example.pablo.searchjob;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by DOCENTES on 22/10/2015.
 */
public class post_job extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private TextView JTitle, JDescription, JNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_job);
        JTitle = (TextView) findViewById(R.id.et_title);
        JDescription = (TextView) findViewById(R.id.et_description);
        JNumbers = (TextView) findViewById(R.id.et_contacts);
    }

    public void postJob(View view) {
        NetworkOperationSaveAsyncTask asyncTask = new NetworkOperationSaveAsyncTask(JTitle.getText().toString(),JDescription.getText().toString(),JNumbers.getText().toString());
        asyncTask.execute();

    }


    private class NetworkOperationSaveAsyncTask extends AsyncTask<Void, Void, Void> {
        private String _Title,_Description,_Contacts;

        public NetworkOperationSaveAsyncTask(String Title,String Description,String Contacts) {
            _Title=Title;
            _Description=Description;
            _Contacts=Contacts;
        }

        @Override

        protected void onPostExecute(Void aVoid) {
            Toast.makeText(post_job.this, R.string.endsave, Toast.LENGTH_SHORT).show();
            post_job.this.finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // This is my URL: http://dipandroid-ucb.herokuapp.com/work_posts.json
            HttpURLConnection urlConnection = null; // HttpURLConnection Object

            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon() // Build the URL using the Uri class
                    .appendPath("work_posts.json").build();
            try {
                URL url = new URL(buildUri.toString()); // Create a new URL



                urlConnection = (HttpURLConnection) url.openConnection(); // Get a HTTP connection

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                urlConnection.connect();

                String [] Contacts_string= _Contacts.split("\n");
                String Result="";

                for(int x=0; x < Contacts_string.length;x++)
                {
                    Result+="\"" +Contacts_string[x]+"\",";
                }

                Result = Result.substring(0,Result.length()-1);


                String Sentence = "{\"work_post\":{\"title\":\"" + _Title + "\",\"description\":\"" + _Description + "\",\"contacts\":[" + Result + "]}}";
                Log.i("Esto", Result);

                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                os.write(Sentence.getBytes());
                os.flush();
                os.close();

                InputStream input = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Log.d("doInBackground(Resp)", result.toString());
                JSONObject response = new JSONObject(result.toString());

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }





            return null;
        }


    }
}
