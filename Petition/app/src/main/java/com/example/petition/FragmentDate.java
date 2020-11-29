package com.example.petition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class FragmentDate extends Fragment {
    // 변수 초기화
    private Button textView_Date1;
    private Button textView_Date2;
    private Context context;
    private ListView playlist;
    private String select = "";
    private Boolean flag = false;

    Iterator it1, it2;
    ArrayList<DateData> p_data = new ArrayList<DateData>();
    HashMap<String,Integer> hsMap1 = new HashMap<>();   // 키워드 반복 회수
    HashMap<String,Integer> hsMap2 = new HashMap<>();   // 키워드 동의수
    String date1 = "2020-02-01";
    String date2 = "2020-10-29";
    String c_date1 = "";
    String c_date2 = "";

    AsyncTask<?, ?, ?> printTask;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_date, container, false);
        textView_Date1 = (Button) v.findViewById(R.id.btnStart);
        textView_Date2 = (Button) v.findViewById(R.id.btnEnd);

        textView_Date1.setOnClickListener(this::mOnClick);
        textView_Date2.setOnClickListener(this::mOnClick);

        assert container != null;
        context = container.getContext();
        playlist = (ListView)v.findViewById(R.id.playlist);

        //Spinner 관련
        final String[] data = getResources().getStringArray(R.array.dateList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, data);
        Spinner spinner = (Spinner) v.findViewById(R.id.dateSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                flag = true;
                select = (String) adapterView.getItemAtPosition(i);
                printTask = new FragmentDate.printTask().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return v;
    }

    // 달력 버튼 클릭 이벤트 핸들러
    @SuppressLint("NonConstantResourceId")
    public void mOnClick(View v){
        String year = "";
        String month = "";
        String day = "";
        flag = true;
        switch (v.getId()){
            case R.id.btnStart:
                //DB 읽어오기
                if(!date1.equals("")){
                    String[] subStr;
                    for (int i=0; i<date1.length(); i++) {
                        subStr = date1.split("-");
                        year = subStr[0];
                        month = subStr[1];
                        day = subStr[2];
                    }
                }
                new DatePickerDialog(getContext(), mDateSetListener, Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)).show();
                Log.i("[FragmentDate]", date1);
                break;
            case R.id.btnEnd:
                if(!date2.equals("")){
                    String[] subStr;
                    for (int i=0; i<date2.length(); i++) {
                        subStr = date2.split("-");
                        year = subStr[0];
                        month = subStr[1];
                        day = subStr[2];
                    }
                }
                new DatePickerDialog(getContext(), mDateSetListener2, Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)).show();
                Log.i("[FragmentDate]", date2);
                break;
        }
    }

    // 시작 날짜 설정
    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String tmp0, tmp1, tmp2;
            tmp0=Integer.toString(year);
            if((month+1)/10==0){
                tmp1 = "0" + Integer.toString(month+1);
            }
            else
                tmp1 = Integer.toString(month+1);
            if((dayOfMonth/10)==0){
                tmp2 = "0" + Integer.toString(dayOfMonth);
            }
            else
                tmp2 = Integer.toString(dayOfMonth);
            date1 = tmp0+"-"+tmp1+"-"+tmp2;
            textView_Date1.setText(date1);
            switch (month){
                case 0:
                    tmp1="Jan";
                    break;
                case 1:
                    tmp1="Feb";
                    break;
                case 2:
                    tmp1="Mar";
                    break;
                case 3:
                    tmp1="Apr";
                    break;
                case 4:
                    tmp1="May";
                    break;
                case 5:
                    tmp1="Jun";
                    break;
                case 6:
                    tmp1="Jul";
                    break;
                case 7:
                    tmp1="Aug";
                    break;
                case 8:
                    tmp1="Sep";
                    break;
                case 9:
                    tmp1="Oct";
                    break;
                case 10:
                    tmp1="Nov";
                    break;
                case 11:
                    tmp1="Dec";
                    break;
            }
            c_date1 = tmp1+" "+tmp2+" "+tmp0;
            Log.i("[FragmentDate]", c_date1);
        }
    };

    // 종료 날짜 설정
    private final DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String tmp0, tmp1, tmp2;
            tmp0=Integer.toString(year);
            if((month+1)/10==0){
                tmp1="0" + Integer.toString(month+1);
            }
            else
                tmp1=Integer.toString(month+1);
            if((dayOfMonth/10)==0){
                tmp2 = "0" + Integer.toString(dayOfMonth);
            }
            else
                tmp2 = Integer.toString(dayOfMonth);
            date2=tmp0+"-"+tmp1+"-"+tmp2;
            textView_Date2.setText(date2);
            switch (month){
                case 0:
                    tmp1="Jan";
                    break;
                case 1:
                    tmp1="Feb";
                    break;
                case 2:
                    tmp1="Mar";
                    break;
                case 3:
                    tmp1="Apr";
                    break;
                case 4:
                    tmp1="May";
                    break;
                case 5:
                    tmp1="Jun";
                    break;
                case 6:
                    tmp1="Jul";
                    break;
                case 7:
                    tmp1="Aug";
                    break;
                case 8:
                    tmp1="Sep";
                    break;
                case 9:
                    tmp1="Oct";
                    break;
                case 10:
                    tmp1="Nov";
                    break;
                case 11:
                    tmp1="Dec";
                    break;
            }
            c_date2 = tmp1+" "+tmp2+" "+tmp0;
            Log.i("[FragmentDate]", c_date2);
        }
    };

    //출력 기능 구현
    @SuppressLint("StaticFieldLeak")
    private class printTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("[FragmentDate]", '\n' + "백그라운드 안 여기 실행된다.");
            if(flag){
                // 검색 받은 경우, p_data 다시 읽어옴
                p_data.clear();

                //DB 관련
                final DBHelper dbHelper = new DBHelper(getActivity(), "PETITION.db", null, 1);
                it1=dbHelper.returnSortedKeyForAgree(c_date1,c_date2);
                it2=dbHelper.returnSortedKeyForTotal(c_date1,c_date2);
                hsMap1=dbHelper.returnHashMapForAgree(c_date1,c_date2);
                hsMap2=dbHelper.returnHashMapForTotal(c_date1,c_date2);
                DateData tmp;

                //동의 순, 청원 순 정렬
                if (select.equals("청원순")) {
                    Log.i("[FragmentDate]", '\n' + "청원순 버튼 클릭");
                    while(it1.hasNext()) {
                        String tmp1 = (String)it1.next();
                        tmp = new DateData(tmp1,(hsMap1.get(tmp1)),(hsMap2.get(tmp1)));
                        p_data.add(tmp);
                    }
                }
                else {
                    Log.i("[FragmentDate]", '\n' + "동의순 버튼 클릭");
                    while(it2.hasNext()) {
                        String tmp2 = (String)it2.next();
                        tmp = new DateData(tmp2,(hsMap1.get(tmp2)),(hsMap2.get(tmp2)));
                        p_data.add(tmp);
                    }
                }
                flag = false;
            }
            return null;
        }

        //DB에서 읽어온 데이터를 이용해 리스트 만들기
        @Override
        protected void onPostExecute(Void result) {
            if(!p_data.equals(new ArrayList<DateData>())){
                FragmentDate.StoreListAdapter mAdapter = new FragmentDate.StoreListAdapter(context, R.layout.listview_date, p_data);
                playlist.setAdapter(mAdapter);
            }
        }
    }

    //현재 DB에 저장된 정보 출력
    public class StoreListAdapter extends ArrayAdapter<DateData> {
        private ArrayList<DateData> items;
        DateData fInfo;

        //ListView 세팅함수
        public StoreListAdapter(Context context, int textViewResourseId, ArrayList<DateData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        //ListView 출력함수
        @SuppressLint({"ViewHolder", "InflateParams"})
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            fInfo = items.get(position);
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_date, null);

            //키워드 전달
            final String Keyword = fInfo.getKeyword();
            ((TextView)v.findViewById(R.id.keyword)).setText(Keyword);

            //관련 청원 수
            final String Agree = Integer.toString(fInfo.getAgree());
            ((TextView) v.findViewById(R.id.title)).setText(Agree);

            //총 참여인원 수
            final String Total = Integer.toString(fInfo.getTotal());
            ((TextView) v.findViewById(R.id.total)).setText(Total);

            return v;
        }
    }
}