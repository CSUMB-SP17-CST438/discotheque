package edu.jocruzcsumb.discotheque;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by carsen on 4/8/17.
 */

public class Theme implements Parcelable
{
    protected Theme(Parcel in)
    {
        //TODO
    }

    public static Theme parse(JSONObject jsonTheme)
    {
        //TODO
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //TODO
    }

    public static final Creator<Theme> CREATOR = new Creator<Theme>()
    {
        @Override
        public Theme createFromParcel(Parcel in)
        {
            return new Theme(in);
        }

        @Override
        public Theme[] newArray(int size)
        {
            return new Theme[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }
}
