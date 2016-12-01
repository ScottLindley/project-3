package com.scottlindley.touchmelabs;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by Scott Lindley on 12/1/2016.
 */

public class WeatherService extends JobService{


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
