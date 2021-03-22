package org.smartregister.pathzeir.reporting.stock.repository;

import android.text.TextUtils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.pathzeir.application.ZeirApplication;
import org.smartregister.pathzeir.repository.ZeirRepository;
import org.smartregister.repository.Repository;
import org.smartregister.service.AlertService;
import org.smartregister.stock.domain.ActiveChildrenStats;
import org.smartregister.stock.repository.StockExternalRepository;
import org.smartregister.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;

/**
 * Created by samuelgithengi on 2/14/18.
 */

public class ZeirStockHelperRepository extends StockExternalRepository {

    public ZeirStockHelperRepository(Repository repository) {
        super(repository);
    }

    @Override
    public int getVaccinesUsedToday(Long date, String vaccineName) {
        int vaccineUsed = 0;
        DateTime thedate = new DateTime(date);
        DateTime startofday = thedate.withTimeAtStartOfDay();
        DateTime endofday = thedate.plusDays(1).withTimeAtStartOfDay();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery("Select count(*) from vaccines where date >= " + startofday.getMillis() + " and date < " + endofday.getMillis() + " and name like '%" + vaccineName + "%'", null);
        c.moveToFirst();
        if (c.getCount() > 0 && !StringUtils.isBlank(c.getString(0))) {
            vaccineUsed = Integer.parseInt(c.getString(0));
        }

        c.close();
        return vaccineUsed;
    }

    @Override
    public int getVaccinesUsedUntilDate(Long date, String vaccineName) {
        int vaccineUsed = 0;
        DateTime thedate = new DateTime(date);
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery("Select count(*) from vaccines where date <= " + thedate.getMillis() + " and name like '%" + vaccineName + "%'", null);
        c.moveToFirst();
        if (c.getCount() > 0 && !StringUtils.isBlank(c.getString(0))) {
            vaccineUsed = Integer.parseInt(c.getString(0));
        }
        c.close();
        return vaccineUsed;
    }


    @Override
    public ActiveChildrenStats getActiveChildrenStat() {
        ActiveChildrenStats activeChildrenStats = new ActiveChildrenStats();
        ZeirRepository repo = (ZeirRepository) ZeirApplication.getInstance().getRepository();
        SQLiteDatabase db = repo.getReadableDatabase();
        Cursor c = db.rawQuery("select ec_client.dob, ec_client.registration_date " +
                "from ec_client " +
                "inner join ec_child_details " +
                "on ec_client.base_entity_id = ec_child_details.base_entity_id " +
                "where (( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' ) " +
                "and  ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' ))", null);
        c.moveToFirst();
        boolean thismonth;

        while (!c.isAfterLast()) {
            thismonth = false;
            String dobString = c.getString(0);
            String createdString = c.getString(1);
            if (!TextUtils.isEmpty(dobString)) {
                DateTime dateTime = new DateTime(dobString);
                Date dob = dateTime.toDate();
                DateTime dateTime2 = new DateTime(createdString);
                DateTime now = new DateTime(System.currentTimeMillis());
                if (now.getMonthOfYear() == dateTime2.getMonthOfYear() && now.getYear() == dateTime2.getYear()) {
                    thismonth = true;
                }


                long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

                if (timeDiff >= 0) {
                    int months = (int) Math.floor((float) timeDiff /
                            TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
                    int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(
                            months * 30, TimeUnit.DAYS)) /
                            TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

                    if (weeks >= 4) {
                        weeks = 0;
                        months++;
                    }
                    if (months < 12) {
                        if (thismonth) {
                            activeChildrenStats.setChildrenThisMonthZeroToEleven(activeChildrenStats.getChildrenThisMonthZeroToEleven() + 1);
                        } else {
                            activeChildrenStats.setChildrenLastMonthZeroToEleven(activeChildrenStats.getChildrenLastMonthZeroToEleven() + 1);
                        }
                    } else if (months > 11 && months < 60) {
                        if (thismonth) {
                            activeChildrenStats.setChildrenThisMonthtwelveTofiftyNine(activeChildrenStats.getChildrenThisMonthtwelveTofiftyNine() + 1);
                        } else {
                            activeChildrenStats.setChildrenLastMonthtwelveTofiftyNine(activeChildrenStats.getChildrenLastMonthtwelveTofiftyNine() + 1);
                        }
                    }
                }
            }
            c.moveToNext();
        }
        c.close();
        return activeChildrenStats;
    }

    @Override
    public int getVaccinesDueBasedOnSchedule(JSONObject vaccineobject) {
        int countofNextMonthVaccineDue = 0;
        try {
            ZeirRepository repo = (ZeirRepository) ZeirApplication.getInstance().getRepository();
            SQLiteDatabase db = repo.getReadableDatabase();
            Cursor c = db.rawQuery("select ec_client.dob, ec_client.base_entity_id " +
                    "from ec_client " +
                    "inner join ec_child_details " +
                    "on ec_client.base_entity_id = ec_child_details.base_entity_id " +
                    "where (( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' ) " +
                    "and  ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' ))", null);
            c.moveToFirst();
            String vaccineName = vaccineobject.getString("name");
            Thread.sleep(5000);

            while (!c.isAfterLast()) {
                String dobString = c.getString(0);
                String baseEntityId = c.getString(1);
                if (!TextUtils.isEmpty(dobString)) {
                    List<Vaccine> vaccines = ZeirApplication.getInstance().vaccineRepository().findByEntityId(baseEntityId);
                    AlertService alertService = ImmunizationLibrary.getInstance().context().alertService();
                    List<Alert> alerts = alertService.findByEntityId(baseEntityId);

                    Map<String, Date> receivedVaccines = receivedVaccines(vaccines);
                    DateTime dateTime = Utils.dobStringToDateTime(dobString);
                    List<Map<String, Object>> scheduleList = VaccinatorUtils.generateScheduleList(Constants.KEY.CHILD, dateTime, receivedVaccines, alerts);

                    for (Map<String, Object> scheduledVaccine : scheduleList ) {
                        DateTime dueDate = (DateTime) scheduledVaccine.get(Constants.DATE);
                        VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) scheduledVaccine.get(Constants.KEY.VACCINE);

                        if (vaccine.display().equalsIgnoreCase(vaccineName) && dueDate != null) {
                            DateTime today = new DateTime(System.currentTimeMillis());
                            DateTime startOfNextMonth = today.plusMonths(1).dayOfMonth().withMinimumValue();

                            if (dueDate.yearOfCentury().equals(startOfNextMonth.yearOfCentury())
                                    && dueDate.monthOfYear().equals(startOfNextMonth.monthOfYear())) {
                                countofNextMonthVaccineDue++;
                            }
                        }
                    }
                }
                c.moveToNext();
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countofNextMonthVaccineDue;
    }
}
