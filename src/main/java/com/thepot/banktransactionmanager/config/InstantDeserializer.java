package com.thepot.banktransactionmanager.config;

import java.time.Instant;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class InstantDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer<Instant> {
    protected InstantDeserializer() {
        super(INSTANT, ISO_OFFSET_DATE_TIME);
    }
}
