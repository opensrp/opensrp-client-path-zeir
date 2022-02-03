package org.smartregister.path.reporting.monthly.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class Hia2Indicator implements Serializable {
    private long id;
    @JsonProperty
    private String indicatorCode;
    @JsonProperty
    private String label;
    @JsonProperty
    private String dhisId;
    @JsonProperty
    private String categoryOptionCombo;
    @JsonProperty
    private String description;
    @JsonProperty
    private String category;
    private String grouping;
    private Date createdAt;
    private Date updatedAt;

    public Hia2Indicator() {
        this.category = "Immunization";
    }

    public Hia2Indicator(long id, String indicatorCode, String description) {
        this.id = id;
        this.indicatorCode = indicatorCode;
        this.description = description;
        this.category = "Immunization";
    }

    public Hia2Indicator(long id, String indicatorCode, String label, String dhisId, String description, String category, Date createdAt, Date updatedAt) {
        this.id = id;
        this.indicatorCode = indicatorCode;
        this.label = label;
        this.dhisId = dhisId;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getCategoryOptionCombo() {
        return categoryOptionCombo;
    }

    public void setCategoryOptionCombo(String categoryOptionCombo) {
        this.categoryOptionCombo = categoryOptionCombo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDhisId() {
        return dhisId;
    }

    public void setDhisId(String dhisId) {
        this.dhisId = dhisId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("indicatorCode", indicatorCode);
        object.put("label", label);
        object.put("dhisId", dhisId);
        object.put("description", description);
        object.put("category", category);

        return object;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }
}