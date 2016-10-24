package com.aca.travelsafe.PojoModel;

/**
 * Created by Marsel on 10/5/2016.
 */
public class SpinnerItem {
    String key;
    String value;

    public SpinnerItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return value;
    }


}
