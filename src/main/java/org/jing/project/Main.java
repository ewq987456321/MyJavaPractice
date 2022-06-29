package org.jing.project;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;


public class Main {
    public static void DBSetting(DB mongodb, boolean... reset) {
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

    public static void PrintAll(DB mongodb) {
        Bson FieldSetting = Projections.fields(
                Projections.include("name", "age"),
                Projections.excludeId()
        );
        mongodb.getCollection()
                .find()
                .projection(FieldSetting)
                .forEach(a -> {
                    JSONObject jsonObject = JSONObject.parseObject(((Document) a).toJson());
                    System.out.println(jsonObject);
                });
    }

    public static void PrintWithSort(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        System.out.print("請輸入要依據什麼排序");
        String sort = cin.next();
        Bson projection = Projections.fields(
                Projections.include("name", "age"),
                Projections.excludeId()
        );
        mongodb.getCollection().find().sort(Sorts.ascending(sort)).projection(projection).forEach(
                a -> {
                    JSONObject jsonObject = JSONObject.parseObject(((Document) a).toJson());
                    System.out.println(jsonObject);
                });
    }

    public static void InsertDocument(DB mongodb) {
        String key, value;
        Scanner cin = new Scanner(System.in);
        Document doc = new Document();
        doc.append("_id", new ObjectId());
        do {
            System.out.print("請輸入key(欲結束輸入請輸入 null: ");
            key = cin.next();
            if (!Objects.equals(key, "null")) {
                System.out.print("請輸入value: ");
                value = cin.next();
                doc.append(key, value);
            }
        } while (!Objects.equals(key, "null"));
        mongodb.getCollection().insertOne(doc);
    }

    public static void UpdateDocument(DB mongodb) {
        String key, value;
        Scanner cin = new Scanner(System.in);
        UpdateOptions options = new UpdateOptions().upsert(false);
        PrintAll(mongodb);

        System.out.print("請輸入舊的key: ");
        key = cin.next();
        System.out.print("請輸入舊的value: ");
        value = cin.next();
        BasicDBObject query = new BasicDBObject();
        query.put(key, value);

        System.out.print("以下為搜尋到的結果\n");
        mongodb.getCollection()
                .find()
                .filter(query)
                .projection(Projections.fields(Projections.excludeId()))
                .iterator().forEachRemaining(a -> System.out.println(JSONObject.parseObject(((Document) a).toJson())));
        System.out.print("\n確定要修改嗎(y/n): ");
        if (!Objects.equals(cin.next(), "n")) {
            System.out.print("請輸入新的key: ");
            key = cin.next();
            System.out.print("請輸入新的value: ");
            value = cin.next();
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put(key, value);

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);

            UpdateResult result =
                    mongodb.getCollection().updateMany(query, updateObject, options);
            if (result.getModifiedCount() != 0) {
                System.out.println("修改成功");
                mongodb.getCollection()
                        .find()
                        .filter(query)
                        .projection(Projections.fields(Projections.excludeId()))
                        .iterator().forEachRemaining(a -> System.out.println(JSONObject.parseObject(((Document) a).toJson())));
            } else {
                System.out.print("未找到你輸入的資料，修改失敗\n");
            }
        }
    }

    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        DB mongodb = new DB("localhost", "27017");
        DBSetting(mongodb);
        int func;
        do {
            System.out.print("請選擇功能 1)重新設定資料庫 2)查詢所有資料 3)以年齡排序: ");
            func = cin.nextInt();
            switch (func) {
                case 1:
                    DBSetting(mongodb, true);
                    break;
                case 2:
                    PrintAll(mongodb);
                    break;
                case 3:
                    PrintWithSort(mongodb);
                    break;
                case 4:
                    InsertDocument(mongodb);
                    break;
                case 5:
                    UpdateDocument(mongodb);
                    break;
                case -1:
                    break;
                default:
                    System.out.println("input error");
                    break;
            }
        }
        while (func != -1);
    }
}
