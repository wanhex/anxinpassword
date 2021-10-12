package com.wanhex.anxinpassword.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanhex.anxinpassword.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
public
class Password implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @JSONField(name = "category")
    @ColumnInfo(name = "category")
    public String category;

    @JSONField(name = "site")
    @ColumnInfo(name = "site")
    public String site;

    @JSONField(name = "abbrev_username")
    @ColumnInfo(name = "abbrev_username")
    public String abbreviatedUserName;

    @JSONField(name = "username")
    @ColumnInfo(name = "username")
    public String username;

    @JSONField(name = "password")
    @ColumnInfo(name = "password")
    public String password;

    @JSONField(name = "comments")
    @ColumnInfo(name = "comments")
    public String comments;

    @JSONField(name = "timestamp")
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

    public String getAbbreviatedUserName() {
        if (username.isEmpty()) {
            return "****";
        }
        return username.charAt(0) + "***";
    }

    public String getTimeStampStr() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
        Timestamp now = new Timestamp(timeStamp);//获取系统当前时间
        String str = df.format(now);
        return str;
    }

    public int getImageId() {
        return R.mipmap.lock;
    }

    public String getSummary() {
        return getAbbreviatedUserName() + "@" + site;
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
