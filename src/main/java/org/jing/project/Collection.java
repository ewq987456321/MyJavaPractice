package org.jing.project;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;

import com.mongodb.MongoCommandException;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Objects;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class Collection {
    public static void main(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        int func;
        do {
            System.out.print("請選擇 1)印出全部 2)印出全部(排序) 3)新增資料 4)修改資料 5)刪除資料 -1)上一頁: ");
            func = cin.nextInt();
            switch (func) {
                case 1 -> PrintAll(mongodb);
                case 2 -> PrintWithSort(mongodb);
                case 3 -> InsertDocument(mongodb);
                case 4 -> UpdateDocument(mongodb);
                case 5 -> DelDocument(mongodb);
                case -1 -> {
                }
                default -> System.out.println("輸入錯誤！");
            }
        } while (func != -1);
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
                        .iterator().forEachRemaining(a ->
                                System.out.println(JSONObject.parseObject(((Document) a).toJson())));
            } else {
                System.out.print("未找到你輸入的資料，修改失敗\n");
            }
        }
    }

    public static void DelDocument(DB mongodb) {
        Scanner cin = new Scanner(System.in);
        System.out.print("請輸入要刪除的key: ");
        String key, value;
        key = cin.next();
        System.out.print("請輸入要刪除的value: ");
        value = cin.next();
        Bson query = eq(key, value);
        System.out.println("以下為搜尋到的資料");
        mongodb.getCollection().find(query).iterator().forEachRemaining(System.out::println);
        System.out.print("確定要刪除嗎(y/n): ");
        if (!Objects.equals(cin.next(), "n")) {
            try {
                DeleteResult result = mongodb.getCollection().deleteMany(query);
                System.out.println("已刪除" + result.getDeletedCount() + "個資料");
            } catch (MongoCommandException e) {
                System.out.println(e.getErrorMessage());
            }
        }
    }

}
