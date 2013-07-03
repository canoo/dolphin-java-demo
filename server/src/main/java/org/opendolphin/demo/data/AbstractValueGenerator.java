package org.opendolphin.demo.data;

public abstract class AbstractValueGenerator<T> {

    public abstract T randomValue();

    protected boolean getRandomBoolean(double probability) {
        return Math.random() < probability;
    }

    protected String getRandomString(String[] values) {
        int index = new Double(Math.random() * 100000).intValue();
        return values[index % values.length];
    }

}
