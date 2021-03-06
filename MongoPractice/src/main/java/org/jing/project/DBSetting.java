package org.jing.project;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Scanner;

public class DBSetting {
    public static void main(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        int func;
        do {
            System.out.print("請選擇: 1)重設伺服器(Server) 2)重設資料庫(database) 3)重設集合(Collection) -1)上一頁: ");
            func = cin.nextInt();
            switch (func) {
                case 1 -> resetHost(mongodb, true);
                case 2 -> reChooseDatabase(mongodb);
                case 3 -> reChooseCollection(mongodb);
                case 4 -> addCollection(mongodb);
                case 5 -> addDatabase(mongodb);
                case -1 -> {
                }
                default -> System.out.println("輸入錯誤！");
            }
        } while (func != -1);
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
        reChooseDatabase(mongodb);
    }

    public static void reChooseDatabase(DB mongodb) {
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

        reChooseCollection(mongodb);
    }

    public static void reChooseCollection(DB mongodb) {
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

    public static void addCollection(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        System.out.print("請輸入要新增的名稱: ");
        try {
            mongodb.getDatabase().createCollection(cin.next());
        } catch (MongoCommandException e) {
            System.out.println("新增失敗");
            System.out.println(e.getErrorMessage());
        }
    }

    public static void addDatabase(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        System.out.print("請輸入要新增的Database名稱(需新增資料後才會實際建立): ");
        try {
            mongodb.getClient().getDatabase(cin.next());
        } catch (MongoCommandException e) {
            System.out.println("新增失敗");
            System.out.println(e.getErrorMessage());
        }
    }
}
