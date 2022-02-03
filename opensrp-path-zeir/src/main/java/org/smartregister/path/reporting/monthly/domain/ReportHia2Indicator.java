package org.smartregister.path.reporting.monthly.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class ReportHia2Indicator implements Serializable {

    @JsonProperty
    private String indicatorCode;

    @JsonProperty
    private String description;

    @JsonProperty
    private String category;

    @JsonProperty
    private String categoryOptionCombo;

    @JsonProperty
    private String value;

    @JsonProperty
    private String dhisId;


    public ReportHia2Indicator() {
    }

    public ReportHia2Indicator(String indicatorCode, String description, String category, String value) {
        this.indicatorCode = indicatorCode;
        this.description = description;
        this.category = category;
        this.value = value;
    }

    public String getCategoryOptionCombo() {
        return categoryOptionCombo;
    }

    public void setCategoryOptionCombo(String categoryOptionCombo) {
        this.categoryOptionCombo = categoryOptionCombo;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public void setHia2Indicator(Hia2Indicator hia2Indicator) {
        if (hia2Indicator != null) {
            this.indicatorCode = hia2Indicator.getIndicatorCode();
            this.description = hia2Indicator.getDescription();
            this.category = hia2Indicator.getCategory();
        }
    }

    public String getDhisId() {
        return dhisId;
    }

    public void setDhisId(String dhisId) {
        this.dhisId = dhisId;
    }
}