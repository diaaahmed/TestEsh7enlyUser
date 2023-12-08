package com.esh7enly.domain;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;


import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>>
{
    private final Type responseType;
    LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(@NonNull final Call<R> call)
    {
        return new LiveData<ApiResponse<R>>() {
            AtomicBoolean started = new AtomicBoolean(false);
            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {

                    call.enqueue(new Callback<R>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response)
                        {
                            postValue(new ApiResponse<>(response));
                        }

                        @Override
                        public void onFailure(@NonNull Call<R> call, @NonNull Throwable throwable)
                        {
                            //Utils.log("Failed. RAW URL: " + call.request().url());
                            postValue(new ApiResponse<>(throwable));
                        }
                    });
                }
            }
        };
    }
}