package com.example.pablo.searchjob;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.pablo.searchjob.data.JobPostDbContract;
import com.example.pablo.searchjob.data.JobPostDbHelper;

import java.util.ArrayList;

/**
 * Created by DOCENTES on 20/10/2015.
 */
public class job_detail extends AppCompatActivity{

    private TextView JTitle,JDescription,JNumbers;
    private int JId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_detail);

        JTitle = (TextView)findViewById(R.id.j_title);
        JDescription = (TextView)findViewById(R.id.j_description);
        JNumbers = (TextView)findViewById(R.id.j_contact);


        Bundle bolsa=getIntent().getExtras();
        getSupportActionBar().setTitle(bolsa.getString("title"));
        JId=bolsa.getInt("id");
        JTitle.setText(bolsa.getString("title"));
        JDescription.setText(bolsa.getString("description"));

        LoadDBAsyncTask2 asyncTask = new LoadDBAsyncTask2();
        asyncTask.execute();
    }

    private class LoadDBAsyncTask2 extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> result = new ArrayList<>();
            JobPostDbHelper dbHelper = new JobPostDbHelper(job_detail.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(JobPostDbContract.ContactEntry.TABLE_NAME,
                    new String[] {JobPostDbContract.ContactEntry.COLUMN_NUMBER},
                    JobPostDbContract.ContactEntry.COLUMN_JOB_ID + "='" + JId + "'" , null, null, null, JobPostDbContract.ContactEntry.COLUMN_NUMBER + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    String Number = cursor.getString(0);
                    result.add(Number);
                    Log.i("a",Number);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            dbHelper.close();
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> numbers) {
            String a="";
            for (String number : numbers) {
                a+=number + "\n";
            }

            JNumbers.setText(a);
        }
    }
}
