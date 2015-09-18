package activity.contacts.com.contacts;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import activity.contacts.com.contacts.com.contacts.bd.SQLHelper;
import activity.contacts.com.contacts.com.contacts.bean.ContactBean;


public class ContactFragment extends Fragment{

    Button btnAdd, btnClearAll, btnEdit, btnDelete;
    ImageButton btnPicture, btnMaps;
    String pathImage=null;
    Uri picUri;
    int GALLERY_IMAGE_RESULT= 1;
    int MAPS_RESULT = 2;
    final static String PARCEL = "CONTACT";
    ContactBean selectedContact=null;
    TextView address;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle!=null) {
            selectedContact = bundle.getParcelable(PARCEL);
            pathImage = selectedContact.getPathImage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        onClickButtons(view);
        if(selectedContact!=null){
            onFillFormwithContact(view);
            onEnabledForm(view, false);
            btnAdd.setVisibility(View.INVISIBLE);
            btnClearAll.setEnabled(false);
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnMaps.setEnabled(false);
        }

    return  view;
    }

    public void onClickButtons(final View view){
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(view)) {
                    //Insert on DB
                    TextView name = (TextView) view.findViewById(R.id.txtName);
                    TextView last = (TextView) view.findViewById(R.id.txtLast);
                    TextView phone = (TextView) view.findViewById(R.id.txtPhone);
                    TextView mail = (TextView) view.findViewById(R.id.txtMail);
                    address = (TextView) view.findViewById(R.id.txtAddress);
                    TextView career = (TextView) view.findViewById(R.id.txtCareer);

                    ContactBean contact = new ContactBean(Parcel.obtain());
                    contact.setName(name.getText().toString());
                    contact.setLastName(last.getText().toString());
                    contact.setPhone(phone.getText().toString());
                    contact.setCareer(career.getText().toString());
                    contact.setEmail(mail.getText().toString());
                    contact.setAddress(address.getText().toString());
                    contact.setPathImage(pathImage);

                    SQLHelper sqlHelper = new SQLHelper(getActivity());
                    sqlHelper.insertContact(contact);

                    onEnabledForm(view, false);
                    btnAdd.setEnabled(false);
                    btnClearAll.setEnabled(false);
                    btnMaps.setEnabled(false);

                    //Media
                    playSound("ADD");

                    //Notification
                    sendNotification();

                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
                    adBuilder.setTitle("Contacts");
                    adBuilder.setMessage("Correctly Added").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = adBuilder.create();
                    alertDialog.show();

                }
            }
        });

        btnClearAll = (Button) view.findViewById(R.id.btnClear);
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView name = (TextView) view.findViewById(R.id.txtName);
                name.setText("");
                name.setHint("Name");
                TextView last = (TextView) view.findViewById(R.id.txtLast);
                last.setText("");
                last.setHint("Last Name");
                TextView phone = (TextView) view.findViewById(R.id.txtPhone);
                phone.setText("");
                phone.setHint("Phone number");
                TextView mail = (TextView) view.findViewById(R.id.txtMail);
                mail.setText("");
                mail.setHint("Email");
                address = (TextView) view.findViewById(R.id.txtAddress);
                address.setText("");
                address.setHint("Address");
                TextView career = (TextView) view.findViewById(R.id.txtCareer);
                career.setText("");
                career.setHint("Career");
                pathImage=null;
                if(selectedContact!=null)
                    selectedContact.setPathImage(null);
                btnPicture.setImageResource(R.mipmap.profile);
                playSound("CLEAR");
            }
        });

        btnPicture = (ImageButton) view.findViewById(R.id.btnImage);
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChooserForImage();
            }
        });

        btnMaps = (ImageButton) view.findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddLocationActivity.class);
                startActivityForResult(intent, MAPS_RESULT);
            }
        });

        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnEdit.getText().toString().contentEquals("Edit")) {
                    onEnabledForm(view, true);
                    btnEdit.setText("Save");
                    btnClearAll.setEnabled(true);
                    btnMaps.setEnabled(true);
                }else if(btnEdit.getText().toString().contentEquals("Save")) {
                    if (validateFields(view)) {
                        //Update on DB
                        TextView name = (TextView) view.findViewById(R.id.txtName);
                        TextView last = (TextView) view.findViewById(R.id.txtLast);
                        TextView phone = (TextView) view.findViewById(R.id.txtPhone);
                        TextView mail = (TextView) view.findViewById(R.id.txtMail);
                        TextView address = (TextView) view.findViewById(R.id.txtAddress);
                        TextView career = (TextView) view.findViewById(R.id.txtCareer);

                        selectedContact.setName(name.getText().toString());
                        selectedContact.setLastName(last.getText().toString());
                        selectedContact.setPhone(phone.getText().toString());
                        selectedContact.setCareer(career.getText().toString());
                        selectedContact.setEmail(mail.getText().toString());
                        selectedContact.setAddress(address.getText().toString());
                        selectedContact.setPathImage(pathImage);

                        SQLHelper sqlHelper = new SQLHelper(getActivity());
                        sqlHelper.updateContact(selectedContact);

                        onEnabledForm(view, false);
                        btnEdit.setText("Edit");
                        btnClearAll.setEnabled(false);
                        btnMaps.setEnabled(false);

                        //Media
                        playSound("EDIT");

                        //Notification
                        //sendNotification();

                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
                        adBuilder.setTitle("Contact Edited");
                        adBuilder.setMessage("Correctly Edited").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = adBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        });

        btnDelete = (Button) view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to delete the contact?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                                @SuppressWarnings("unused") final int id) {
                                SQLHelper sqlHelper = new SQLHelper(getActivity());
                                sqlHelper.deleteContact(selectedContact);
                                playSound("DELETE");
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
        });
    }

    public Boolean validateFields(View view){
        TextView name = (TextView) view.findViewById(R.id.txtName);
        TextView last = (TextView) view.findViewById(R.id.txtLast);
        TextView phone = (TextView) view.findViewById(R.id.txtPhone);
        TextView mail = (TextView) view.findViewById(R.id.txtMail);
        address = (TextView) view.findViewById(R.id.txtAddress);
        TextView career = (TextView) view.findViewById(R.id.txtCareer);
        if(name.getText().toString().equalsIgnoreCase(""))
        {
            name.setError(name.getHint()+" is required");
            return false;
        }
        if(last.getText().toString().equalsIgnoreCase(""))
        {
            last.setError(last.getHint()+" is required");
            return false;
        }
        if(phone.getText().toString().equalsIgnoreCase(""))
        {
            phone.setError(phone.getHint()+" is required");
            return false;
        }else if(phone.getText().toString().length()<8){
            phone.setError("Invalid "+phone.getHint());
            return false;
        }

        if(mail.getText().toString().equalsIgnoreCase(""))
        {
            mail.setError(mail.getHint()+" is required");
            return false;
        }else if(!checkEmail(mail.getText().toString())){
            mail.setError(mail.getHint()+" is not correct");
            return false;
        }
        if(address.getText().toString().equalsIgnoreCase(""))
        {
            address.setError(address.getHint()+" is required");
            return false;
        }
        if(career.getText().toString().equalsIgnoreCase(""))
        {
            career.setError(address.getHint() + " is required");
            return false;
        }
        return true;
    }

    public boolean checkEmail(String email)
    {
        Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void onEnabledForm(View view, boolean block){
        TextView name = (TextView) view.findViewById(R.id.txtName);
        TextView last = (TextView) view.findViewById(R.id.txtLast);
        TextView phone = (TextView) view.findViewById(R.id.txtPhone);
        TextView mail = (TextView) view.findViewById(R.id.txtMail);
        address = (TextView) view.findViewById(R.id.txtAddress);
        ImageView imageView = (ImageView) view.findViewById(R.id.btnImage);
        TextView career = (TextView) view.findViewById(R.id.txtCareer);

        name.setEnabled(block);
        last.setEnabled(block);
        phone.setEnabled(block);
        mail.setEnabled(block);
        address.setEnabled(block);
        imageView.setEnabled(block);
        career.setEnabled(block);

    }

    public void playSound(String media){
        MediaPlayer mPlayer;
        int resource=0;
        switch (media){
            case("ADD"): resource = R.raw.bonuswin;
                break;
            case("DELETE"): resource = R.raw.zeldawin;
                break;
            case("CLEAR"): resource = R.raw.playerdown;
                break;
            case("EDIT"): resource = R.raw.mario;
                break;
        }
        mPlayer= MediaPlayer.create(getActivity(), resource);
        mPlayer.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == GALLERY_IMAGE_RESULT) {
                Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_SHORT).show();
                Uri imageSelected = picUri;
                String imagePath = picUri.getPath();

                if (data != null) {
                    imageSelected = data.getData();
                    imagePath = getRealPathFromURI(getActivity(), imageSelected);
                }

                Picasso.with(getActivity())
                        .load(imageSelected)
                        .error(R.drawable.errorimage)
                        .resize(300, 300)
                        .centerCrop()
                        .into(btnPicture);
                pathImage = imagePath;
            }else if(requestCode == MAPS_RESULT)
            {
                String latitude = data.getStringExtra("latitude");
                String longitude = data.getStringExtra("longitude");
                if(latitude != null && longitude != null)
                {
                    double lat = Double.parseDouble(latitude);
                    double lon = Double.parseDouble(longitude);
                    String googleAddress = getAddress(lat, lon);
                    address = (TextView) getActivity().findViewById(R.id.txtAddress);
                    address.setText(googleAddress);
                }
            }
        }
    }


    public void onFillFormwithContact(View view){
        TextView name = (TextView) view.findViewById(R.id.txtName);
        TextView last = (TextView) view.findViewById(R.id.txtLast);
        TextView phone = (TextView) view.findViewById(R.id.txtPhone);
        TextView mail = (TextView) view.findViewById(R.id.txtMail);
        TextView address = (TextView) view.findViewById(R.id.txtAddress);
        ImageView imageView = (ImageView) view.findViewById(R.id.btnImage);
        TextView career = (TextView) view.findViewById(R.id.txtCareer);

        name.setText(selectedContact.getName());
        last.setText(selectedContact.getLastName());
        phone.setText(selectedContact.getPhone());
        mail.setText(selectedContact.getEmail());
        address.setText(selectedContact.getAddress());
        career.setText(selectedContact.getCareer());

        String pathImage = selectedContact.getPathImage();
        if(pathImage!=null){
            File imgFile = new File(pathImage);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }else{
            imageView.setImageResource(R.mipmap.profile);
        }

    }

    private String getAddress(double latitude, double longitude) {
        String add = "";
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
            {
                for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();
                     i++)
                    add += addresses.get(0).getAddressLine(i) + "\n";
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return add;
    }


    public void sendNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Contact Notification")
                        .setContentText("You just add a contact!!!");

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1766,mBuilder.build());
    }


    private void displayChooserForImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getOutputMediaFile(1);
        picUri = Uri.fromFile(file); // create
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file

        String pickTitle = "Select or take a new Picture";
        Intent chooserIntent = Intent.createChooser(takePhotoIntent, pickTitle);
        chooserIntent.putExtra
                (Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});

        startActivityForResult(chooserIntent, 1);
    }

    private  File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Contacts");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] pictureArray = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  pictureArray, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
//    private void displayChooserForImage()
//    {
//        Intent pickIntent = new Intent();
//        pickIntent.setType("image/*");
//        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
//        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
//        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
//        chooserIntent.putExtra
//                (
//                        Intent.EXTRA_INITIAL_INTENTS,
//                        new Intent[]{takePhotoIntent}
//                );
//        startActivityForResult(chooserIntent, 2);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1 && resultCode == activity.RESULT_OK)
//        {
//            String latitude = data.getStringExtra("latitude");
//            String longitude = data.getStringExtra("longitude");
//            if(latitude != null && longitude != null)
//            {
//                this.addressLatitude.setText(latitude);
//                this.addressLongitude.setText(longitude);
//
//                double lat = Double.parseDouble(latitude);
//                double lon = Double.parseDouble(longitude);
//
//                String address = getAddress(lat, lon);
//                this.contactAddress.setText(address);
//            }
//        }
//      if(requestCode == 2 && resultCode == getActivity().RESULT_OK)
//        {
//            //Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_SHORT).show();
//            Uri imageSelected = data.getData();
//            Picasso.with(getActivity())
//                    .load(imageSelected)
//                    //.error(R.drawable.error_placeholder)
//                    .resize(500, 500)
//                    .centerCrop()
//                    .into(btnPicture);
//            pathImage = getRealPathFromURI(getActivity(), imageSelected);
//            //String imagePath = getRealPathFromURI(activity, imageSelected);
//            //this.profileImagePath.setText(imagePath);
//
//        }}
//
//        public String getRealPathFromURI(Context context, Uri contentUri) {
//            Cursor cursor = null;
//            try {
//                String[] proj = { MediaStore.Images.Media.DATA };
//                cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
//                int column_index = cursor.getColumnIndex(proj[0]);
//                cursor.moveToFirst();
//                //pathImage = cursor.getString(column_index);
//                return cursor.getString(column_index);
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//                selectedImageURI = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getActivity().getContentResolver().query(selectedImageURI,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                pathImage = cursor.getString(columnIndex);
//                cursor.close();
//                File imgFile = new File(pathImage);
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                btnPicture.setImageBitmap(myBitmap);


//    public void sendNotification(){
//
//        //Intent intent = new Intent(this, );
//        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        Notification n  = new Notification.Builder(getActivity())
//                .setContentTitle("New mail from " + "test@gmail.com")
//                .setContentText("Subject")
//                .setSmallIcon(R.drawable.profile)
//                //.setContentIntent(pIntent)
//                .setAutoCancel(true)
//                //.addAction(R.drawable.icon, "Call", pIntent)
//                //.addAction(R.drawable.icon, "More", pIntent)
//                //.addAction(R.drawable.icon, "And more", pIntent).build();
//
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, n);
//
//    }
}
