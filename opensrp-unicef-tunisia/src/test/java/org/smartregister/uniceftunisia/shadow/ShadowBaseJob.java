package org.smartregister.uniceftunisia.shadow;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.job.BaseJob;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
@Implements(BaseJob.class)
public class ShadowBaseJob {

    private static ShadowHelper shadowHelper = new ShadowHelper();
    public static String scheduleJobMN = "scheduleJob(String, Long, Long)";
    public static String scheduleJobImmediatelyMN = "scheduleJobImmediately(String)";

    @Implementation
    public static void scheduleJob(String jobTag, Long start, Long flex) {
        shadowHelper.addMethodCall(scheduleJobMN, jobTag, start, flex);
    }

    @Implementation
    public static void scheduleJobImmediately(String jobTag) {
        shadowHelper.addMethodCall(scheduleJobImmediatelyMN, jobTag);
    }

    public static ShadowHelper getShadowHelper() {
        return shadowHelper;
    }

}
