package org.smartregister.pathzeir.service.intent;

import org.smartregister.child.service.intent.ArchiveClientRecordIntentService;
import org.smartregister.pathzeir.dao.AppChildDao;

import java.util.List;

public class ArchiveChildrenAgedAboveFiveIntentService extends ArchiveClientRecordIntentService {

    @Override
    protected List<String> getClientIdsToArchive() {
        return AppChildDao.getChildrenAboveFiveYears();
    }

    @Override
    protected void onArchiveDone() {
        //Do nothing for now
    }
}
