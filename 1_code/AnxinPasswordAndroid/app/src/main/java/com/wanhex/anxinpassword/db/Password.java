package com.wanhex.anxinpassword.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.wanhex.anxinpassword.R;

@Entity
public class Password implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "site")
    public String site;

    @ColumnInfo(name = "abbrev_username")
    public String abbreviatedUserName;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "comments")
    public String comments;

    @ColumnInfo(name = "timestamp")
    public long timeStamp;

    public Password() {
    }

    public Password(String site, String username, String password, String comments) {
        this.site = site;
        this.username = username;
        this.password = password;
        this.comments = comments;
    }

    protected Password(Parcel in) {
        id = in.readInt();
        category = in.readString();
        site = in.readString();
        abbreviatedUserName = in.readString();
        username = in.readString();
        password = in.readString();
        comments = in.readString();
        timeStamp = in.readLong();
    }

    public String getTimeStampStr() {
        return "timeStamp";
    }

    public int getImageId() {
        return R.mipmap.lock;
    }

    public String getSummary() {
        return "Summary";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(category);
        dest.writeString(site);
        dest.writeString(abbreviatedUserName);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(comments);
        dest.writeLong(timeStamp);
    }

    public static final Creator<Password> CREATOR = new Creator<Password>() {
        @Override
        public Password createFromParcel(Parcel in) {

            Password password = new Password();
            password.id = in.readInt();
            password.category = in.readString();
            password.site = in.readString();
            password.abbreviatedUserName = in.readString();
            password.username = in.readString();
            password.password = in.readString();
            password.comments = in.readString();
            password.timeStamp = in.readLong();

            return password;
        }

        @Override
        public Password[] newArray(int size) {
            return new Password[size];
        }
    };

}
