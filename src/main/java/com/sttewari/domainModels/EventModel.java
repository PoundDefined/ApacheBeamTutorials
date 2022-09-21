package com.sttewari.domainModels;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.beam.sdk.schemas.JavaBeanSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;

@Value
@DefaultSchema(JavaBeanSchema.class)
@AllArgsConstructor(onConstructor = @__({@SchemaCreate}))
public class EventModel {
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
