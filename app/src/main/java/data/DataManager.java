package data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class DataManager {
    private SQLiteDatabase mDatabase;

    public DataManager(Context context) {
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);

        mDatabase = helper.getWritableDatabase();
    }

    public static final String TABLE_ROW_ID = "_id";
    public static final String TABLE_ROW_TITLE = "photo_title";
    public static final String TABLE_ROW_LAT = "gps_location_lat";
    public static final String TABLE_ROW_LONG = "gps_location_long";
    public static final String TABLE_ROW_TAGS = "photo_tags";
    public static final String TABLE_ROW_URI = "photo_storage_location";
    public static final String TABLE_ROW_TAG = "tags_tag";

    private static final String DB_NAME = "database_of_photos";
    private static final int DB_VERSION = 1;
    private static final String TABLE_PHOTOS = "table_for_photos";
    private static final String TABLE_TAGS = "table_for_tags";

    private Gson mGsonSerializer = new Gson();

    public void addPhoto(Photo photo) {
        String query = "INSERT INTO " + TABLE_PHOTOS + " (" + TABLE_ROW_TITLE + ", " + TABLE_ROW_TAGS + ", " +
                TABLE_ROW_URI + ", " + TABLE_ROW_LAT + ", " + TABLE_ROW_LONG + ") " + "VALUES (" +
                "'" + photo.getmTitle() + "'" + ", " + "'" + photo.getmTags() + "'" + ", " + "'" + photo.getmStorageLocation() + "'" + ", " +
                photo.getmGPSLocation().getLatitude() + ", " + photo.getmGPSLocation().getLongitude() + ");";

        mDatabase.execSQL(query);

        Log.i("addPhoto()", query);

        addTags(photo);
    }

    private void addTags(Photo photo) {

        ArrayList<String> tags = mGsonSerializer.fromJson(photo.getmTags(), new TypeToken<ArrayList<String>>(){}.getType());

        if (tags.isEmpty())
            return;

        for (String s : tags) {
            String query = "INSERT INTO " + TABLE_TAGS + " (" + TABLE_ROW_TAG + ") " + "SELECT '" +
                    s + "' " + "WHERE NOT EXISTS ( SELECT 1 FROM " + TABLE_TAGS + " WHERE " + TABLE_ROW_TAG + " = " +
                    "'" + s + "');";

            mDatabase.execSQL(query);

            Log.i("addTags()", query);
        }
    }

    public Cursor getAllPhotos() {
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + TABLE_PHOTOS, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getPhotosByTag(String tag) {
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + TABLE_PHOTOS + " WHERE " + TABLE_ROW_TAGS + " LIKE " + tag, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getPhotoById(int id) {
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + TABLE_PHOTOS + " WHERE " + TABLE_ROW_ID + " = " + id, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getTags() {
        Cursor c = mDatabase.rawQuery("SELECT " + TABLE_ROW_TAG + " FROM " + TABLE_TAGS, null);
        c.moveToFirst();

        return c;
    }

    public void deletePhoto(int id) {
        Cursor c = mDatabase.rawQuery("SELECT " + TABLE_ROW_TAGS + " FROM " + TABLE_PHOTOS + " WHERE " + TABLE_ROW_ID + " = " + id, null);

        c.moveToFirst();

        String tags = c.getString(c.getColumnIndex(TABLE_ROW_TAGS));

        c.close();

        ArrayList<String> tagList = mGsonSerializer.fromJson(tags, new TypeToken<ArrayList<String>>(){}.getType());

        if (!tagList.isEmpty()) {
            for (String s : tagList) {
                Cursor checkAvailability = mDatabase.rawQuery("SELECT * FROM " + TABLE_PHOTOS + " WHERE " + TABLE_ROW_TAGS + " LIKE " + s, null);

                if (!checkAvailability.moveToFirst()) {
                    deleteTag(s);
                }

                checkAvailability.close();
            }
        }

        String query = "DELETE FROM " + TABLE_PHOTOS + " WHERE " + TABLE_ROW_ID + " = " + id;

        mDatabase.execSQL(query);
    }

    private void deleteTag(String s) {
        String query = "DELETE FROM " + TABLE_TAGS + " WHERE " + TABLE_ROW_TAG + " = " + s;

        mDatabase.execSQL(query);
    }

    public void editPhoto(Photo photo) {
        //TODO(reason: not implemented)
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String createTableQuery = "create table " + TABLE_PHOTOS + " (" +
                    TABLE_ROW_ID + " integer primary key autoincrement not null," +
                    TABLE_ROW_TITLE + " text not null," + TABLE_ROW_TAGS + " text not null," +
                    TABLE_ROW_URI + " text not null," + TABLE_ROW_LAT + " real," + TABLE_ROW_LONG + " real" + ");";

            sqLiteDatabase.execSQL(createTableQuery);

            createTableQuery = "create table " + TABLE_TAGS + " (" + TABLE_ROW_ID + " integer primary key autoincrement not null," +
                    TABLE_ROW_TAG + " text not null" + ");";

            sqLiteDatabase.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}
