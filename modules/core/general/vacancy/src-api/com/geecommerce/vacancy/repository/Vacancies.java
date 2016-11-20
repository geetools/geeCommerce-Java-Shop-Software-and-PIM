package com.geecommerce.vacancy.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.vacancy.model.Vacancy;
import com.geecommerce.vacancy.model.VacancyGroup;

public interface Vacancies extends Repository {
    List<Vacancy> thatBelongTo(VacancyGroup group);
}
