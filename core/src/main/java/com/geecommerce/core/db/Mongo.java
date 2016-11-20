package com.geecommerce.core.db;

import com.geecommerce.core.config.SystemConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public enum Mongo {
    CLIENT;

    private MongoClient mongoClient;

    public MongoClient get() {
        if (mongoClient == null) {
            try {
                MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(100).autoConnectRetry(true)
                    .connectTimeout(30000).socketTimeout(60000).socketKeepAlive(true).build();

                // System.out.println("Using [host=" +
                // SystemConfig.GET.val(SystemConfig.MONGODB_HOST) + ", port=" +
                // SystemConfig.GET.intVal(SystemConfig.MONGODB_PORT) + "]");

                mongoClient = new MongoClient(new ServerAddress(SystemConfig.GET.val(SystemConfig.MONGODB_HOST),
                    SystemConfig.GET.intVal(SystemConfig.MONGODB_PORT)), options);
            } catch (Throwable t) {
                throw new IllegalStateException(t);
            }
        }

        return mongoClient;
    }
}
