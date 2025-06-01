package com.example.labrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 뷰 초기화
        initializeViews();
    }

    private void initializeViews() {
        // 검색뷰 찾기
        SearchView searchView = findViewById(R.id.searchView);

        // 검색뷰 리스너 설정
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // 검색 버튼이 눌렸을 때 호출
                    performSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // 텍스트가 변경될 때마다 호출
                    // 여기서 실시간으로 검색을 수행할 수도 있습니다.
                    performRealTimeSearch(newText);
                    return true;
                }
            });
        }
    }

    private void performSearch(String query) {

        Intent searchResultIntent = new Intent(SearchActivity.this, ResultActivity.class);
        searchResultIntent.putExtra("initialQuery", query);
        startActivity(searchResultIntent);
    }

    private void performRealTimeSearch(String newText) {
        Toast.makeText(this, "실시간 검색: " + newText, Toast.LENGTH_SHORT).show();
    }
}


