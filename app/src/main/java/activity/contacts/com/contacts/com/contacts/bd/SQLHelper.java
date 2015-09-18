package activity.contacts.com.contacts.com.contacts.bd;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.util.Log;

import java.util.ArrayList;

import activity.contacts.com.contacts.com.contacts.bean.ContactBean;


public class SQLHelper extends SQLiteOpenHelper {

    private static final String TABLE_CONTACTS = "TABLE_CONTACTS";
    private static final String dropTableContacts = "DROP TABLE IF EXISTS " + TABLE_CONTACTS;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "CONTACTS";
    private static final String DBid = "idContact";
    private static final String DBname = "name";
    private static final String DBlast = "lastName";
    private static final String DBphone = "phone";
    private static final String DBcareer = "career";
    private static final String DBemail = "email";
    private static final String DBaddress = "address";
    private static final String DBimage = "image";

    public SQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creacion de la Tabla
        String createTableContacts =
                "CREATE TABLE TABLE_CONTACTS (idContact INTEGER primary key autoincrement, name TEXT, lastName TEXT, phone numeric, career text, email text, address text, image text nullable)";
        db.execSQL(createTableContacts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTableContacts);
        onCreate(db);
    }

    public void onDropTable(){
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(dropTableContacts);
        onCreate(sqlDB);
    }

    public void insertContact(ContactBean contact){
        SQLiteDatabase sqlDB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBname,contact.getName());
        values.put(DBlast,contact.getLastName());
        values.put(DBphone,contact.getPhone());
        values.put(DBcareer,contact.getCareer());
        values.put(DBemail, contact.getEmail());
        values.put(DBaddress, contact.getAddress());
        values.put(DBimage, contact.getPathImage());
        sqlDB.insert(TABLE_CONTACTS, null, values);
        sqlDB.close();

    }

    public ArrayList<ContactBean> onSelectContacts(){
        String query= "Select * from TABLE_CONTACTS";
        ArrayList<ContactBean> contacts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    ContactBean temp = new ContactBean(Parcel.obtain());
                    temp.setId(cursor.getInt(0));
                    temp.setName(cursor.getString(1));
                    temp.setLastName(cursor.getString(2));
                    temp.setPhone(cursor.getString(3));
                    temp.setCareer(cursor.getString(4));
                    temp.setEmail(cursor.getString(5));
                    temp.setAddress(cursor.getString(6));
                    temp.setPathImage(cursor.getString(7));
                    contacts.add(temp);
                } while (cursor.moveToNext());
            }
        return contacts;
    }

    public void updateContact(ContactBean contact){
        SQLiteDatabase sqlDB = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBname,contact.getName());
        values.put(DBlast,contact.getLastName());
        values.put(DBphone,contact.getPhone());
        values.put(DBcareer, contact.getCareer());
        values.put(DBemail, contact.getEmail());
        values.put(DBaddress, contact.getAddress());
        values.put(DBimage, contact.getPathImage());
        String where = DBid+"="+contact.getId();
        sqlDB.update(TABLE_CONTACTS, values , where , null);
    }

    public void deleteContact(ContactBean contact){
        SQLiteDatabase sqlDB = getWritableDatabase();
        String where = DBid+"="+contact.getId();
        sqlDB.delete(TABLE_CONTACTS , where , null);
    }
}
