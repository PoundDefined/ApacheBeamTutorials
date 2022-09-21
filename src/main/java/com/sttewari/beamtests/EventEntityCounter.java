package com.sttewari.beamtests;

import com.sttewari.domainModels.EventModel;
import com.sttewari.domainModels.ParsedEventModel;
import com.sttewari.domainModels.PayloadModel;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.MapElements;
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

        // get parsed collection
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

        tracePCollection
                .apply("Service1", Filter
                        .by((ParsedEventModel eventModel) ->
                                "val".equalsIgnoreCase(eventModel.getPayloadModel().getKey()))
                )
                .apply("FormatResults", MapElements
                        .into(TypeDescriptors.strings())
                        .via((ParsedEventModel payload) ->
                                payload.getEventModel().getEventType() + " " + payload.getPayloadModel().getKey())
                )
                .apply(TextIO.write()
                        .withNoSpilling()
                        .to("./target/out/filteredWithKeyVal.out"));

        // parse to payload
        tracePCollection
                .apply("FormatResults", MapElements
                        .into(TypeDescriptors.strings())
                        .via((ParsedEventModel payload) ->
                                 payload.getEventModel().getEventType() + " " + payload.getPayloadModel().getKey())
                )
                .apply(TextIO.write()
                        .withNoSpilling()
                        .to("./target/out/parsedPayload.out"));

        // group keys in parsed payload
        tracePCollection
                .apply("Count keys by request and payload value type", MapElements
                        .into(TypeDescriptors.strings())
                        .via((ParsedEventModel parsedEvent) ->
                            parsedEvent.getEventModel().getEventType() + "-" + parsedEvent.getPayloadModel().getKey()
                        )
                )
                .apply(Count.perElement())
                .apply("FormatResults", MapElements
                        .into(TypeDescriptors.strings())
                        .via((KV<String, Long> wordCount) -> wordCount.getKey() + ": " + wordCount.getValue()))
                .apply(TextIO.write()
                        .withNoSpilling()
                        .to("./target/out/countPerKey.out"));

        pipeline.run().waitUntilFinish();
    }
}
