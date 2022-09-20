package com.sttewari.beamtests;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class EventEntityCounter {

    private static final ObjectMapper OM = new ObjectMapper();
    public static void main(String[] args) {
        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();


        Pipeline pipeline = Pipeline.create(pipelineOptions);

        PCollection<ParsedEventModel> tracePCollection = pipeline.apply(TextIO.read()
                        .from("./src/main/resources/traceEventLog.txt"))
                .apply("ParseToEntity", MapElements
                        .into(TypeDescriptor.of(EventModel.class))
                        .via((String line) -> {
                            try {
                                return OM.readValue(line, EventModel.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                )
                .apply("ParsedEventModel", MapElements
                        .into(TypeDescriptor.of(ParsedEventModel.class))
                        .via((EventModel model) -> {
                            try {
                                PayloadModel payloadModel = OM.readValue(model.getPayload(), PayloadModel.class);
                                return new ParsedEventModel(model, payloadModel);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                );


        // parse to payload
        tracePCollection
                .apply("FormatResults", MapElements
                        .into(TypeDescriptors.strings())
                        .via((ParsedEventModel payload) ->
                                 payload.getEventModel().getEventType() + " " + payload.getPayloadModel().getKey())
                )
                .apply(TextIO.write().to("./out/parsedPayload"));

        // group keys by request/response payload

        pipeline.run().waitUntilFinish();
    }
}
