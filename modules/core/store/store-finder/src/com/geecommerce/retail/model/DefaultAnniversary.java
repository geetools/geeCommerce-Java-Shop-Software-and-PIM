package com.geecommerce.retail.model;

import static com.geecommerce.retail.model.Anniversary.Column.BRANCH_NAME;
import static com.geecommerce.retail.model.Anniversary.Column.END_DATE;
import static com.geecommerce.retail.model.Anniversary.Column.ID;
import static com.geecommerce.retail.model.Anniversary.Column.SPECIAL_TIME_RANGE;
import static com.geecommerce.retail.model.Anniversary.Column.SPECIAL__DATE;
import static com.geecommerce.retail.model.Anniversary.Column.START_DATE;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;

import com.google.common.collect.Maps;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import org.apache.commons.lang.time.DateUtils;

@Cacheable
@Model("anniversaries")
public class DefaultAnniversary extends AbstractModel implements Anniversary {
    private static final long serialVersionUID = 1336487253264098006L;
    private static final String DATE_FORMAT = "dd.MM.yyyy";

    private Id id = null;
    private String branchName;
    private Date startDate;
    private Date endDate;
    private Date specialDate;
    private String specialTimeRange;

    public void fromMap(Map<String, Object> map) {
	if (map == null)
	    return;

	super.fromMap(map);

	this.id = id_(map.get(ID));
	this.branchName = str_(map.get(BRANCH_NAME));
	this.startDate = date_(map.get(START_DATE));
	this.endDate = date_(map.get(END_DATE));
	this.specialDate = date_(map.get(SPECIAL__DATE));
	this.specialTimeRange = str_(map.get(SPECIAL_TIME_RANGE));
    }

    public Map<String, Object> toMap() {
	Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

	map.put(ID, getId());
	map.put(BRANCH_NAME, getBranchName());
	map.put(START_DATE, getStartDate());
	map.put(END_DATE, getEndDate());
	map.put(SPECIAL__DATE, getSpeicalDate());
	map.put(SPECIAL_TIME_RANGE, getSpecialTimeRange());
	return map;
    }

    @Override
    public boolean isSpecialInRange() {
	return specialDate != null && specialDate.compareTo(startDate) >= 0 && specialDate.compareTo(endDate) <= 0;
    }

    @Override
    public String getFormattedStartDate() {
	return DateFormatUtils.format(startDate, DATE_FORMAT);
    }

    @Override
    public String getFormattedEndDate() {
	return DateFormatUtils.format(endDate, DATE_FORMAT);
    }

    @Override
    public String getFormattedSpecialDate() {
	return DateFormatUtils.format(specialDate, DATE_FORMAT);
    }

    @Override
    public Id getId() {
	return null;
    }

    @Override
    public String getBranchName() {
	return branchName;
    }

    @Override
    public Date getStartDate() {
	return startDate;
    }

    @Override
    public Date getEndDate() {
	return endDate;
    }

    @Override
    public void setBranchName(String branchName) {
	this.branchName = branchName;
    }

    @Override
    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Override
    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    public Date getSpeicalDate() {
	return specialDate;
    }

    public String getSpecialTimeRange() {
	return specialTimeRange;
    }

    @Override
    public void setSpecialDate(Date specialDate) {
	this.specialDate = specialDate;
    }

    public void setSpecialTimeRange(String specialTimeRange) {
	this.specialTimeRange = specialTimeRange;
    }

}
