package com.jinternals.scheduler.constants;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class SchedulerConstants {
    public static final DateTimeFormatter BUCKET_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddhhmm");
    public static final String SCHEDULED_ITEMS_ID = "scheduled-items";
    public static final String ID = "id";
    public static final String SCHEDULED_ITEMS_STREAM_NAME = "scheduled-items-stream";
    public static final String SCHEDULED_ITEMS_GROUP_NAME = "scheduled-items-group";
    public static final String CONSUMER_NAME = "scheduled-consumer-%s";
}
