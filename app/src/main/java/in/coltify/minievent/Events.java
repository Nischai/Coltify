package in.coltify.minievent;

/**
 * Created by Home on 31-08-2017.
 */

public class Events {
    private String Uid,Time,PosterUri,Name,Mobile,Location,Description,Date;

    public Events(){

    }
    public Events(String uid, String time, String posterUri, String name, String mobile,
                  String location, String description, String date) {
        Uid = uid;
        Time = time;
        PosterUri = posterUri;
        Name = name;
        Mobile = mobile;
        Location = location;
        Description = description;
        Date = date;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPosterUri() {
        return PosterUri;
    }

    public void setPosterUri(String posterUri) {
        PosterUri = posterUri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
