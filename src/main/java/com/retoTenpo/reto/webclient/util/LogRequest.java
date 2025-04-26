package com.retoTenpo.reto.webclient.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LogRequest {

  public static void printObject(String msg, Object obj) {

    String str = obj instanceof String ? (String) obj : JsonConvert.serializeObject(obj);

    if (Constants.ERROR_SERIALIZE_OBJECT.equals(str)) {
      log.info(String.format("%s : %s", msg, "No json"));
      return;
    }
    log.info(String.format("%s : %s", msg, str));
  }

}
