package com.example.labrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private ListView medicineListView;
    private ArrayList<Medicine> medicineList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // 뷰 초기화
        initializeViews();

        // 인텐트에서 음성으로 인식한 텍스트를 가져옴
        String recognizedText = getIntent().getStringExtra("recognizedText");

        if (recognizedText != null && !recognizedText.isEmpty()) {
            // AsyncTask를 사용하여 음성으로 인식한 텍스트를 처리
            new GetMedicineDataTask(this).executeAsyncTask(recognizedText);
        } else {
            // 음성으로 인식한 텍스트가 없을 때 처리
            Toast.makeText(SearchResultActivity.this, "음성인식 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 뷰 초기화 메서드
    private void initializeViews() {
        medicineListView = findViewById(R.id.medicineListView);
        medicineList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        // 리스트뷰 아이템 클릭 이벤트 설정
        medicineListView.setOnItemClickListener((parent, view, position, id) -> {
            // 리스트 아이템 클릭 시 처리
            Medicine selectedMedicine = medicineList.get(position);
            Intent intent = new Intent(SearchResultActivity.this, MedicineDetailActivity.class);
            intent.putExtra("selectedMedicine", selectedMedicine);
            startActivity(intent);
        });
    }

    // AsyncTask 클래스
    private static class GetMedicineDataTask extends AsyncTask<String, Void, String> {

        private WeakReference<SearchResultActivity> activityReference;

        GetMedicineDataTask(SearchResultActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 프로그레스 다이얼로그 표시
            SearchResultActivity activity = activityReference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.progressDialog.setMessage("로딩중...");
                activity.progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchTerm = params[0];
            try {
                // 검색어를 UTF-8로 인코딩
                String encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8");

                // '의약품 낱알식별정보(DB) 서비스' API 요청 URL
                String apiUrl = buildApiUrl(encodedSearchTerm);

                // API에 요청하고 응답을 문자열로 받음
                String result = getApiResponse(apiUrl);

                // JSON 파싱
                parseJson(result, activityReference.get());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // 프로그레스 다이얼로그 숨김
            SearchResultActivity activity = activityReference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.progressDialog.dismiss();
            }
        }

        // execute 대신 executeOnExecutor 사용
        public GetMedicineDataTask executeAsyncTask(String recognizedText) {
            executeOnExecutor(THREAD_POOL_EXECUTOR, recognizedText);
            return this;
        }
    }

    // API 응답을 문자열로 반환하는 메서드
    private static String getApiResponse(String apiUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(apiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // 응답을 문자열로 읽어오기
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            } else {
                return ""; // 스트림이 비어있는 경우 빈 문자열 반환
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // JSON 파싱 메서드
    private static void parseJson(String json, SearchResultActivity activity) throws JSONException {
        // JSON 데이터를 파싱하여 Medicine 객체로 만들어 리스트에 추가
        activity.medicineList.clear();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Medicine 객체 생성 부분 수정
            String itemName = jsonObject.getString("ITEM_NAME");
            String entpName = jsonObject.getString("ENTP_NAME");
            String itemImage = jsonObject.getString("ITEM_IMAGE");
            String itemSeq = jsonObject.getString("ITEM_SEQ");
            String useMethodQesitm = jsonObject.getString("USE_METHOD_QESITM");
            String atpnQesitm = jsonObject.getString("ATPN_QESITM");
            String etcOtcCode = jsonObject.getString("ETC_OTC_CODE");

            Medicine medicine = new Medicine(itemName, entpName, etcOtcCode, itemImage, useMethodQesitm, atpnQesitm);

            activity.medicineList.add(medicine);
        }

        // 어댑터를 사용하지 않고 직접 리스트뷰에 데이터 추가
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_1, activity.getMedicineNames());
        activity.medicineListView.setAdapter(adapter);
    }

    // API 요청 URL을 구성하는 메서드
    private static String buildApiUrl(String encodedSearchTerm) {
        // TODO: 여기에 실제 서비스 키를 입력하세요.
        String apiKey = "YOUR_API_KEY"; // 여기에 실제 서비스 키를 입력하세요.
        return "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService" +
                "?key=" + apiKey +
                "&item_name=" + encodedSearchTerm;
    }

    // Medicine 객체에서 의약품 이름만 가져오는 메서드
    private List<String> getMedicineNames() {
        List<String> names = new ArrayList<>();
        for (Medicine medicine : medicineList) {
            names.add(medicine.getItemName());
        }
        return names;
    }
}

