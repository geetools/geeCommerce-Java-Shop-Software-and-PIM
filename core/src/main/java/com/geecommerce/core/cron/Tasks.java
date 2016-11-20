package com.geecommerce.core.cron;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobKey;

import com.geecommerce.core.App;
import com.geecommerce.core.inject.ModuleInjector;
import com.geecommerce.core.service.annotation.Task;
import com.geemodule.api.ModuleLoader;
import com.geemvc.Str;

public class Tasks {
    @SuppressWarnings("unchecked")
    public static final List<Taskable> find() {
        List<Taskable> tasks = new ArrayList<>();

        ModuleLoader loader = App.get().moduleLoader();

        Class<Taskable>[] foundClasses = (Class<Taskable>[]) loader.findAllTypesAnnotatedWith(Task.class, false);

        for (Class<Taskable> foundClass : foundClasses) {
            Taskable instance = ModuleInjector.get().getInstance(foundClass);
            tasks.add(instance);
        }

        return tasks;
    }

    public static final JobKey getSingleTaskToRun() {
        String envRun = System.getProperty("run");
        String runGroup = null;
        String runName = null;

        if (!Str.isEmpty(envRun)) {
            String[] groupAndname = envRun.split(Str.AT);
            if (groupAndname.length == 2) {
                runGroup = groupAndname[0].trim();
                runName = groupAndname[1].trim();
            }
        }

        return Str.isEmpty(runName) || Str.isEmpty(runGroup) ? null : JobKey.jobKey(runName, runGroup);
    }
}
