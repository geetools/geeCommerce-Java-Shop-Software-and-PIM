package com.geecommerce.antispam.helper;

import com.geecommerce.core.service.api.Helper;

public interface AntiSpamHelper extends Helper {

    public boolean checkSecurity();

    public boolean checkHoneyPotField();

    public boolean checkRecapture();

    public boolean checkRequestCounter(String name);
}
