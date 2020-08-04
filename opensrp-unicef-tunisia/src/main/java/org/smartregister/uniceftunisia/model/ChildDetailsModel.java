package org.smartregister.uniceftunisia.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.uniceftunisia.util.AppConstants;

import timber.log.Timber;

import static org.smartregister.uniceftunisia.util.AppJsonFormUtils.getJsonObject;
import static org.smartregister.uniceftunisia.util.AppJsonFormUtils.getJsonString;

public class ChildDetailsModel implements Comparable<ChildDetailsModel> {

    private String id;
    public String childBaseEntityId;
    private String relationalId;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String zeirId;
    private String motherBaseEntityId;
    private String fatherBaseEntityId;
    private String motherFirstName;
    private String motherLastName;
    private String inActive;
    private String lostFollowUp;
    private JSONObject ClientJson;

    public ChildDetailsModel(JSONObject ClientJson) {
        setClientJson(ClientJson);
        mapJsonToField();
    }

    public Object[] getColumnValuesFromJson() {
        return new Object[]{
                getChildBaseEntityId(), getRelationalId(), getFirstName(), getLastName(), getGender(), getDateOfBirth(),
                getZeirId(), getMotherBaseEntityId(), getFatherBaseEntityId(), getMotherFirstName(), getMotherLastName(), getInActive(), getLostFollowUp()
        };
    }

    private void mapJsonToField() {
        try {

            JSONObject childJson = ClientJson.getJSONObject(AppConstants.KEY.CHILD);
            JSONObject motherJson = ClientJson.getJSONObject(AppConstants.KEY.MOTHER);

            if (childJson.has(AppConstants.KEY.ID_LOWER_CASE)) {
                setId(childJson.getString(AppConstants.Client.ID_LOWER_CASE));
            }
            if (childJson.has(AppConstants.Client.BASE_ENTITY_ID)) {
                setChildBaseEntityId(childJson.getString(AppConstants.Client.BASE_ENTITY_ID));
            }
            if (childJson.has(AppConstants.Client.RELATIONSHIPS)) {
                JSONObject relationships = childJson.getJSONObject(AppConstants.Client.RELATIONSHIPS);
                if (relationships != null && relationships.has(AppConstants.KEY.MOTHER)) {
                    setRelationalId(relationships.getJSONArray(AppConstants.KEY.MOTHER).getString(0));
                }
            }
            if (childJson.has(AppConstants.Client.FIRST_NAME)) {
                setFirstName(childJson.getString(AppConstants.Client.FIRST_NAME));
            }
            if (childJson.has(AppConstants.Client.LAST_NAME)) {
                setLastName(childJson.getString(AppConstants.Client.LAST_NAME));
            }
            if (childJson.has(AppConstants.Client.GENDER)) {
                setGender(childJson.getString(AppConstants.Client.GENDER));
            }
            if (childJson.has(AppConstants.Client.BIRTHDATE)) {
                setDateOfBirth(childJson.getString(AppConstants.Client.BIRTHDATE));
            }
            if (childJson.has(AppConstants.Client.IDENTIFIERS)) {
                setZeirId(childJson.getJSONObject(AppConstants.Client.IDENTIFIERS).getString(AppConstants.KEY.ZEIR_ID));
            }
            if (motherJson.has(AppConstants.Client.BASE_ENTITY_ID)) {
                setMotherBaseEntityId(motherJson.getString(AppConstants.Client.BASE_ENTITY_ID));
            }
            if (childJson.has(AppConstants.Client.RELATIONSHIPS)) {
                JSONObject relationships = childJson.getJSONObject(AppConstants.Client.RELATIONSHIPS);
                if (relationships != null && relationships.has(AppConstants.KEY.FATHER)) {
                    setFatherBaseEntityId(relationships.getJSONArray(AppConstants.KEY.FATHER).getString(0));
                }
            }
            if (motherJson.has(AppConstants.Client.FIRST_NAME)) {
                setMotherFirstName(motherJson.getString(AppConstants.Client.FIRST_NAME));
            }
            if (motherJson.has(AppConstants.Client.LAST_NAME)) {
                setMotherLastName(motherJson.getString(AppConstants.Client.LAST_NAME));
            }
            if (childJson.has(AppConstants.KEY.ATTRIBUTES)) {
                setInActive(getJsonString(getJsonObject(childJson, AppConstants.KEY.ATTRIBUTES), AppConstants.Client.INACTIVE));
                setLostFollowUp(getJsonString(getJsonObject(childJson, AppConstants.KEY.ATTRIBUTES), AppConstants.Client.LOST_TO_FOLLOW_UP));
            }

        } catch (JSONException e) {
            Timber.e(e, "Error parsing Advanced Search Client JSON");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChildBaseEntityId() {
        return childBaseEntityId;
    }

    public void setChildBaseEntityId(String childBaseEntityId) {
        this.childBaseEntityId = childBaseEntityId;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public void setRelationalId(String relationalId) {
        this.relationalId = relationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getZeirId() {
        return zeirId;
    }

    public void setZeirId(String zeirId) {
        this.zeirId = zeirId;
    }

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public void setMotherBaseEntityId(String motherBaseEntityId) {
        this.motherBaseEntityId = motherBaseEntityId;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public void setMotherFirstName(String motherFirstName) {
        this.motherFirstName = motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public void setMotherLastName(String motherLastName) {
        this.motherLastName = motherLastName;
    }

    public void setClientJson(JSONObject ClientJson) {
        this.ClientJson = ClientJson;
    }

    public String getInActive() {
        return inActive;
    }

    public void setInActive(String inActive) {
        this.inActive = inActive;
    }

    public String getLostFollowUp() {
        return lostFollowUp;
    }

    public void setLostFollowUp(String lostFollowUp) {
        this.lostFollowUp = lostFollowUp;
    }

    @Override
    public int compareTo(ChildDetailsModel childDetailsModel) {
        return this.getZeirId().compareTo(childDetailsModel.getZeirId());
    }

    public String getFatherBaseEntityId() {
        return fatherBaseEntityId;
    }

    public void setFatherBaseEntityId(String fatherBaseEntityId) {
        this.fatherBaseEntityId = fatherBaseEntityId;
    }
}
