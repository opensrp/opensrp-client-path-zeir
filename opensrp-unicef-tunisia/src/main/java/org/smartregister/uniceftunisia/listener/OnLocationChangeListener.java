package org.smartregister.uniceftunisia.listener;

import org.jetbrains.annotations.Nullable;

public interface OnLocationChangeListener {
    void updateUi(@Nullable String location);
}
