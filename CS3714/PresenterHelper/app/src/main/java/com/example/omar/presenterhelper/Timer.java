package com.example.omar.presenterhelper;

/**
 * Created by omar on 10/1/17.
 */

public class Timer {
    int seconds = 0;
    int hours = 0;
    int minutes = 0;
    public Timer(int s, int m, int h) {
        seconds = s;
        minutes = m;
        hours = h;
    }

    public Timer() {
        this(0, 0, 0);
    }
    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void count() {
        if (seconds == 60) {
            minutes++;
            seconds = 0;
        }
        if (minutes == 60) {
            hours++;
            minutes = 0;
        }
        seconds++;
    }

    public String toString() {
        return String.format("%02d", hours) +
                ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
