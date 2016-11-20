package com.geecommerce.antispam.model;

import java.util.Date;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface RequestCounter extends MultiContextModel {

    public RequestCounter setId(Id id);

    public String getName();

    public RequestCounter setName(String name);

    public String getIp();

    public RequestCounter setIp(String ip);

    public int getCount();

    public RequestCounter setCount(Integer count);

    public Date getRequestTime();

    public RequestCounter setRequestTime(Date requestTime);

    public Boolean getBlocked();

    public RequestCounter setBlocked(boolean blocked);

    final class Col {
        public static final String ID = "_id";
        public static final String IP = "ip";
        public static final String COUNT = "count";
        public static final String NAME = "name";
        public static final String BLOCKED = "blocked";
        public static final String REQUEST_TIME = "req_time";

    }
}
