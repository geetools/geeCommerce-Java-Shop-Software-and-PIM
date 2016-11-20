package com.geecommerce.vacancy.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.vacancy.model.Vacancy;
import com.geecommerce.vacancy.model.VacancyGroup;

@Repository
public class DefaultVacancies extends AbstractRepository implements Vacancies {
    @Override
    public List<Vacancy> thatBelongTo(VacancyGroup group) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Vacancy.Col.GROUP_ID, group.getId());

        List<Vacancy> vacancies = find(Vacancy.class, filter,
            QueryOptions.builder().sortBy(Vacancy.Col.POSITION).build());
        return vacancies;
    }
}
