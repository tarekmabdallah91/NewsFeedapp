/*
 *
 * Copyright 2019 tarekmabdallah91@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.gmail.tarekmabdallah91.news.paging;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gmail.tarekmabdallah91.news.R;
import com.gmail.tarekmabdallah91.news.apis.APIClient;
import com.gmail.tarekmabdallah91.news.apis.APIServices;
import com.gmail.tarekmabdallah91.news.models.articles.Article;
import com.gmail.tarekmabdallah91.news.models.countryNews.ResponseCountryNews;
import com.gmail.tarekmabdallah91.news.models.section.ResponseSection;
import com.gmail.tarekmabdallah91.news.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.gmail.tarekmabdallah91.news.utils.Constants.IS_COUNTRY_SECTION;
import static com.gmail.tarekmabdallah91.news.utils.Constants.ONE;
import static com.gmail.tarekmabdallah91.news.utils.Constants.QUERY_Q_KEYWORD;
import static com.gmail.tarekmabdallah91.news.utils.Constants.RX_KEYWORD;
import static com.gmail.tarekmabdallah91.news.utils.Constants.TWO;
import static com.gmail.tarekmabdallah91.news.utils.Constants.ZERO;
import static com.gmail.tarekmabdallah91.news.utils.ViewsUtils.getQueriesMap;

public class ItemDataSource extends PageKeyedDataSource<Integer, Article> {

    private Activity activity;
    private String sectionId, searchKeyword;
    private MutableLiveData<NetworkState> networkState;
    private boolean isCountrySection;
    private Throwable noConnectionThrowable;

    ItemDataSource(Activity activity, String sectionId, String searchKeyword){
        this.activity = activity;
        this.sectionId = sectionId;
        this.searchKeyword = searchKeyword;
        isCountrySection = activity.getIntent().getBooleanExtra(IS_COUNTRY_SECTION,false);
        networkState = new MutableLiveData<>();
        noConnectionThrowable = new Throwable(activity.getString(R.string.no_connection));
    }

    //this will be called once to load the initial data
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Article> callback) {
        networkState.postValue(NetworkState.LOADING);
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(RX_KEYWORD, "onSubscribe" + d.isDisposed());
            }

            @Override
            public void onNext(Object body) {
                Log.d(RX_KEYWORD, "onNext" + body.toString());
                List<Article> articles = new ArrayList<>();
                if (body instanceof ResponseSection){
                    ResponseSection responseSection = (ResponseSection) body;
                    articles = responseSection.getResponse().getResults();
                }else if (body instanceof ResponseCountryNews){
                    ResponseCountryNews responseCountryNews = (ResponseCountryNews) body;
                    articles = responseCountryNews.getResponse().getResults();
                }
                networkState.postValue(NetworkState.LOADED);
                callback.onResult(articles, null , TWO );
            }

            @Override
            public void onError(Throwable e) {
                handelFailureCase(e);
            }

            @Override
            public void onComplete() {
                Log.d(RX_KEYWORD, "onComplete");
            }
        };
        getObservable(ONE).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    //this will load the previous page
    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Article> callback) {
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(RX_KEYWORD, "onSubscribe" + d.isDisposed());
            }

            @Override
            public void onNext(Object body) {
                Log.d(RX_KEYWORD, "onNext" + body.toString());
                List<Article> articles = new ArrayList<>();
                if (body instanceof ResponseSection){
                    ResponseSection responseSection = (ResponseSection) body;
                    articles = responseSection.getResponse().getResults();
                }else if (body instanceof ResponseCountryNews){
                    ResponseCountryNews responseCountryNews = (ResponseCountryNews) body;
                    articles = responseCountryNews.getResponse().getResults();
                }
                Integer adjacentKey = (params.key > ONE) ? params.key - ONE : null;
                networkState.postValue(NetworkState.LOADED);
                callback.onResult(articles, adjacentKey );
            }

            @Override
            public void onError(Throwable e) {
                handelFailureCase(e);
            }

            @Override
            public void onComplete() {
                Log.d(RX_KEYWORD, "onComplete");
            }
        };
        getObservable(params.key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    //this will load the next page
    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Article> callback) {
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(RX_KEYWORD, "onSubscribe" + d.isDisposed());
            }

            @Override
            public void onNext(Object body) {
                Log.d(RX_KEYWORD, "onNext" + body.toString());
                List<Article> articles = new ArrayList<>();
                int pagesNumber = ZERO;
                if (body instanceof ResponseSection){
                    ResponseSection responseSection = (ResponseSection) body;
                    pagesNumber = responseSection.getResponse().getPages();
                    articles = responseSection.getResponse().getResults();
                }else if (body instanceof ResponseCountryNews){
                    ResponseCountryNews responseCountryNews = (ResponseCountryNews) body;
                    pagesNumber = responseCountryNews.getResponse().getPages();
                    articles = responseCountryNews.getResponse().getResults();
                }
                Integer key = pagesNumber > params.key ? params.key + ONE : null;
                networkState.postValue(NetworkState.LOADED);
                callback.onResult(articles, key);
            }

            @Override
            public void onError(Throwable e) {
                handelFailureCase(e);
            }

            @Override
            public void onComplete() {
                Log.d(RX_KEYWORD, "onComplete");
            }
        };
        getObservable(params.key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    private Observable getObservable(int pageNumber){
        APIServices apiServices = APIClient.getAPIServices(activity);
        Map<String, Object> queries = getQueriesMap(activity, pageNumber);
        if (null != searchKeyword) queries.put(QUERY_Q_KEYWORD, searchKeyword);
        if (isCountrySection) return apiServices.getCountrySection(sectionId, queries);
        else return apiServices.getArticlesBySection(sectionId, queries);
    }

    MutableLiveData getNetworkState() {
        return networkState;
    }

    private void handelFailureCase(Throwable t){
        Log.d(RX_KEYWORD, "onError" + t.getMessage());
        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));
    }
}