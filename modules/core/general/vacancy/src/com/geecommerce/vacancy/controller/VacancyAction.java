package com.geecommerce.vacancy.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.vacancy.model.Vacancy;
import com.geecommerce.vacancy.model.VacancyGroup;
import com.geecommerce.vacancy.service.VacancyService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/jobs/{$event}/{id}")
public class VacancyAction extends BaseActionBean {
    private VacancyService vacancyService;

    private List<VacancyGroup> vacancyGroups = new ArrayList<>();
    private Map<String, List<Vacancy>> vacancyByBranch = new LinkedHashMap<>();
    private Vacancy vacancy;

    @Inject
    public VacancyAction(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @DefaultHandler
    public Resolution view() {
        this.vacancyGroups = vacancyService.getVacancyGroups();
        return view("/vacancy/list");
    }

    @HandlesEvent("vacancy")
    public Resolution details() {
        this.vacancy = vacancyService.getVacancy(getId());
        return view("/vacancy/details");
    }

    @HandlesEvent("vacanciesByBranch")
    public Resolution vacancies() {

        Map<String, List<Vacancy>> vacancyByBranch = new LinkedHashMap<>();
        vacancyService.getVacancies().stream().filter(vacancy -> vacancy.isShow()).forEach(vacancy -> {
            if (vacancyByBranch.containsKey(vacancy.getBranch())) {
                vacancyByBranch.get(vacancy.getBranch()).add(vacancy);
            } else {
                LinkedList<Vacancy> vacancies = new LinkedList<>();
                vacancies.add(vacancy);
                vacancyByBranch.put(vacancy.getBranch(), vacancies);
            }
        });

        this.vacancyByBranch = vacancyByBranch;
        return view("/vacancy/list_by_branch");
    }

    public Map<String, List<Vacancy>> getVacancyByBranch() {
        return vacancyByBranch;
    }

    public void setVacancyByBranch(Map<String, List<Vacancy>> vacancyByBranch) {
        this.vacancyByBranch = vacancyByBranch;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }

    public void setVacancy(Vacancy vacancy) {
        this.vacancy = vacancy;
    }

    public List<VacancyGroup> getVacancyGroups() {
        return vacancyGroups;
    }
}
