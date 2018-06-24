package data;

import android.location.Location;
import android.net.Uri;

public class Photo {

    private String mTitle;
    private String mTags;
    private Uri mStorageLocation;
    private Location mGPSLocation;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTags() {
        return mTags;
    }

    public void setmTags(String mTags) {
        this.mTags = mTags;
    }

    public Uri getmStorageLocation() {
        return mStorageLocation;
    }

    public void setmStorageLocation(Uri mStorageLocation) {
        this.mStorageLocation = mStorageLocation;
    }

    public Location getmGPSLocation() {
        return mGPSLocation;
    }

    public void setmGPSLocation(Location mGPSLocation) {
        this.mGPSLocation = mGPSLocation;
    }

}
