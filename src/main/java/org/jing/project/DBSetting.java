package org.jing.project;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Scanner;

public class DBSetting {
    public static void main(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        int func;
        System.out.print("請選擇: 1)重設伺服器(Server) 2)重設資料庫(database) 3)重設集合(Collection): ");
        func = cin.nextInt();
        switch (func) {
            case 1 -> resetHost(mongodb, true);
            case 2 -> resetDatabase(mongodb);
            case 3 -> resetCollection(mongodb);
            default -> System.out.println("輸入錯誤！");
        }
    }

    public static void resetHost(DB mongodb, boolean... reset) {
        // 設定host and port
        if (reset.length == 0) {
            mongodb.setClient(mongodb.connect());
        } else {
            mongodb.setClient(null);
        }
        while (mongodb.client == null) {
            Scanner cin = new Scanner(System.in);
            System.out.println("請重新輸入host: ");
            mongodb.setHost(cin.next());
            System.out.print("請重新輸入post: ");
            mongodb.setPort(cin.next());
            mongodb.client = mongodb.connect();
        }
        resetDatabase(mongodb);
    }

    public static void resetDatabase(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        int list_index = 0;
        System.out.println("請選擇要連接的資料庫: ");
        MongoDatabase database;
        ArrayList<JSONObject> JsonList = new ArrayList<>();
        mongodb.client.listDatabases().forEach(a -> {
            JSONObject jsonObject = JSONObject.parseObject(a.toJson());
            JsonList.add(JSONObject.parseObject(a.toJson()));
            System.out.println(JsonList.lastIndexOf(jsonObject) + " " + jsonObject.getString("name"));
        });
        do {
            if (list_index > JsonList.size() - 1 || list_index < 0) {
                System.out.println("input error, input should in the list");
            }
            System.out.print("請選擇: ");
            list_index = cin.nextInt();
        } while (list_index > JsonList.size() - 1 || list_index < 0);
        database = mongodb.client.getDatabase(JsonList.get(list_index).getString("name"));
        mongodb.setDatabase(database);

        resetCollection(mongodb);
    }

    public static void resetCollection(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        int list_index;
        MongoCollection<Document> collection;
        ArrayList<JSONObject> JsonList2 = new ArrayList<>();
        mongodb.getDatabase().listCollections().forEach(a -> {
            JSONObject jsonObject = JSONObject.parseObject(a.toJson());
            JsonList2.add(jsonObject);
            System.out.println(JsonList2.lastIndexOf(jsonObject) + " " + jsonObject.getString("name"));
        });
        do {
            System.out.print("請輸入要連接的集合: ");
            list_index = cin.nextInt();
        }
        while (list_index < 0 || list_index > JsonList2.size() - 1);
        collection = mongodb.getDatabase().getCollection(JsonList2.get(list_index).getString("name"));
        mongodb.setCollection(collection);
    }
}
