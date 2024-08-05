package com.thiha.sneakershop.apigateway.utils;

import java.time.Instant;
import java.util.Date;

public class DateTimeHelper {
    public static Date getCurrentUTC(){
        Instant instant = Instant.now();
        return Date.from(instant);
    }
}
