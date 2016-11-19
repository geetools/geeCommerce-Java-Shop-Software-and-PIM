package com.geecommerce.guiwidgets.service;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.Magazine;
import com.geecommerce.guiwidgets.repository.Magazines;

@Service
public class DefaultMagazineService implements MagazineService {
    private final Magazines magazines;

    @Inject
    public DefaultMagazineService(Magazines magazines) {
	this.magazines = magazines;
    }

    @Override
    public Magazine createMagazine(Magazine magazine) {
	return magazines.add(magazine);
    }

    @Override
    public Magazine getMagazine(Id magazineId) {
	return magazines.findById(Magazine.class, magazineId);
    }

    @Override
    public void update(Magazine magazine) {
	magazines.update(magazine);
    }

    @Override
    public Magazine getActualMagazine() {
	List<Magazine> listMagazine = magazines.enabledMagazines();
	if (listMagazine != null && listMagazine.size() > 0) {
	    for (Magazine magazine : listMagazine) {
		Date currentDate = new Date();
		if ((magazine.getShowFrom() == null || currentDate.after(magazine.getShowFrom())) && (magazine.getShowTo() == null || currentDate.before(magazine.getShowTo())))
		    return magazine;
	    }
	}
	return null;
    }

}
