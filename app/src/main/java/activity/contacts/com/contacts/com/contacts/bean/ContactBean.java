package activity.contacts.com.contacts.com.contacts.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class ContactBean implements Parcelable {
    private int id;
    private String name;
    private String lastName;
    private String phone;
    private String career;
    private String email;
    private String address;
    private String pathImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public ContactBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        lastName = in.readString();
        phone = in.readString();
        career = in.readString();
        email = in.readString();
        address = in.readString();
        pathImage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(career);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(pathImage);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ContactBean> CREATOR = new Parcelable.Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel in) {
            return new ContactBean(in);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };
}