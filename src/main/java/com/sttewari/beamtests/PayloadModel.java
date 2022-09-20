package com.sttewari.beamtests;

import lombok.Value;

import java.io.Serializable;

@Value
public class PayloadModel implements Serializable {
    private String key;

    public PayloadModel() {
        key = null;
    }
}
