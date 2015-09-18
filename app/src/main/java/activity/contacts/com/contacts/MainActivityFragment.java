package activity.contacts.com.contacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import activity.contacts.com.contacts.com.contacts.bd.SQLHelper;
import activity.contacts.com.contacts.com.contacts.bean.ContactBean;
import activity.contacts.com.contacts.com.contacts.bean.MyArrayAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<ContactBean> contacts;
    MyArrayAdapter myAdapter;
    SQLHelper sqlHelper;
    View view;

    final String PREFS_NAME = "activity.contacts.com.PREFERENCE_FILE_KEY";
    final static String namePrefs = "name";
    TextView textSearch;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);
        onFillContacts();
        setWelcomeName();
        onSearch("");
        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        setWelcomeName(view);
//
//    }

    public void onFillContacts(){
            //Query para ir por favoritos
            sqlHelper = new SQLHelper(getActivity());
            contacts = sqlHelper.onSelectContacts();
            ListView listContacts = (ListView) view.findViewById(R.id.listContacts);
            myAdapter = new MyArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,contacts);
            listContacts.setAdapter(myAdapter);

            //textSearch = (TextView) view.findViewById(R.id.textForSearch);
     }

    public void setWelcomeName(){
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String name = settings.getString(namePrefs, "");
        //Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
        if (name != null && name != "") {
            TextView textName = (TextView) view.findViewById(R.id.welcomeName);
            textName.setText("Welcome to the app "+ name);
        }
    }

    public void onSearch(String text){
        myAdapter.getFilter().filter(text.toString().toLowerCase());
//        textSearch.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                //myAdapter.getFilter().filter(s.toString().toLowerCase());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                //myAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//               // myAdapter = new MyArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,contacts);
//               // myAdapter.getFilter().filter(s.toString().toLowerCase());
//
//               // ListView listContacts = (ListView) view.findViewById(R.id.listContacts);
//               // listContacts.setAdapter(myAdapter);
//                          }
//        });
    }

    public void refillList(){
        myAdapter.refillListView(sqlHelper.onSelectContacts());
    }
}
