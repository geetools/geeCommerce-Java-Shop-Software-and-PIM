package com.geecommerce.antispam.model;

import java.util.Date;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("request_counters")
public class DefaultRequestCounter extends AbstractMultiContextModel implements RequestCounter {

    @Column(Col.ID)
    private Id id;

    @Column(Col.NAME)
    private String name;

    @Column(Col.IP)
    private String ip;

    @Column(Col.COUNT)
    private Integer count;

    @Column(Col.REQUEST_TIME)
    private Date requestTime;

    @Column(Col.BLOCKED)
    private Boolean blocked;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public RequestCounter setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RequestCounter setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public RequestCounter setIp(String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public int getCount() {
        if (count == null)
            count = 0;
        return count;
    }

    @Override
    public RequestCounter setCount(Integer count) {
        this.count = count;
        return this;
    }

    @Override
    public Date getRequestTime() {
        return requestTime;
    }

    @Override
    public RequestCounter setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    @Override
    public Boolean getBlocked() {
        if (blocked == null)
            blocked = false;
        return blocked;
    }

    @Override
    public RequestCounter setBlocked(boolean blocked) {
        this.blocked = blocked;
        return this;
    }

}
