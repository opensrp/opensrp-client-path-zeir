package org.smartregister.uniceftunisia.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class MonthlyTally extends Tally implements Serializable {
    @JsonProperty
    private Date dateSent;
    @JsonProperty
    private Date month;
    @JsonProperty
    private boolean edited;
    @JsonProperty
    private String providerId;

    @JsonProperty
    private Date updatedAt;

    @JsonProperty
    private String grouping;


    private Hia2Indicator hia2Indicator;
    private Tally indicatorTally;


    @JsonProperty
    private Date createdAt;

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public Hia2Indicator getHia2Indicator() {
        return hia2Indicator;
    }

    public void setHia2Indicator(Hia2Indicator hia2Indicator) {
        this.hia2Indicator = hia2Indicator;
    }

    public Tally getIndicatorTally() {
        return indicatorTally;
    }

    public void setIndicatorTally(Tally indicatorTally) {
        this.indicatorTally = indicatorTally;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }
}