package com.carbon.model;

import java.time.LocalDateTime;

public interface DataForAnalytics{
    Integer getPoints();
    String getTaxonomy();
    LocalDateTime getSubmittedAt();
}