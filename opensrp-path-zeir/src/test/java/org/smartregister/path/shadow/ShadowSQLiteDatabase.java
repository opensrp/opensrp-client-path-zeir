package org.smartregister.path.shadow;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
@Implements(SQLiteDatabase.class)
public class ShadowSQLiteDatabase {

    @Implementation
    public static synchronized void loadLibs (Context context) {
        // Do nothing here to avoid the crash due to lacking native libs
    }
}
