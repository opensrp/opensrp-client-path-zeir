package org.smartregister.path.reporting.dropuout.domain;


import org.apache.commons.lang3.StringUtils;
import org.smartregister.path.reporting.dropuout.repository.CohortRepository;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by keyman on 11/01/18.
 */
public class Cohort implements Serializable {
    private Long id;
    private String month;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public Date getMonthAsDate() {
        if (StringUtils.isBlank(month)) {
            return null;
        }
        try {
            return CohortRepository.DF_YYYYMM.parse(month);
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setMonth(Date date) {
        if (date != null) {
            try {
                this.month = CohortRepository.DF_YYYYMM.format(date);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

