package nure.hloba.maksym.lab_task5;

import java.util.Date;

public class Note {
    private String title;
    private final String description;
    private final Date dateOfCreation;
    private int id;
    private final Date dateOfAppointment;
    private final Importance importance;
    private final String imageUri;

    public String getImageUri() {
        return imageUri;
    }

    public Date getDateOfAppointment() {
        return dateOfAppointment;
    }

    public Importance getImportance() {
        return importance;
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

    public String getDescription() {
        return description;
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
        this.dateOfAppointment = dateOfAppointment;
        this.imageUri = imageUri;
    }
}
