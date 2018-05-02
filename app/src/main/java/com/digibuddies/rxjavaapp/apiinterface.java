package com.digibuddies.rxjavaapp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Vikram on 13-02-2018.
 */

interface apiinterface {
    @GET("/tutorial/jsonparsetutorial.txt")
    Observable<Data> register();
}
