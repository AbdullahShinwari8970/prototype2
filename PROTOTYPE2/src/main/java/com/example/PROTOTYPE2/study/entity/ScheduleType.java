package com.example.PROTOTYPE2.study.entity;

public enum ScheduleType {
    INSTANT,   // token generated immediately when participant is enrolled
    ONE_TIME,  // token generated once, expires after 7 days
    DAILY,     // new token every 24 hours
    WEEKLY,    // new token every 7 days
    MONTHLY    // new token every 30 days
}
