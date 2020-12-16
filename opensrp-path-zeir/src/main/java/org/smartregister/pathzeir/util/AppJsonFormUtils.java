package org.smartregister.pathzeir.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.clientandeventmodel.Event;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class AppJsonFormUtils extends ChildJsonFormUtils {

    public static String populateFormValues(Context context, Map<String, String> detailsMap, List<String> nonEditableFields) {
        String populatedForm = ChildJsonFormUtils.getMetadataForEditForm(context, detailsMap, nonEditableFields);
        JSONObject form = null;
        try {
            form = new JSONObject(populatedForm);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return form != null ? form.toString() : null;
    }

    public static void tagEventMetadata(Event event) {
        tagSyncMetadata(event);
    }
}
