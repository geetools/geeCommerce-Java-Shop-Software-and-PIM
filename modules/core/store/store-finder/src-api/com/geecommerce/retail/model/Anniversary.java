package com.geecommerce.retail.model;

import java.util.Date;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

/**
 */
public interface Anniversary extends Model {

    Id getId();

    String getBranchName();

    void setBranchName(String branchName);

    Date getStartDate();

    void setStartDate(Date date);

    String getFormattedStartDate();

    Date getEndDate();

    void setEndDate(Date date);

    String getFormattedEndDate();

    Date getSpeicalDate();

    void setSpecialDate(Date date);

    String getFormattedSpecialDate();

    String getSpecialTimeRange();

    void setSpecialTimeRange(String timeRange);

    boolean isSpecialInRange();

    class Column {
        public static final String ID = "_id";
        public static final String BRANCH_NAME = "branchName";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String SPECIAL__DATE = "specialDate";
        public static final String SPECIAL_TIME_RANGE = "specialTimeRange";
    }
}
