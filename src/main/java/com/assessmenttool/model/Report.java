package com.assessmenttool.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private int participants;

    private String date;

    private double passingRate;

    private double averageScore;

    private double topScore;

    public Report() {
    }

    public Report(String id, String title, int participants, String date, double passingRate, double averageScore, double topScore) {
        this.id = id;
        this.title = title;
        this.participants = participants;
        this.date = date;
        this.passingRate = passingRate;
        this.averageScore = averageScore;
        this.topScore = topScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPassingRate() {
        return passingRate;
    }

    public void setPassingRate(double passingRate) {
        this.passingRate = passingRate;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getTopScore() {
        return topScore;
    }

    public void setTopScore(double topScore) {
        this.topScore = topScore;
    }
}
