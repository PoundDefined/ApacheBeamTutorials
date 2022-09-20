package com.sttewari.beamtests;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@AllArgsConstructor
public class ParsedEventModel implements Serializable {
    private EventModel eventModel;
    private PayloadModel payloadModel;

    public ParsedEventModel() {
        this.eventModel = null;
        this.payloadModel = null;
    }
}
