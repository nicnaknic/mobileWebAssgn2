package com.example.assignment2;

public class Task {

    int taskID;
    String taskDesc;
    String deadline;
    int clientID;
    String clientName;
    String clientAddress;
    String clientBirthDate;
    String clientPhotoURL;

    public Task(int taskID, String taskDesc, String deadline, int clientID, String clientName, String clientAddress, String clientBirthDate, String clientPhotoURL  ) {
        this.taskID = taskID;
        this.taskDesc = taskDesc;
        this.deadline = deadline;
        this.clientID = clientID;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.clientBirthDate = clientBirthDate;
        this.clientPhotoURL = clientPhotoURL;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientBirthDate() {
        return clientBirthDate;
    }

    public void setClientBirthDate(String clientBirthDate) {
        this.clientBirthDate = clientBirthDate;
    }

    public String getClientPhotoURL() {
        return clientPhotoURL;
    }

    public void setClientPhotoURL(String clientPhotoURL) {
        this.clientPhotoURL = clientPhotoURL;
    }
}
