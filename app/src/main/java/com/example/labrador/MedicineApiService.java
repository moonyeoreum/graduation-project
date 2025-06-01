package com.example.labrador;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MedicineApiService {
    @GET("getDrbEasyDrugList")
    Call<List<Medicine>> getMedicineData(
            @Query("entpName") String entpName,
            @Query("itemName") String itemName,
            @Query("useMethodQesitm") String useMethodQesitm,
            @Query("atpnQesitm") String atpnQesitm,
            @Query("itemImage") String itemImage,
            @Query("ServiceKey") String serviceKey
    );
}





