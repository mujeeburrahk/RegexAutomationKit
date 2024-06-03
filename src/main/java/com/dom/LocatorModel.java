package com.dom;

import com.enums.LocatorPlatform;
import com.enums.LocatorType;

public class LocatorModel {
    private final LocatorPlatform locatorPlatform;
    private final String locatorName;
    private final LocatorType locatorType;
    private final String locatorValue;

    public LocatorModel(LocatorPlatform locatorPlatform, String locatorName, LocatorType locatorType, String locatorValue){
        this.locatorPlatform = locatorPlatform;
        this.locatorName = locatorName;
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
    }

    public LocatorPlatform getLocatorPlatform() { return locatorPlatform; }

    public String getLocatorName(){
        return locatorName;
    }

    public LocatorType getLocatorType(){
        return locatorType;
    }

    public String getLocatorValue(){
        return locatorValue;
    }
}