package dutta.sayon.com.irrigationscheduling;


import android.os.AsyncTask;
import android.widget.Toast;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by rocky on 10/4/15.
 */
public class NotificationService extends JobService{

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new DailyTask(this).execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private static class DailyTask extends AsyncTask<JobParameters, Void, JobParameters> {

        NotificationService notificationService;
        public DailyTask(NotificationService notificationService) {
            this.notificationService = notificationService;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            return null;
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            notificationService.jobFinished(jobParameters, false);
        }
    }
}
