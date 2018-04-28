package com.winning.monitor.data.api.vo;

public class Range2 {
    private int value;

    private int count;

    private double sum;

    private double avg;

    private int fails;

    public Range2() {
    }

    public Range2(int value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Range2) {
            Range2 _o = (Range2) obj;

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

    public Range2 setAvg(double avg) {
        this.avg = avg;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Range2 setCount(int count) {
        this.count = count;
        return this;
    }

    public int getFails() {
        return fails;
    }

    public Range2 setFails(int fails) {
        this.fails = fails;
        return this;
    }

    public double getSum() {
        return sum;
    }

    public Range2 setSum(double sum) {
        this.sum = sum;
        return this;
    }

    public int getValue() {
        return value;
    }

    public Range2 setValue(int value) {
        this.value = value;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + value;

        return hash;
    }

    public Range2 incCount() {
        count++;
        return this;
    }

    public Range2 incCount(int count) {
        this.count += count;
        return this;
    }

    public Range2 incFails() {
        fails++;
        return this;
    }

    public Range2 incFails(int fails) {
        this.fails += fails;
        return this;
    }

}
