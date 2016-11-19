package com.geecommerce.guiwidgets.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.Magazine;

public interface MagazineService extends Service {
    public Magazine createMagazine(Magazine magazine);

    public Magazine getMagazine(Id magazineId);

    public Magazine getActualMagazine();

    public void update(Magazine magazine);
}
