package com.graso.anitrack.importer.store;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.graso.anitrack.importer.model.ImportJob;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ImportJobStore {

    private final Cache<String, ImportJob> jobs = Caffeine.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .maximumSize(200)
            .build();

    public void put(ImportJob job) {
        jobs.put(job.getJobId(), job);
    }

    public ImportJob get(String jobId) {
        return jobs.getIfPresent(jobId);
    }

    public void remove(String jobId) {
        jobs.invalidate(jobId);
    }
}
