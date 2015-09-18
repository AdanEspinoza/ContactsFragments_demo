package activity.contacts.com.contacts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class WelcomeDialogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_dialog);

        AlertDialog.Builder first = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_welcome_dialog,null);
        first.setTitle("Welcome");

        //Button btnUser = (Button) this.findViewById(R.id.btnNewUser);
        final EditText textName = (EditText) this.findViewById(R.id.newUser);
        first.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (textName.getText().toString().trim().length() > 0) {
                    String welcomeName = textName.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("welcomeName", welcomeName);
                    setResult(RESULT_OK, intent);
                    //first.dismiss();
                } else {
                    Toast.makeText(WelcomeDialogActivity.this, "Please enter Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
        first.create();
        first.show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
