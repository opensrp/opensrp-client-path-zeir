package org.smartregister.uniceftunisia.widget;

import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.jetbrains.annotations.NotNull;
import org.smartregister.uniceftunisia.util.AppConstants;

import java.util.Set;

public class AppMultiSelectListFactory extends MultiSelectListFactory {

    @NotNull
    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        Set<String> translatableWidgetFields = super.getCustomTranslatableWidgetFields();
        translatableWidgetFields.add(AppConstants.KEY.BUTTON_TEXT);
        translatableWidgetFields.add(AppConstants.KEY.DIALOG_TITLE);
        translatableWidgetFields.add(AppConstants.KEY.SEARCH_HINT);
        translatableWidgetFields.add(AppConstants.KEY.OPTIONS_TEXT);
        return translatableWidgetFields;
    }
}
