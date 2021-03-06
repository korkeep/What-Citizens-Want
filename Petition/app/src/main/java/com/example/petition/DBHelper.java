package com.example.petition;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    //DBHelper 생성자 (관리할 DB 이름, 버전 정보)
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i("[DBHelper: DBHelper]", "생성자가 호출됩니다.");
    }

    //DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //새로운 테이블 생성 (ID, BEGIN_DATE, END_DATE, CATEGORY, TITLE, KEYWORD, AGREE, LIKED)
        sqLiteDatabase.execSQL("CREATE TABLE PETITION (ID TEXT PRIMARY KEY, BEGIN_DATE TEXT, END_DATE TEXT, CATEGORY TEXT, TITLE TEXT, KEYWORD TEXT, AGREE INTEGER, LIKED INTEGER);");
        Log.i("[DBHelper: onCreate]", "테이블을 불러옵니다.");
    }

    //DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    // 레코드 추가, Primary Key 이용해 중복 처리 (FirstActivity)
    public void Insert(Petition data) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO PETITION VALUES('"+ data.getID()+"', '"+data.getBegin()+"', '"+data.getEnd()+ "', '"+data.getCategory()+"', '"+data.getTitle()+"', '"+data.getKeyword()+"', '"+data.getAgree()+"', 0);");
        Log.i("[DBHelper: insert]", "레코드가 추가되었습니다.");
        db.close();
    }

    // 키워드 검색 레코드 출력 (FragmentHome)
    public String get_HomeData(String search) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (ID, BEGIN_DATE, END_DATE, CATEGORY, TITLE, KEYWORD, AGREE, LIKED)
        String mQuery = "SELECT * FROM PETITION WHERE KEYWORD LIKE '%"+search+"%' ORDER BY AGREE DESC";
        Cursor cursor = db.rawQuery(mQuery, null);

        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //ID
                    + '\t'
                    + cursor.getString(1)   //BEGIN_DATE
                    + '\t'
                    + cursor.getString(2)   //END_DATE
                    + '\t'
                    + cursor.getString(3)   //CATEGORY
                    + '\t'
                    + cursor.getString(4)   //TITLE
                    + '\t'
                    + cursor.getString(5)   //KEYWORD
                    + '\t'
                    + cursor.getInt(6)      //AGREE
                    + '\t'
                    + cursor.getInt(7)      //LIKED
                    + '\n';
        }
        Log.i("[DBHelper: get_HomeData]", '\n' + result);
        db.close();
        return result;
    }

    // 좋아요 체크여부 반환 (FragmentHome, FragmentLike)
    public boolean get_Liked(String ID) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (ID, BEGIN_DATE, END_DATE, CATEGORY, TITLE, KEYWORD, AGREE, LIKED)
        Cursor cursor = db.rawQuery("SELECT LIKED FROM PETITION WHERE ID=?", new String[]{ID});
        while (cursor.moveToNext()) {
            result += cursor.getInt(0);   //LIKED
        }
        db.close();

        if(Integer.parseInt(result) == 1){
            Log.i("[DBHelper: getResult_Liked]", ID + "는 좋아요 클릭한 청원입니다.");
            return true;
        } else {
            Log.i("[DBHelper: getResult_Liked]", ID + "는 좋아요 클릭한 청원이 아닙니다.");
            return false;
        }
    }

    // 좋아요 업데이트 (FragmentHome, FragmentLike)
    public void update_Liked(String ID) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        String temp ="";

        // 현재 좋아요 여부 가져오기
        Cursor cursor = db.rawQuery("SELECT LIKED FROM PETITION WHERE ID=?", new String[] {ID});
        while (cursor.moveToNext()) {
            temp += cursor.getInt(0);    //PLAYED
        }
        int result = 0;
        if(Integer.parseInt(temp) == 0){
            result = 1;
        }

        // 좋아요 여부 업데이트
        db.execSQL("UPDATE PETITION SET LIKED=" + result + " WHERE ID='" + ID + "';");
        Log.i("[DBHelper: update_Liked]", Integer.toString(result));
        db.close();
    }

    // 제목 순으로 레코드 출력 (FragmentLike)
    public String sort_Title() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (ID, BEGIN_DATE, END_DATE, CATEGORY, TITLE, KEYWORD, AGREE, LIKED)
        Cursor cursor = db.rawQuery("SELECT * FROM PETITION WHERE LIKED LIKE 1 ORDER BY TITLE", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //ID
                    + '\t'
                    + cursor.getString(1)   //BEGIN_DATE
                    + '\t'
                    + cursor.getString(2)   //END_DATE
                    + '\t'
                    + cursor.getString(3)   //CATEGORY
                    + '\t'
                    + cursor.getString(4)   //TITLE
                    + '\t'
                    + cursor.getString(5)   //KEYWORD
                    + '\t'
                    + cursor.getInt(6)      //AGREE
                    + '\t'
                    + cursor.getInt(7)      //LIKED
                    + '\n';
        }
        Log.i("[DBHelper: sort_Title]", '\n' + result);
        db.close();
        return result;
    }

    // 청원 날짜 순으로 레코드 출력 (FragmentLike)
    public String sort_Category() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (ID, BEGIN_DATE, END_DATE, CATEGORY, TITLE, KEYWORD, AGREE, LIKED)
        Cursor cursor = db.rawQuery("SELECT * FROM PETITION WHERE LIKED LIKE 1 ORDER BY CATEGORY", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //ID
                    + '\t'
                    + cursor.getString(1)   //BEGIN_DATE
                    + '\t'
                    + cursor.getString(2)   //END_DATE
                    + '\t'
                    + cursor.getString(3)   //CATEGORY
                    + '\t'
                    + cursor.getString(4)   //TITLE
                    + '\t'
                    + cursor.getString(5)   //KEYWORD
                    + '\t'
                    + cursor.getInt(6)      //AGREE
                    + '\t'
                    + cursor.getInt(7)      //LIKED
                    + '\n';
        }
        Log.i("[DBHelper: sort_Category]", '\n' + result);
        db.close();
        return result;
    }

    // 공감 인원 순으로 레코드 출력 (FragmentLike)
    public String sort_Agree() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (ID, BEGIN_DATE, END_DATE, CATEGORY, TITLE, KEYWORD, AGREE, LIKED)
        Cursor cursor = db.rawQuery("SELECT * FROM PETITION WHERE LIKED LIKE 1 ORDER BY AGREE DESC", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //ID
                    + '\t'
                    + cursor.getString(1)   //BEGIN_DATE
                    + '\t'
                    + cursor.getString(2)   //END_DATE
                    + '\t'
                    + cursor.getString(3)   //CATEGORY
                    + '\t'
                    + cursor.getString(4)   //TITLE
                    + '\t'
                    + cursor.getString(5)   //KEYWORD
                    + '\t'
                    + cursor.getInt(6)      //AGREE
                    + '\t'
                    + cursor.getInt(7)      //LIKED
                    + '\n';
        }
        Log.i("[DBHelper: sort_Agree]", '\n' + result);
        db.close();
        return result;
    }

    //내림차순으로 항목 리턴 (FragmentDate)
    public static List sortByValue(final Map map) {
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list,new Comparator() {

            public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        //Collections.reverse(list); // 주석시 오름차순
        return list;
    }

    //청원수가 많은거 출력하는거 (FragmentDate)
    public Iterator returnSortedKeyForAgree(String a, String b) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        //변수들 선언
        String raw_keyword; //키워드 쓸수 있을때까지 변경할때만 사용
        int raw_agree;//동의수
        HashMap<String,Integer> hsMap1 = new HashMap<>();//키워드 반복 회수
        HashMap<String,Integer> hsMap2 = new HashMap<>();//키워드 동의수

        //Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >= '" + a + "' AND <= '" + b + "';", null);
        Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >=? AND END_DATE <= ?", new String[] {a, b});

        while (cursor.moveToNext()) {
            //keyword를 데이터베이스에서 읽어와 []를 없애고 , 기준으로 split한다.
            raw_keyword = cursor.getString(0);
            String mid[]=raw_keyword.split(",");

            //동의 수를 저장한다.
            raw_agree=cursor.getInt(1);

            for (String word : mid){
                //String tmp = word.substring(1,word.length()-1);
                String tmp=word;
                if(hsMap1.containsKey(word)){
                    hsMap1.put(tmp,hsMap1.get(tmp)+1);
                    hsMap2.put(tmp,hsMap2.get(tmp)+raw_agree);
                } else{
                    hsMap1.put(tmp,1);
                    hsMap2.put(tmp,raw_agree);
                }
            }
            /*raw_keyword=cursor.getString(0);
            Log.i("로우키",raw_keyword);*/
        }
        Log.i("[ReturnSortedKey]","While 끝");

        Iterator it = sortByValue(hsMap1).iterator();
        db.close();
        return it;
    }

    //동의수가 많은 것 출력하는 거 (FragmentDate)
    public Iterator returnSortedKeyForTotal(String a, String b) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        //변수들 선언
        String raw_keyword; //키워드 쓸수 있을때까지 변경할때만 사용
        int raw_agree;//동의수
        HashMap<String,Integer> hsMap1 = new HashMap<>();//키워드 반복 회수
        HashMap<String,Integer> hsMap2 = new HashMap<>();//키워드 동의수

        //Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >= '" + a + "' AND <= '" + b + "';", null);
        Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >=? AND END_DATE <= ?", new String[] {a, b});
        while (cursor.moveToNext()) {
            //keyword를 데이터베이스에서 읽어와 []를 없애고 , 기준으로 split한다.
            raw_keyword = cursor.getString(0);
            String mid[]=raw_keyword.split(",");

            //동의 수를 저장한다.
            raw_agree=cursor.getInt(1);

            for (String word : mid){
                //String tmp = word.substring(1,word.length()-1);
                String tmp=word;
                if(hsMap1.containsKey(word)){
                    hsMap1.put(tmp,hsMap1.get(tmp)+1);
                    hsMap2.put(tmp,hsMap2.get(tmp)+raw_agree);
                } else{
                    hsMap1.put(tmp,1);
                    hsMap2.put(tmp,raw_agree);
                }
            }
        }
        Log.i("[ReturnSortedKeyForTotal]","While 끝");

        Iterator it = sortByValue(hsMap2).iterator();
        db.close();
        return it;
    }

    //TO-DO (FragmentDate)
    public HashMap<String,Integer> returnHashMapForAgree(String a, String b) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        //변수들 선언
        String raw_keyword; //키워드 쓸수 있을때까지 변경할때만 사용
        int raw_agree;//동의수
        HashMap<String,Integer> hsMap1 = new HashMap<>();//키워드 반복 회수
        HashMap<String,Integer> hsMap2 = new HashMap<>();//키워드 동의수

        //Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >= '" + a + "' AND <= '" + b + "';", null);
        Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >=? AND END_DATE <= ?", new String[] {a, b});
        while (cursor.moveToNext()) {
            //keyword를 데이터베이스에서 읽어와 []를 없애고 , 기준으로 split한다.
            raw_keyword = cursor.getString(0);
            String mid[]=raw_keyword.split(",");

            //동의 수를 저장한다.
            raw_agree=cursor.getInt(1);

            for (String word : mid){
                // String tmp = word.substring(1,word.length()-1);
                String tmp=word;
                if(hsMap1.containsKey(word)){
                    hsMap1.put(tmp,hsMap1.get(tmp)+1);
                    hsMap2.put(tmp,hsMap2.get(tmp)+raw_agree);
                } else{
                    hsMap1.put(tmp,1);
                    hsMap2.put(tmp,raw_agree);
                }
            }
        }
        Log.i("[ReturnHashMapForAgree]","While 끝");
        db.close();
        return hsMap1;
    }

    // TO-DO (FragmentDate)
    public HashMap<String,Integer> returnHashMapForTotal(String a, String b) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        //변수들 선언
        String raw_keyword; //키워드 쓸수 있을때까지 변경할때만 사용
        int raw_agree;//동의수
        HashMap<String,Integer> hsMap1 = new HashMap<>();//키워드 반복 회수
        HashMap<String,Integer> hsMap2 = new HashMap<>();//키워드 동의수

        //Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >= '" + a + "' AND <= '" + b + "';", null);
        Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE END_DATE >=? AND END_DATE <= ?", new String[] {a, b});
        while (cursor.moveToNext()) {
            //keyword를 데이터베이스에서 읽어와 []를 없애고 , 기준으로 split한다.
            raw_keyword = cursor.getString(0);
            String mid[]=raw_keyword.split(",");

            //동의 수를 저장한다.
            raw_agree=cursor.getInt(1);

            for (String word : mid){
                //String tmp = word.substring(1,word.length()-1);
                String tmp=word;
                if(hsMap1.containsKey(word)){
                    hsMap1.put(tmp,hsMap1.get(tmp)+1);
                    hsMap2.put(tmp,hsMap2.get(tmp)+raw_agree);
                } else{
                    hsMap1.put(tmp,1);
                    hsMap2.put(tmp,raw_agree);
                }
            }
        }
        Log.i("[ReturnHashMapForTotal]","While 끝");
        db.close();
        return hsMap2;
    }

    public HashMap<DualKey,Integer> returnHashMapForRelate(String a, String b) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        //변수들 선언
        String raw_keyword; //키워드 쓸수 있을때까지 변경할때만 사용
        int raw_agree;//동의수
        HashMap<DualKey,Integer> hsMap3 = new HashMap<>();

        Cursor cursor = db.rawQuery("SELECT KEYWORD, AGREE, END_DATE FROM PETITION WHERE (AGREE>=50000) AND (END_DATE >=? AND END_DATE <= ?)", new String[] {a, b});
        while (cursor.moveToNext()) {
            //keyword를 데이터베이스에서 읽어와 []를 없애고 , 기준으로 split한다.
            raw_keyword = cursor.getString(0);
            //Log.i("10000이상 받아온 놈들",raw_keyword);
            String mid[]=raw_keyword.split(",");

            //동의 수를 저장한다.
            raw_agree=cursor.getInt(1);

            int len=mid.length;
            for(int i=0; i<len; i++)
            {
                for(int j=i+1; j<len; j++)
                {
                    String word = mid[i];
                    String compareWord=mid[j];
                    DualKey x1=new DualKey(word,compareWord);
                    DualKey x2=new DualKey(compareWord,word);

                    if(hsMap3.containsKey(x1)) {
                        Log.i("집어넣기 ","시작");
                        hsMap3.put(x1,hsMap3.get(x1)+raw_agree);
                    }else{
                        hsMap3.put(x1,raw_agree);
                    }
                    if(hsMap3.containsKey(x2)) {
                        Log.i("집어넣기 ","시작");
                        hsMap3.put(x2,hsMap3.get(x2)+raw_agree);
                    }else{
                        hsMap3.put(x2,raw_agree);
                    }
                }
            }
        }
        Log.i("get_max3","와일문 끝");
        db.close();
        return hsMap3;
    }
}
