package activity.contacts.com.contacts;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import activity.contacts.com.contacts.com.contacts.bd.SQLHelper;
import activity.contacts.com.contacts.com.contacts.bean.ContactBean;


public class MainActivity extends AppCompatActivity {

    MainActivityFragment mainActivityFragment;
    final static String PARCEL = "CONTACT";
    final String PREFS_NAME = "activity.contacts.com.PREFERENCE_FILE_KEY";
    final static String namePrefs = "name";
    String USER = null;
    int SHARED_RESULT = 1;
    SharedPreferences settings;

    private String SEARCH_OPENED = "SEARCH_OPENED";
    private String SEARCH_QUERY="SEARCH_QUERY";

    private Drawable mIconOpenSearch;
    private Drawable mIconCloseSearch;
    private boolean mSearchOpened;
    private String mSearchQuery;
    private EditText mSearchEt;
    private MenuItem mSearchAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = settings.getString(namePrefs, "");
        if (name == "") {
            showPrompt();
            //Toast.makeText(MainActivity.this,"YEAH", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(MainActivity.this,"It is not", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState == null) {
            mSearchOpened = false;
            mSearchQuery = "";
        } else {
            mSearchOpened = savedInstanceState.getBoolean(SEARCH_OPENED);
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
        }
//        mIconOpenSearch = getResources()
//                .getDrawable(R.drawable.abc_ic_voice_search_api_mtrl_alpha);
//        mIconCloseSearch = getResources()
//                .getDrawable(R.drawable.abc_edit_text_material);


        setContentView(R.layout.activity_main);
        mainActivityFragment = new MainActivityFragment();
        getFragmentManager().beginTransaction().add(R.id.fragmentMain, mainActivityFragment, "tag").commit();

        if (mSearchOpened) {
            openSearchBar(mSearchQuery);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SEARCH_OPENED, mSearchOpened);
        outState.putString(SEARCH_QUERY, mSearchQuery);
    }

    public void onCreateContact() {
        ContactFragment contactsFragment = new ContactFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentMain, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onDropContacts() {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
        adBuilder.setTitle("Confirm delete");
        adBuilder.setMessage("Do you want to delete all your contact list");
        adBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "All deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                SQLHelper sqlHelper = new SQLHelper(MainActivity.this);
                sqlHelper.onDropTable();
                mainActivityFragment.refillList();
            }
        });
        adBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "Nothing happend", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = adBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                onCreateContact();
                return true;
            case R.id.action_deleteAll:
                onDropContacts();
                return true;
            case R.id.action_search:
                if (mSearchOpened) {
                    closeSearchBar();
                } else {
                    openSearchBar(mSearchQuery);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            showDialogForExitForm();
        else
            super.onBackPressed();
    }

    private void showDialogForExitForm() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit the form?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        getFragmentManager().popBackStack();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void passContact(ContactBean contact) {
        ContactFragment contactFragment = new ContactFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCEL, contact);
        contactFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentMain, contactFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SHARED_RESULT && resultCode == this.RESULT_OK) {
//            USER = data.getStringExtra("welcomeName");
//            if (USER != null) {
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putBoolean("my_first_time", true).commit();
//                editor.putString(namePrefs, USER);
//                editor.commit();
//            }
//        }
//    }

    public void showPrompt(){
        AlertDialog.Builder firstDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_welcome_dialog, null);
        firstDialog.setTitle("Welcome to the App");
        firstDialog.setView(view);
        final EditText textName = (EditText) view.findViewById(R.id.newUser);
        firstDialog.setPositiveButton("Accept",new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick (DialogInterface dialog,int which){
            if (textName.getText().toString().trim().length() > 0) {
                String welcomeName = textName.getText().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("my_first_time", true).commit();
                editor.putString(namePrefs, welcomeName);
                editor.commit();
                mainActivityFragment.setWelcomeName();
            } else {
                Toast.makeText(MainActivity.this, "Please enter Username", Toast.LENGTH_SHORT).show();
                showPrompt();
            }
        }
        }

        );
        firstDialog.create();
        firstDialog.show();

    }


    private void openSearchBar(String queryText) {

        // Set custom view on action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search);

        // Search edit text field setup.
        mSearchEt = (EditText) actionBar.getCustomView().findViewById(R.id.etSearch);
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MainActivityFragment mainAcF = (MainActivityFragment) getFragmentManager().findFragmentByTag("tag");
                if(mainAcF!=null){
                    mainAcF.onSearch(mSearchEt.getText().toString());
                }
            }
        });


        mSearchEt.setText(queryText);
        mSearchEt.requestFocus();

        // Change search icon accordingly.
        //mSearchAction.setIcon(mIconCloseSearch);
        mSearchOpened = true;

    }

    private void closeSearchBar() {

        // Remove custom view.
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        // Change search icon accordingly.
        //mSearchAction.setIcon(mIconOpenSearch);
        mSearchOpened = false;

    }
}
