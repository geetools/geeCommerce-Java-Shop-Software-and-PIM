package com.geecommerce.core.type;

import java.util.Date;

public interface HistorySupport {
    public Id getHistoryId();

    public void setHistoryId(Id historyId);

    public Date getHistoryDate();

    public void setHistoryDate(Date historyDate);
}
