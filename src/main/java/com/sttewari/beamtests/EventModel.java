package com.sttewari.beamtests;

import lombok.Value;

import java.io.Serializable;

@Value
public class EventModel implements Serializable {
    private String segmentName;
    private String requestId;
    private String eventType;
    private String payload;

    public EventModel() {
        segmentName = null;
        requestId = null;
        eventType = null;
        payload = null;
    }
}
