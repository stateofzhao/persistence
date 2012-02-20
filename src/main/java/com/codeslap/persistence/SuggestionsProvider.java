package com.codeslap.persistence;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.SoftReference;
import java.util.*;

/**
 * This is a content provider that can be used to get suggestions for the Android's search engine
 *
 * @author cristian
 */
public abstract class SuggestionsProvider<T> extends ContentProvider {

    private static final Map<Object, SoftReference<SuggestionInfo>> suggestions = new WeakHashMap<Object, SoftReference<SuggestionInfo>>();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return String.format("vnd.android.cursor.item/vnd.%s.%s", getContext().getPackageName(), getClass().getSimpleName());
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String where, String[] whereArgs, String sortOrder) {
        // retrieve elements using the search filter
        List<T> findAll = new ArrayList<T>();
        String query = uri.getLastPathSegment();
        if (!SearchManager.SUGGEST_URI_PATH_QUERY.equals(query) && query != null && !query.isEmpty()) {
            List<T> ts = queryItems(query);
            findAll.addAll(ts);
        }

        // build the list of suggestions
        List<SuggestionInfo> suggestionInfos = new ArrayList<SuggestionInfo>();
        for (T object : findAll) {
            SoftReference<SuggestionInfo> soft;
            if (suggestions.containsKey(object) && suggestions.get(object).get() != null) {
                soft = suggestions.get(object);
            } else {
                soft = new SoftReference<SuggestionInfo>(buildSuggestionInfo(object));
                suggestions.put(object, soft);
            }
            suggestionInfos.add(soft.get());
        }
        return new SuggestionsCursor(suggestionInfos);
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    /**
     * @param query the string to search for
     * @return a not-null list of elements that matches the query
     */
    public abstract List<T> queryItems(String query);

    /**
     * @param object the sample object
     * @return a suggestion based on a sample object. You must use the {@link SuggestionInfo.Builder}
     */
    public abstract SuggestionInfo buildSuggestionInfo(T object);
}