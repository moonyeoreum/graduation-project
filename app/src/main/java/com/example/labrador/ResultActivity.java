package com.example.labrador;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ListView medicineListView;
    private ArrayList<Medicine> medicineList;
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initializeViews();
        handleInitialQuery();
    }

    private void initializeViews() {
        medicineListView = findViewById(R.id.medicineListView);
        medicineList = new ArrayList<>();
        adapter = new MedicineAdapter(this, R.layout.medicine_item, medicineList);
        medicineListView.setAdapter(adapter);

        medicineListView.setOnItemClickListener((parent, view, position, id) -> {
            Medicine selectedMedicine = medicineList.get(position);
            Intent intent = new Intent(ResultActivity.this, MedicineDetailActivity.class);
            intent.putExtra("selectedMedicine", selectedMedicine);
            startActivity(intent);
        });
    }

    private void handleInitialQuery() {
        String initialQuery = getIntent().getStringExtra("initialQuery");
        if (initialQuery != null && !initialQuery.isEmpty()) {
            new SearchTask().execute(initialQuery);
        }
    }

    private class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String encodedSearchTerm = URLEncoder.encode(params[0], "UTF-8");
                String apiUrl = buildApiUrl(encodedSearchTerm);
                return getApiResponse(apiUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            handleSearchResult(result);
        }
    }

    private String buildApiUrl(String encodedSearchTerm) {
        // TODO: 여기에 실제 서비스 키를 입력하세요.
        String apiKey = getString(R.string.api_key);
        int numberOfRows = 100; // 원하는 행의 수
        return "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList?" +
                "serviceKey=" + apiKey +
                "&numOfRows=" + numberOfRows +
                "&itemName=" + encodedSearchTerm +
                "&type=xml";
    }


    private String getApiResponse(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }
                    return builder.toString();
                }
            } else {
                Log.e("NETWORK_ERROR", "Error response code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleSearchResult(String result) {
        Log.d("HANDLE_RESULT", "Result: " + result);
        if (result != null) {
            new ParseXmlTask().execute(result);
        } else {
            Toast.makeText(ResultActivity.this, "네트워크 에러 또는 검색 결과를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private class ParseXmlTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            parseXml(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }

    private void parseXml(String xml) {
        medicineList.clear();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            Medicine currentMedicine = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if ("item".equals(tagName)) {
                            currentMedicine = new Medicine();
                        } else if (currentMedicine != null) {
                            handleStartTag(currentMedicine, tagName, parser);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if ("item".equals(tagName) && currentMedicine != null) {
                            medicineList.add(currentMedicine);
                            currentMedicine = null;
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            Toast.makeText(ResultActivity.this, "XML 파싱 오류", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleStartTag(Medicine currentMedicine, String tagName, XmlPullParser parser)
            throws XmlPullParserException, IOException {
        switch (tagName) {
            case "itemName":
                currentMedicine.setItemName(readText(parser));
                break;
            case "entpName":
                currentMedicine.setEntpName(readText(parser));
                break;
            case "etcOtcCode":
                currentMedicine.setEtcOtcCode(readText(parser));
                break;
            case "itemImage":
                currentMedicine.setItemImage(readText(parser));
                break;
            case "useMethodQesitm":
                currentMedicine.setUseMethodQesitm(readText(parser));
                break;
            case "atpnQesitm":
                currentMedicine.setAtpnQesitm(readText(parser));
                break;
        }
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = "";
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
        }
        return text;
    }
}




