package org.smartregister.uniceftunisia.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.uniceftunisia.util.AppConstants;
import org.smartregister.uniceftunisia.util.AppJsonFormUtils;

import java.util.Date;

public class CheckChildDetailsModel {
    private boolean myResult;
    private JSONObject client;
    private String entityId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob;
    private String zeirId;
    private String inactive;
    private String lostToFollowUp;

    public CheckChildDetailsModel(JSONObject client) {
        this.client = client;
    }

    public boolean is() {
        return myResult;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getZeirId() {
        return zeirId;
    }

    public String getInactive() {
        return inactive;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public CheckChildDetailsModel invoke() {
        this.entityId = "";
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        this.gender = "";
        this.dob = "";
        this.zeirId = "";
        this.inactive = "";
        this.lostToFollowUp = "";

        if (client.has(AppConstants.KEY.CHILD)) {
            JSONObject child = AppJsonFormUtils.getJsonObject(client, AppConstants.KEY.CHILD);

            // Skip deceased children
            if (StringUtils.isNotBlank(AppJsonFormUtils.getJsonString(child, AppConstants.KEY.DEATHDATE))) {
                myResult = true;
                return this;
            }

            entityId = AppJsonFormUtils.getJsonString(child, AppConstants.KEY.BASE_ENTITY_ID);
            firstName = AppJsonFormUtils.getJsonString(child, AppConstants.KEY.FIRSTNAME);
            middleName = AppJsonFormUtils.getJsonString(child, AppConstants.KEY.MIDDLENAME);
            lastName = AppJsonFormUtils.getJsonString(child, AppConstants.KEY.LASTNAME);

            gender = AppJsonFormUtils.getJsonString(child, AppConstants.KEY.GENDER);
            dob = AppJsonFormUtils.getJsonString(child, AppConstants.KEY.BIRTHDATE);
            if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                try {
                    Long dobLong = Long.valueOf(dob);
                    Date date = new Date(dobLong);
                    dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.toString(), e);
                }
            }

            zeirId = AppJsonFormUtils.getJsonString(AppJsonFormUtils.getJsonObject(child, AppConstants.KEY.IDENTIFIERS), JsonFormUtils.ZEIR_ID);
            if (StringUtils.isNotBlank(zeirId)) {
                zeirId = zeirId.replace("-", "");
            }

            inactive = AppJsonFormUtils.getJsonString(AppJsonFormUtils.getJsonObject(child, AppConstants.KEY.ATTRIBUTES), AppConstants.KEY.INACTIVE);
            lostToFollowUp = AppJsonFormUtils.getJsonString(AppJsonFormUtils.getJsonObject(child, AppConstants.KEY.ATTRIBUTES), AppConstants.KEY.LOST_TO_FOLLOW_UP);
        }
        myResult = false;
        return this;
    }
}
