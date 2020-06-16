package org.smartregister.uniceftunisia.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class CheckChildDetailsModelTest {

    @Test
    public void invoke() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonChildObject = new JSONObject();
        JSONObject jsonIdentifiersObject = new JSONObject();
        JSONObject jsonAttributesObject = new JSONObject();
        jsonAttributesObject.put(AppConstants.KEY.INACTIVE, "inactive");
        jsonAttributesObject.put(AppConstants.KEY.LOST_TO_FOLLOW_UP, "lost to follow up");

        jsonIdentifiersObject.put(JsonFormUtils.ZEIR_ID, "zeir-id");
        jsonChildObject.put(AppConstants.KEY.IDENTIFIERS, jsonIdentifiersObject);
        jsonChildObject.put(AppConstants.KEY.ATTRIBUTES, jsonAttributesObject);

        jsonChildObject.put(AppConstants.KEY.BASE_ENTITY_ID, "entityId");
        jsonChildObject.put(AppConstants.KEY.FIRSTNAME, "first");
        jsonChildObject.put(AppConstants.KEY.LASTNAME, "last");
        jsonChildObject.put(AppConstants.KEY.MIDDLENAME, "middle");
        jsonChildObject.put(AppConstants.KEY.BIRTHDATE, String.valueOf(new Date().getTime()));
        jsonChildObject.put(AppConstants.KEY.GENDER, "male");

        jsonObject.put(AppConstants.KEY.CHILD,jsonChildObject);
        CheckChildDetailsModel checkChildDetailsModel = new CheckChildDetailsModel(jsonObject);
        CheckChildDetailsModel resultChildDetailsModel = checkChildDetailsModel.invoke();
        Assert.assertEquals("entityId", resultChildDetailsModel.getEntityId());
        Assert.assertEquals("zeirid", resultChildDetailsModel.getZeirId());
        Assert.assertEquals("first", resultChildDetailsModel.getFirstName());
        Assert.assertEquals("last", resultChildDetailsModel.getLastName());
        Assert.assertEquals("male", resultChildDetailsModel.getGender());
        Assert.assertEquals("inactive", resultChildDetailsModel.getInactive());
        Assert.assertEquals("lost to follow up", resultChildDetailsModel.getLostToFollowUp());
    }
}