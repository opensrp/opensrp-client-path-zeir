package org.smartregister.uniceftunisia.model;

import org.json.JSONObject;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.util.AppJsonFormUtils;

public class CheckMotherDetailsModel {
    private JSONObject client;
    private String motherBaseEntityId;
    private String motherFirstName;
    private String motherLastName;

    public CheckMotherDetailsModel(JSONObject client) {
        this.client = client;
    }

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public CheckMotherDetailsModel invoke() {
        motherBaseEntityId = "";
        motherFirstName = "";
        motherLastName = "";

        if (client.has(AppConstants.KEY.MOTHER)) {
            JSONObject mother = AppJsonFormUtils.getJsonObject(client, AppConstants.KEY.MOTHER);
            motherFirstName = AppJsonFormUtils.getJsonString(mother, AppConstants.KEY.FIRSTNAME);
            motherLastName = AppJsonFormUtils.getJsonString(mother, AppConstants.KEY.LASTNAME);
            motherBaseEntityId = AppJsonFormUtils.getJsonString(mother, AppConstants.KEY.BASE_ENTITY_ID);
        }
        return this;
    }
}
