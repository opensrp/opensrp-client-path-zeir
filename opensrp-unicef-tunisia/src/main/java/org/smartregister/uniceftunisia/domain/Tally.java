package org.smartregister.uniceftunisia.domain;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class Tally implements Serializable {

    private String indicator;
    @JsonProperty
    private long id;
    @JsonProperty
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
