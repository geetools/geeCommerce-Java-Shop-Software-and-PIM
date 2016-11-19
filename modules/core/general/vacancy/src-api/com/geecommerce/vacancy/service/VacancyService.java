package com.geecommerce.vacancy.service;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.vacancy.model.Vacancy;
import com.geecommerce.vacancy.model.VacancyGroup;

import java.util.List;
import java.util.Map;

public interface VacancyService extends Service {
    List<VacancyGroup> getVacancyGroups();

    VacancyGroup getVacancyGroup(Id groupId);

    List<Vacancy> getVacancies(VacancyGroup group);

    List<Vacancy> getVacancies();

    List<Vacancy> getVacancies(Map<String, Object> query, QueryOptions queryOptions);

    Vacancy getVacancy(Id vacancyId);
}
