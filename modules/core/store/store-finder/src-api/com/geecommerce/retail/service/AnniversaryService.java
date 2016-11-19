package com.geecommerce.retail.service;

import com.geecommerce.retail.model.Anniversary;

import java.util.Date;
import java.util.List;

/**
 */
public interface AnniversaryService {

    List<Anniversary> findAllEndDateAfter(Date date);
}
