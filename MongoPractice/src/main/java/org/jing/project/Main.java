package org.jing.project;

import java.util.Scanner;

import static org.jing.project.Collection.*;
import static org.jing.project.DBSetting.resetHost;


public class Main {


    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        DB mongodb = new DB("localhost", "27017");
        resetHost(mongodb);
        int func;
        do {
            System.out.print("請選擇功能 1)重新設定資料庫 2)資料相關 3)以年齡排序: ");
            func = cin.nextInt();
            switch (func) {
                case 1:
                    DBSetting.main(mongodb);
                    break;
                case 2:
                    Collection.main(mongodb);
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
