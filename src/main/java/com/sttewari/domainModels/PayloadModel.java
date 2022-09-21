package com.sttewari.domainModels;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.beam.sdk.schemas.JavaBeanSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;

@Value
@DefaultSchema(JavaBeanSchema.class)
@AllArgsConstructor(onConstructor = @__({@SchemaCreate}))
public class PayloadModel {
    private String key;

    public PayloadModel() {
        key = null;
    }
}
