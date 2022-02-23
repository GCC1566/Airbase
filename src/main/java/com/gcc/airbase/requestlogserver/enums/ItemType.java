package com.gcc.airbase.requestlogserver.enums;

public enum ItemType {

    DEFAULT_STR("=");

    String value;

    public String getValue(){
        return value;
    }

    ItemType(String s){
        value = s;
    }

}
