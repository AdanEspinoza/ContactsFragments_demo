package activity.contacts.com.contacts.com.contacts.bean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import activity.contacts.com.contacts.MainActivity;
import activity.contacts.com.contacts.R;


public class MyArrayAdapter extends ArrayAdapter<ContactBean>  {
    Activity context;
    ArrayList<ContactBean> contactBeans;
    ArrayList<ContactBean> arrayList;

    public MyArrayAdapter(Activity context, int resource, ArrayList<ContactBean> contacts) {
        super(context, resource, contacts);
        this.context = context;
        this.contactBeans = contacts;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(contactBeans);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.contactlist, parent, false);

        ImageView imgView = (ImageView) row.findViewById(R.id.imagePic);
        String pathImage = contactBeans.get(position).getPathImage();
        if(pathImage!=null){
            File imgFile = new File(pathImage);
            Uri uriImage = Uri.fromFile(imgFile);
            Picasso.with(context)
                    .load(uriImage)
                    .error(R.drawable.errorimage)
                    .resize(300, 300)
                    .centerCrop()
                    .into(imgView);
        }else{
            imgView.setImageResource(R.mipmap.profile);
        }

        TextView tvName = (TextView) row.findViewById(R.id.textName);
        tvName.setText(contactBeans.get(position).getName());

        TextView tvLast = (TextView) row.findViewById(R.id.textLastName);
        tvLast.setText(" "+contactBeans.get(position).getLastName());

        TextView tvCareer = (TextView) row.findViewById(R.id.textCareer);
        tvCareer.setText(contactBeans.get(position).getCareer());

        ImageButton btnCall = (ImageButton) row.findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactBeans.get(position).getPhone()));
                context.startActivity(intent);

            }
        });

        ImageButton btnSend = (ImageButton) row.findViewById(R.id.btnSendMail);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sent from Contact App: ");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contactBeans.get(position).getEmail()});
                context.startActivity(intent);

            }
        });

        ImageButton btnText = (ImageButton) row.findViewById(R.id.btnSendText);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = contactBeans.get(position).getPhone();  // The number on which you want to send SMS
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));

            }
        });
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).passContact(contactBeans.get(position));
            }
        });
        return row;
    }

    public void refillListView(ArrayList<ContactBean> contactBeans){
        this.contactBeans.clear();
        this.contactBeans.addAll(contactBeans);
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                constraint = constraint.toString().toLowerCase(Locale.getDefault());
                contactBeans.clear();
                if(constraint.length() == 0){

                    contactBeans.addAll(arrayList);

                } else{

                    for(ContactBean bean: arrayList){

                        if(bean.getName().toLowerCase(Locale.getDefault()).contains(constraint)
                                ||bean.getLastName().toLowerCase(Locale.getDefault()).contains(constraint)) {
                            contactBeans.add(bean);

                        }
                    }

                }

                results.count = contactBeans.size();
                results.values = contactBeans;
                return results;
            }
        };
    }
}

