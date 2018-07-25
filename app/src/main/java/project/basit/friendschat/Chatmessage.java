package project.basit.friendschat;

import java.util.Date;

/**
 * Created by RAJESH on 09-06-2017.
 */

public class Chatmessage {
    private String message_Text;
    private String message_User;
    private long message_Time;

    public Chatmessage() {
    }

    public Chatmessage(String message_Text, String message_User) {
        this.message_Text = message_Text;
        this.message_User = message_User;
        message_Time=new Date().getTime();
    }

    public String getMessage_Text() {
        return message_Text;
    }

    public void setMessage_Text(String message_Text) {
        this.message_Text = message_Text;
    }

    public String getMessage_User() {
        return message_User;
    }

    public void setMessage_User(String message_User) {
        this.message_User = message_User;
    }

    public long getMessage_Time() {
        return message_Time;
    }

    public void setMessage_Time(long message_Time) {
        this.message_Time = message_Time;
    }
}
