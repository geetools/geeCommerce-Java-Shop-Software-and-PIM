package com.geecommerce.core.db;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.db.api.ConnectionProvider;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

@Persistence("mongodb")
public class MongoDatabaseConnection implements ConnectionProvider {
    protected MongoClient mongoClient;
    protected Map<String, String> properties;

    @Override
    public String group() {
        return "mongodb";
    }

    @SuppressWarnings("deprecation")
    @Override
    public void init(Map<String, String> properties) {
        if (mongoClient == null) {
            this.properties = new HashMap<>(properties);

            try {
                MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(100).autoConnectRetry(true).connectTimeout(30000).socketTimeout(60000).socketKeepAlive(true).build();
                mongoClient = new MongoClient(new ServerAddress(property("host"), Integer.parseInt(property("port"))), options);
            } catch (Throwable t) {
                throw new IllegalStateException(t);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object provide() {
        DB db = mongoClient.getDB(property("name"));
        db.setWriteConcern(WriteConcern.SAFE);

        if (!db.authenticate(property("user"), property("pass").toCharArray())) {
            throw new IllegalStateException("Unable to establish MongoDB connection with the provided credentials.");
        }

        return db;
    }

    @Override
    public void close() {

    }

    @Override
    public void destroy() {
        mongoClient.close();
    }

    protected String property(String key) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().endsWith("." + key)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
