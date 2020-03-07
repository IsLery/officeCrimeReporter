package com.telran.officecrimereporter;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean solved;
    private boolean requiresPolice;
    private String suspect;
    private String susPhone;



    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        this.id = id;
        this.date = new Date();
    }

    public String getSusPhone() {
        return susPhone;
    }

    public void setSusPhone(String susPhone) {
        this.susPhone = susPhone;
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crime crime = (Crime) o;
        return Objects.equals(id, crime.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isRequiresPolice() {
        return requiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        this.requiresPolice = requiresPolice;
    }
    public UUID getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getFormattedDate(String pattern){
        SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals(new Locale("ru").getLanguage())){
            pattern = pattern.replace("MMM d, yyyy",     "d MMM yyyy");
        }
        format.applyPattern(pattern);
        return format.format(date);
    }

    public String getFormattedTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY)+ " : "+ calendar.get(Calendar.MINUTE);
    }

    public String getPhotoFileName(){
        return "IMG_"+getId().toString() + ".jpg";
    }

    @Override
    public String toString() {
        return "Crime{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", solved=" + solved +
                ", requiresPolice=" + requiresPolice +
                ", suspect='" + suspect + '\'' +
                ", susPhone='" + susPhone + '\'' +
                '}';
    }
}
