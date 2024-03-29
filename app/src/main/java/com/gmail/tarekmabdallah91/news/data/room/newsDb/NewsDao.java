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

package com.gmail.tarekmabdallah91.news.data.room.newsDb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.gmail.tarekmabdallah91.news.models.newsDbPages.NewsDbPage;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert
    void addPage(NewsDbPage page);

    @Query("SELECT * FROM news")
    List<NewsDbPage> getPages(); // used to get pages count in db

    @Query("SELECT * FROM news")
    LiveData<List<NewsDbPage>> getPagesList();

    @Delete
    void deletePageFromDb(NewsDbPage page);

    @Query("DELETE FROM news ") /*WHERE `id` = :id*/
    void deletePageById(); /*String id*/

    @Query("DELETE FROM news")
    void clearNewsDb();
}
