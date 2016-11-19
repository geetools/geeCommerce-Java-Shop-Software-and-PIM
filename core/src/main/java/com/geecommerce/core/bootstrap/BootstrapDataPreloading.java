package com.geecommerce.core.bootstrap;

import com.geecommerce.core.bootstrap.annotation.Bootstrap;

@Bootstrap(order = 30, unitTest = false)
public class BootstrapDataPreloading extends AbstractBootstrap {
    @Override
    public synchronized void init() {
        // if (!app.isMediaRequest())
        // {
        // Set<Class<Model>> preloadableModels = Models.thatArePreloadable();
        //
        // RestRepository repository = app.getRepository(RestRepository.class);
        //
        // for (Class<Model> preloadableModel : preloadableModels)
        // {
        // List<Model> cachedModels = repository.findAll(preloadableModel,
        // QueryOptions.builder().fromCacheOnly(true).build());
        // // System.out.println(app.getServletRequest().getRequestURI() + " -
        // CACHED-" + preloadableModel.getSimpleName().toUpperCase() + " :1: " +
        // cachedModels.size());
        //
        // if(cachedModels == null || cachedModels.size() < 100 ||
        // app.refreshHeaderExists() || app.previewHeaderExists())
        // {
        // List<Model> freshModels = repository.findAll(preloadableModel,
        // QueryOptions.builder().refresh(true).build());
        //
        // // System.out.println(app.getServletRequest().getRequestURI() + " -
        // FRESH-" + preloadableModel.getSimpleName().toUpperCase() + " ::: " +
        // freshModels.size());
        //
        // // cachedModels = repository.findAll(preloadableModel,
        // QueryOptions.builder().fromCacheOnly(true).build());
        //
        // // System.out.println(app.getServletRequest().getRequestURI() + " -
        // CACHED-" + preloadableModel.getSimpleName().toUpperCase() + " :2: " +
        // cachedModels.size());
        // }
        // }
        // }
    }
}
