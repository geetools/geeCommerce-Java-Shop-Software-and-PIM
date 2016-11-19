package com.geecommerce.vacancy.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.vacancy.model.Vacancy;
import com.geecommerce.vacancy.model.VacancyGroup;

import java.util.List;

public interface Vacancies extends Repository {
    List<Vacancy> thatBelongTo(VacancyGroup group);
}
