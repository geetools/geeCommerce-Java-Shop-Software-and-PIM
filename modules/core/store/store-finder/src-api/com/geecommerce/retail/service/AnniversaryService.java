package com.geecommerce.retail.service;

import java.util.Date;
import java.util.List;

import com.geecommerce.retail.model.Anniversary;

/**
 */
public interface AnniversaryService {

    List<Anniversary> findAllEndDateAfter(Date date);
}
