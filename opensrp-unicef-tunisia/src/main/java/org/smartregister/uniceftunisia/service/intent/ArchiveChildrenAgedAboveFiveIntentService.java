package org.smartregister.uniceftunisia.service.intent;

import org.smartregister.child.service.intent.ArchiveClientRecordIntentService;
import org.smartregister.uniceftunisia.dao.ChildDao;

import java.util.List;

public class ArchiveChildrenAgedAboveFiveIntentService extends ArchiveClientRecordIntentService {

    @Override
    protected List<String> getClientIdsToArchive() {
        return ChildDao.getChildrenAboveFiveYears();
    }

    @Override
    protected void onArchiveDone() {
        //Do nothing for now
    }
}
