package org.smartregister.path.widget;

import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.jetbrains.annotations.NotNull;
import org.smartregister.path.util.AppConstants;

import java.util.Set;

public class AppMultiSelectListFactory extends MultiSelectListFactory {

    @NotNull
    @Override
    public Set<String> getCustomTranslatableWidgetFields() {
        Set<String> translatableWidgetFields = super.getCustomTranslatableWidgetFields();
        translatableWidgetFields.add(AppConstants.KeyConstants.BUTTON_TEXT);
        translatableWidgetFields.add(AppConstants.KeyConstants.DIALOG_TITLE);
        translatableWidgetFields.add(AppConstants.KeyConstants.SEARCH_HINT);
        translatableWidgetFields.add(AppConstants.KeyConstants.OPTIONS_TEXT);
        return translatableWidgetFields;
    }
}
