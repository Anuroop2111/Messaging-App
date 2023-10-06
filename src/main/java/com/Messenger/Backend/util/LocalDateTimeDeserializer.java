//package com.Messenger.Backend.util;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//
//public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
//
//    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    @Override
//    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        String dateString = p.getValueAsString();
//        if (dateString == null) {
//            return null;  // Handle null values
//        }
//        return LocalDateTime.parse(dateString, formatter);
//    }
//}
//
