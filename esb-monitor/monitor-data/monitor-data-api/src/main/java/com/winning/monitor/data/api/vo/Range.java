package com.winning.monitor.data.api.vo;

public class Range {
    private int value;

    private int count;

    private double sum;

    private double avg;

    private int fails;

    public Range() {
    }

    public Range(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Range) {
            Range _o = (Range) obj;

            if (value != _o.getValue()) {
                return false;
            }

            return true;
        }

        return false;
    }

    public double getAvg() {
        return avg;
    }

    public Range setAvg(double avg) {
        this.avg = avg;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Range setCount(int count) {
        this.count = count;
        return this;
    }

    public int getFails() {
        return fails;
    }

    public Range setFails(int fails) {
        this.fails = fails;
        return this;
    }

    public double getSum() {
        return sum;
    }

    public Range setSum(double sum) {
        this.sum = sum;
        return this;
    }

    public int getValue() {
        return value;
    }

    public Range setValue(int value) {
        this.value = value;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + value;

        return hash;
    }

    public Range incCount() {
        count++;
        return this;
    }

    public Range incCount(int count) {
        this.count += count;
        return this;
    }

    public Range incFails() {
        fails++;
        return this;
    }

    public Range incFails(int fails) {
        this.fails += fails;
        return this;
    }

}
