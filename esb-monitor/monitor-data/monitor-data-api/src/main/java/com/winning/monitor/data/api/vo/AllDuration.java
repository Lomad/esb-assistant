package com.winning.monitor.data.api.vo;

public class AllDuration {
    private int value;

    private int count;

    public AllDuration() {
    }

    public AllDuration(int value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AllDuration) {
            AllDuration _o = (AllDuration) obj;

            if (value != _o.getValue()) {
                return false;
            }

            return true;
        }

        return false;
    }

    public int getCount() {
        return count;
    }

    public AllDuration setCount(int count) {
        this.count = count;
        return this;
    }

    public int getValue() {
        return value;
    }

    public AllDuration setValue(int value) {
        this.value = value;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + value;

        return hash;
    }

    public AllDuration incCount() {
        count++;
        return this;
    }

    public AllDuration incCount(int count) {
        this.count += count;
        return this;
    }

}
