package nure.hloba.maksym.notes;

import java.util.Date;

public class Note {
    private String title;
    private String description;
    private Date dateOfCreation;
    private int id;
    private static int lastId = 0;
    private Date dateOfAppointment;
    private Importance importance;
    private String imageUri;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Date getDateOfAppointment() {
        return dateOfAppointment;
    }

    public void setDateOfAppointment(Date dateOfAppointment) {
        this.dateOfAppointment = dateOfAppointment;
    }

    public Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Note(String title, String description, Date dateOfCreation, int id, Date dateOfAppointment, Importance importance, String imageUri) {
        this.title = title;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.id = id;
        this.dateOfAppointment = dateOfAppointment;
        this.importance = importance;
        this.imageUri = imageUri;
    }

    public Note(String title, String description, Date dateOfCreation, Date dateOfAppointment, Importance importance, String imageUri) {
        this.title = title;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.importance = importance;
        id = ++lastId;
        this.dateOfAppointment = dateOfAppointment;
        this.imageUri = imageUri;
    }
}
