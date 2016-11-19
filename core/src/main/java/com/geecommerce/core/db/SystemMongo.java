package com.geecommerce.core.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.geecommerce.core.config.SystemConfig;

public enum SystemMongo {
    CLIENT;

    private MongoClient mongoClient;

    public MongoClient get() {
	if (mongoClient == null) {
	    try {
		MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(100).autoConnectRetry(true).connectTimeout(30000).socketTimeout(60000).socketKeepAlive(true).build();

		mongoClient = new MongoClient(new ServerAddress(SystemConfig.GET.val(SystemConfig.MONGODB_HOST), SystemConfig.GET.intVal(SystemConfig.MONGODB_PORT)), options);

	    } catch (Throwable t) {
		throw new IllegalStateException(t);
	    }
	}

	return mongoClient;
    }
}
