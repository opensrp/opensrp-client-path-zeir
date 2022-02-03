package org.smartregister.path.reporting.monthly.intent;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONObject;
import org.smartregister.domain.Response;
import org.smartregister.path.R;
import org.smartregister.path.application.ZeirApplication;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.service.HTTPAgent;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

public class ZeirHIA2IntentService extends IntentService {

    private static final String TAG = ZeirHIA2IntentService.class.getCanonicalName();

    private static final String REPORTS = "reports";

    private static final String REPORTS_SYNC_PATH = "/rest/report/add";

    public ZeirHIA2IntentService() {
        super("ZeirHIA2IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.tag(TAG).i("Started HIA2 service");
        try {
            pushReportsToServer();
        } catch (Exception e) {
            Timber.tag(TAG).e(e);
        }
        Timber.tag(TAG).i("Finishing HIA2 service");
    }

    private void pushReportsToServer() {
        HTTPAgent httpAgent = ZeirApplication.getInstance().context().getHttpAgent();
        Hia2ReportRepository hia2ReportRepository = ZeirApplication.getInstance().hia2ReportRepository();
        try {
            int limit = 50;
            while (true) {
                List<JSONObject> pendingReports = hia2ReportRepository.getUnSyncedReports(limit);
                if (pendingReports.isEmpty()) {
                    return;
                }
                String baseUrl = ZeirApplication.getInstance().context().configuration().dristhiBaseURL();
                if (baseUrl.endsWith(getApplicationContext().getString(R.string.url_separator))) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(getApplicationContext().getString(R.string.url_separator)));
                }

                JSONObject request = new JSONObject();
                request.put(REPORTS, pendingReports);
                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        MessageFormat.format("{0}/{1}",
                                baseUrl,
                                REPORTS_SYNC_PATH),
                        jsonPayload);
                if (response.isFailure()) {
                    Timber.e("Reports sync failed.");
                    return;
                }
                hia2ReportRepository.markReportsAsSynced(pendingReports);
                Timber.i("Reports synced successfully.");
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
