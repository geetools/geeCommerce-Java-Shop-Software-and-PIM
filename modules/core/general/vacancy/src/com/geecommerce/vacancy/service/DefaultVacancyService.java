package com.geecommerce.vacancy.service;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.vacancy.model.Vacancy;
import com.geecommerce.vacancy.model.VacancyGroup;
import com.geecommerce.vacancy.repository.Vacancies;

@Service
public class DefaultVacancyService implements VacancyService {
    private final Vacancies vacancies;

    @Inject
    public DefaultVacancyService(Vacancies vacancies) {
	this.vacancies = vacancies;
    }

    @Override
    public List<VacancyGroup> getVacancyGroups() {
	return vacancies.findAll(VacancyGroup.class, QueryOptions.builder().sortBy(VacancyGroup.Col.POSITION).build());
    }

    @Override
    public VacancyGroup getVacancyGroup(Id groupId) {
	if (groupId == null)
	    return null;

	VacancyGroup group = vacancies.findById(VacancyGroup.class, groupId);
	return group;
    }

    @Override
    public List<Vacancy> getVacancies(VacancyGroup group) {
	return vacancies.thatBelongTo(group);
    }

    @Override
    public List<Vacancy> getVacancies() {
	return vacancies.findAll(Vacancy.class, QueryOptions.builder().sortBy(Vacancy.Col.POSITION).build());
    }

    @Override
    public List<Vacancy> getVacancies(Map<String, Object> query, QueryOptions queryOptions) {
	return vacancies.find(Vacancy.class, query, queryOptions);
    }

    @Override
    public Vacancy getVacancy(Id vacancyId) {
	if (vacancyId == null)
	    return null;

	Vacancy vacancy = vacancies.findById(Vacancy.class, vacancyId);
	return vacancy;
    }
}
