package com.carbon.model;

import java.time.LocalDateTime;

public interface DataForAnalytics{
    Double getCarbonSaved();
    String getTaxonomy();
    LocalDateTime getSubmittedAt();
}