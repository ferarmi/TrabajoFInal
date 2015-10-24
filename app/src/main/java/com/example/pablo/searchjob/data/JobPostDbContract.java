package com.example.pablo.searchjob.data;

import android.provider.BaseColumns;

public class JobPostDbContract {
    public static class JobEntry implements BaseColumns {
        public static final String TABLE_NAME = "jobs";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_POSTED_DATE = "posted_date";
    }

    public static class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_JOB_ID = "job_id";
        public static final String COLUMN_NUMBER = "number";
    }
}
