package org.smartregister.uniceftunisia.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class Hia2Indicator implements Serializable {
    private long id;
    @JsonProperty
    private String indicatorCode;
    @JsonProperty
    private String description;
    @JsonProperty
    private String category;

    public Hia2Indicator() {
        this.category = "Immunization";
    }

    public Hia2Indicator(long id, String indicatorCode, String description) {
        this.id = id;
        this.indicatorCode = indicatorCode;
        this.description = description;
        this.category = "Immunization";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("indicatorCode", indicatorCode);
        object.put("description", description);
        object.put("category", "Immunization");

        return object;
    }
}