package com.geecommerce.antispam.repository;

import com.geecommerce.antispam.model.RequestCounter;
import com.geecommerce.core.service.api.Repository;

public interface RequestCounters extends Repository {

    RequestCounter withNameAndIp(String name, String ip);
}
