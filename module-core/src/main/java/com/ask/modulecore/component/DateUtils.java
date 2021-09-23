package com.ask.modulecore.component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class DateUtils {

  public String getNow() {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
  }
}
