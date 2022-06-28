package org.jing.project;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
//import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static java.util.concurrent.TimeUnit.SECONDS;

class DB {
    String Host;
    String Port;
    MongoClient client;
    MongoDatabase Database;
    MongoCollection Collection;

    public DB(String host, String port) {
        Host = host;
        Port = port;
    }

    public MongoClient connect() {
        try {
            MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyToSocketSettings(builder -> {
                        builder.connectTimeout(3, SECONDS);
                        builder.readTimeout(3, SECONDS);
                    })
                    .applyToClusterSettings(builder -> builder.serverSelectionTimeout(3, SECONDS))
                    .applyConnectionString(new ConnectionString("mongodb://"+this.Host+":"+this.Port))
                    .build());
            mongoClient.startSession();
            System.out.println("連接成功host: " + this.Host + mongoClient);
            return mongoClient;
        } catch (Exception e) {
            System.out.println("連接失敗");
            return null;
        }
    }

    public void setHost(String host) {
        Host = host;
    }

    public void setPort(String port) {
        Port = port;
    }

    public void setClient(MongoClient client) {
        this.client = client;
    }

    public void setDatabase(MongoDatabase database) {
        if (database == null) {
            System.out.println("資料庫選取錯誤");
        }
        Database = database;
    }

    public void setCollection(MongoCollection collection) {
        if (collection == null) {
            System.out.println("集合選取錯誤");
        }
        Collection = collection;
    }

    public MongoDatabase getDatabase() {
        return Database;
    }

    public MongoCollection getCollection() {
        return Collection;
    }
}

