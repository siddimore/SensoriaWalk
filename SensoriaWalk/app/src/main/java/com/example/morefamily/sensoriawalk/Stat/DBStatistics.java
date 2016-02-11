package com.example.morefamily.sensoriawalk.Stat;

/**
 * Created by MoreFamily on 1/10/2016.
 */
public class DBStatistics {

    protected int id;
    protected String mSpeed;
    protected String mStartTime;
    protected String mEndTime;
    private String mSteps;
    private String mDistance;

    public String getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public String getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getmSteps() {
        return mSteps;
    }

    public void setmSteps(String mSteps) {
        this.mSteps = mSteps;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(String mSpeed) {
        this.mSpeed = mSpeed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

        public DBStatistics(){}

        public DBStatistics(String startTime, String endTime, String speed, String distance, String steps) {
            super();
            this.mStartTime = startTime;
            this.mEndTime = endTime;
            this.mSpeed = speed;
            this.mDistance = distance;
            this.mSteps = steps;
        }

        //getters & setters

        @Override
        public String toString() {
            return "stats [id=" + id + ", StartTime=" + mStartTime + ", EndTime=" + mEndTime +", Steps=" + mSteps + ", Distance=" + mDistance
                    + ", Speed=" +mSpeed+ "]";
        }

    }
