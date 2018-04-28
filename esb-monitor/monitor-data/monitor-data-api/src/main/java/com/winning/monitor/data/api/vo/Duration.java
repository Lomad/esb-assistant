package com.winning.monitor.data.api.vo;

public class Duration {
    private int value;

    private int count;

    public Duration() {
    }

    public Duration(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Duration) {
            Duration _o = (Duration) obj;

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

    public Duration setCount(int count) {
        this.count = count;
        return this;
    }

    public int getValue() {
        return value;
    }

    public Duration setValue(int value) {
        this.value = value;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash = hash * 31 + value;

        return hash;
    }

    public Duration incCount() {
        count++;
        return this;
    }

    public Duration incCount(int count) {
        this.count += count;
        return this;
    }

}
