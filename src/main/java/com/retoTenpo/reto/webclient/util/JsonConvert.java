package com.retoTenpo.reto.webclient.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonConvert {

  public static String serializeObject(Object obj) {

    if (obj == null) {
      return Constants.ERROR_SERIALIZE_OBJECT;
    }

    try {
      return (new ObjectMapper())
          .registerModule(new JavaTimeModule())
          .writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      return Constants.ERROR_SERIALIZE_OBJECT;
    }

  }
}
