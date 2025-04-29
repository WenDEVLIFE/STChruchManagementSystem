/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Administrator
 */
public class ReservationModel {
    private String reservationID;
    private String event;
    private String date;
    private String time;
    private String status;
    private String reason;
    String name;

    public ReservationModel(String reservationID, String event, String date, String time, String status, String reason) {
        this.reservationID = reservationID;
        this.event = event;
        this.date = date;
        this.time = time;
        this.status = status;
        this.reason = reason;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ReservationModel{" + "reservationID=" + reservationID + ", event=" + event + ", date=" + date + ", time=" + time + ", status=" + status + ", reason=" + reason + '}';
    }

    public String getName() {
        return name;
    }
}

