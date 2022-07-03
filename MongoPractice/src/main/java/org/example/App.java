package org.example;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void printAll(MongoCollection collection){
        MongoCursor cursor = collection.find().iterator();
        System.out.println("print all document");
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
    }
    public static void printWithChooseFields(MongoCollection collection){

        Bson projectionFields = Projections.fields(
                Projections.include("name", "age"),
                Projections.excludeId());
        MongoCursor cursor1 = collection.find().projection(projectionFields).iterator();
        FindIterable cursor2 = collection.find().projection(projectionFields);
        System.out.println("print with choose fields:");
        while(cursor1.hasNext()){
            System.out.println(cursor1.next());
        }
        System.out.println("transform to Json");
        cursor2.forEach(a->{
            Document doc = (Document)a;
            JSONObject json = JSONObject.parseObject(doc.toJson());
            System.out.println(json.getString("name"));
        });
    }
    public static void printWithSort(MongoCollection collection){
        MongoCursor mongoCursor_ascend = collection.find().sort(Sorts.ascending("name")).iterator();
        MongoCursor mongoCursor_descend = collection.find().sort(Sorts.descending("age")).iterator();
        System.out.println("Sort with ascending name:");
        while(mongoCursor_ascend.hasNext()){
            System.out.println(mongoCursor_ascend.next());
        }
        System.out.println("Sort with descending age");
        while(mongoCursor_descend.hasNext()){
            System.out.println(mongoCursor_descend.next());
        }
    }
    public static void main( String[] args )
    {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("TEST");
            MongoCollection<Document> collection = mongoDatabase.getCollection("test");
            printAll(collection);
            printWithSort(collection);
            printWithChooseFields(collection);
        }catch (Exception e){
            System.out.println("資料庫連接問題: \n"+e.getMessage());
        }
    }
}
