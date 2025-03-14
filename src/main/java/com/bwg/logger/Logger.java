package com.bwg.logger;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {

    private static final Map<Class<?>, org.slf4j.Logger> LOGGERS = new ConcurrentHashMap<>();

    private static final String CODE_FIELD = "eventType";
    private static final String FLOW_ID = "flowID";

    private Logger() {
        super();
    }

    public static void error(Enum<?> code, String message, Object source) {
        error(code, message, source, null);
    }

    public static void error(Enum<?> code, String message, Object source, Throwable throwable) {
        error(code, toMap("message", message), source.getClass());
    }

    public static void error(Enum<?> code, String message, Class<?> source, Throwable throwable) {
        error(code, toMap("message", message), throwable, source);
    }

    public static void error(Enum<?> code, Object source) {
        error(code, source.getClass());
    }

    public static void error(Enum<?> code, Class<?> source) {
        error(code, (Map<String, ?>) null, null, source);
    }

    private static void error(Enum<?> code, Map<String, ?> details, Object source) {
        error(code, details, source.getClass());
    }

    private static void error(Enum<?> code, Object source, Object... objects) {
        error(code, toMap(objects), source.getClass());
    }

    public static void error(Enum<?> code, Map<String, ?> details, Class<?> source) {
        error(code, details, null, source);
    }

    public static void error(Enum<?> code, Throwable cause, Object source) {
        error(code, cause, source.getClass());
    }

    public static void error(Enum<?> code, Throwable cause, Class<?> source) {
        error(code, (Map<String, ?>) null, cause, source);
    }

    public static void error(Enum<?> code, Map<String, ?> details, Throwable cause, Object source) {
        error(code, details, cause, source.getClass());
    }

    public static void error(Enum<?> code, Object source, Throwable cause, Object... objects) {
        error(code, toMap(objects), cause, source.getClass());
    }

    public static void error(Enum<?> code, Map<String, ?> details, Throwable cause, Class<?> source) {
        org.slf4j.Logger logger = getLogger(source);
        if (logger.isErrorEnabled()) {
            logger.error(toMessage(code, details), cause);
            Logger.removeIndexedFieldFromMdc();
        }
    }

    public static void error(Enum<?> code, String detailKey, Object detailValue, Object source) {
        if (isErrorEnable(source)) {
            error(code, Collections.singletonMap(detailKey, detailValue), source);
        }
    }

//    public static void error (Enum<?> code, String detailKey, Object detailValue, Class<?> source){
//        if(isErrorEnable(source))
//        {
//            error(code, Collections.singletonMap(detailKey, detailValue), source);
//        }
//    }

    public static void error(Enum<?> code, String detailKey, Object detailValue, Throwable cause, Object source) {

        if (isErrorEnable(source)) {
            error(code, Collections.singletonMap(detailKey, detailValue), cause, source);
        }
    }

    public static void error(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, Object source) {

        if (isErrorEnable(source)) {
            error(code, toMap(detailKey, detailValue, detail2Key, detail2Value), source);
        }
    }

    public static void error(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, Throwable cause, Object source) {
        if (isErrorEnable(source)) {
            error(code, toMap(detailKey, detailValue, detail2Key, detail2Value), cause, source);
        }
    }

    public static void error(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, String detail3Key, Object detail3Value, Object source) {

        if (isErrorEnable(source)) {
            error(code, toMap(detailKey, detailValue, detail2Key, detail2Value, detail3Key, detail3Value), source);
        }
    }

    public static void error(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, String detail3Key, Object detail3Value, Throwable cause, Object source) {

        if (isErrorEnable(source)) {
            error(code, toMap(detailKey, detailValue, detail2Key, detail2Value, detail3Key, detail3Value), cause, source);
        }
    }

    public static boolean isErrorEnable(Object source) {
        return isErrorEnable(source.getClass());
    }

    public static boolean isErrorEnable(Class<?> source) {
        return getLogger(source).isErrorEnabled();
    }

    private static org.slf4j.Logger getLogger(Class<?> source) {
        return LOGGERS.computeIfAbsent(source, LoggerFactory::getLogger);
    }

    public static void info(Enum<?> code, String message, Object source) {
        info(code, message, source.getClass(), null);
    }

    public static void info(Enum<?> code, String message, Object source, Throwable throwable) {
        info(code, toMap("message", message), source.getClass(), throwable);
    }

    public static void info(Enum<?> code, String message, Class<?> source, Throwable throwable) {
        info(code, toMap("message", message), source);
    }

    public static void info(Enum<?> code, Map<String, ?> details, Object source) {
        info(code, details, source.getClass());
    }

    public static void info(Enum<?> code, Map<String, ?> details, Class<?> source) {
        org.slf4j.Logger logger = getLogger(source);
        if (logger.isInfoEnabled()) {
            logger.info(toMessage(code, details));
            Logger.removeIndexedFieldFromMdc();
        }
    }

    public static void info(Enum<?> code, Object source, Object... objects) {
        if (isInfoEnable(source))
            info(code, toMap(objects), source.getClass());
    }

    public static void info(Enum<?> code, String detailKey, Object detailValue, Object source) {
        if (isInfoEnable(source))
            info(code, Collections.singletonMap(detailKey, detailValue), source.getClass());
    }

    public static void info(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, Object source) {
        if (isInfoEnable(source)) {
            info(code, toMap(detailKey, detailValue, detail2Key, detail2Value), source.getClass());
        }
    }

    public static void info(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, String detail3Key, Object detail3Value, Object source) {
        if (isInfoEnable(source))
            info(code, toMap(detailKey, detailValue, detail2Key, detail2Value, detail3Key, detail3Value), source.getClass());
    }

    public static boolean isInfoEnable(Object source) {
        return isInfoEnable(source.getClass());
    }

    public static boolean isInfoEnable(Class<?> source) {
        return getLogger(source).isInfoEnabled();
    }

    public static void debug(Enum<?> code, String message, Object source) {
        debug(code, message, source.getClass(), null);
    }

    public static void debug(Enum<?> code, String message, Object source, Throwable throwable) {
        debug(code, toMap("message", message), source.getClass());
    }

    public static void debug(Enum<?> code, String message, Class<?> source, Throwable throwable)
    {
        debug(code, toMap("message", message), throwable, source);
    }

    public static void debug(Enum<?> code, Map<String, ?> details, Object source) {
        debug(code, details, source.getClass());
    }

    public static void debug(Enum<?> code, Object source, Object... objects) {
        debug(code, toMap(objects), source.getClass());
    }

    public static void debug(Enum<?> code, Map<String, ?> details, Class<?> source) {
        debug(code, details, null, source);
    }


    public static void debug(Enum<?> code, String detailKey, Object detailValue, Object source) {
        debug(code, Collections.singletonMap(detailKey, detailValue), source.getClass());
    }

    public static void debug(Enum<?> code, Map<String, ?> details, Throwable cause, Object source) {
        debug(code, details, cause, source.getClass());
    }

    public static void debug(Enum<?> code, Object source, Throwable cause, Object... objects) {
        debug(code, toMap(objects), cause, source.getClass());
    }

    public static void debug(Enum<?> code, Map<String, ?> details, Throwable cause, Class<?> source) {
        org.slf4j.Logger logger = getLogger(source);
        if (logger.isDebugEnabled()) {
            logger.debug(toMessage(code, details), cause);
            Logger.removeIndexedFieldFromMdc();
        }
    }


    public static void debug(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, Object source) {
        if (isDebugEnable(source))
            debug(code, toMap(detailKey, detailValue, detail2Key, detail2Value), source.getClass());
    }

    public static void debug(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, String detail3Key, Object detail3Value, Object source) {
        if (isDebugEnable(source))
            debug(code, toMap(detailKey, detailValue, detail2Key, detail2Value, detail3Key, detail3Value), source.getClass());
    }

    public static boolean isDebugEnable(Object source) {
        return isDebugEnable(source.getClass());
    }

    public static boolean isDebugEnable(Class<?> source) {
        return getLogger(source).isDebugEnabled();
    }

    public static void trace(Enum<?> code, String message, Object source) {
        trace(code, message, source.getClass(), null);
    }

    public static void trace(Enum<?> code, String message, Object source, Throwable throwable) {
        trace(code, toMap("message", message), source.getClass());
    }

    public static void trace(Enum<?> code, String message, Class<?> source, Throwable throwable) {
        trace(code, toMap("message", message), source, throwable);
    }

    public static void trace(Enum<?> code, Object source, Object... objects) {
        trace(code, toMap(objects), source.getClass());
    }

    public static void trace(Enum<?> code, Map<String, ?> details, Class<?> source) {
        org.slf4j.Logger logger = getLogger(source);
        if (logger.isTraceEnabled()) {
            logger.trace(toMessage(code, details));
            Logger.removeIndexedFieldFromMdc();
        }
    }

    public static void trace(Enum<?> code, String detailKey, Object detailValue, Object source) {
        trace(code, Collections.singletonMap(detailKey, detailValue), source);
    }

    public static void trace(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, Object source) {
        if (isTraceEnable(source)) {
            trace(code, toMap(detailKey, detailValue, detail2Key, detail2Value), source);
        }
    }

    public static void trace(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, String detail3Key, Object detail3Value, Object source) {
        if (isTraceEnable(source)) {
            trace(code, toMap(detailKey, detailValue, detail2Key, detail2Value, detail3Key, detail3Value), source);
        }
    }

    public static boolean isTraceEnable(Object source) {
        return isTraceEnable(source.getClass());
    }

    public static boolean isTraceEnable(Class<?> source) {
        return getLogger(source).isTraceEnabled();
    }

    public static void warn(Enum<?> code, String message, Object source) {
        warn(code, message, source.getClass(), null);
    }

    public static void warn(Enum<?> code, String message, Object source, Throwable throwable) {
        warn(code, toMap("message", message), source.getClass());
    }

    public static void warn(Enum<?> code, String message, Class<?> source, Throwable throwable) {
        warn(code, toMap("message", message), source);
    }

    public static void warn(Enum<?> code, Object source, Object... objects) {
        warn(code, toMap(objects), source.getClass());
    }

    public static void warn(Enum<?> code, Map<String, ?> details, Class<?> source) {
        warn(code, details, null, source);
    }

    public static void warn(Enum<?> code, Map<String, ?> details, Throwable cause, Object source) {
        warn(code, details, cause, source.getClass());
    }

    public static void warn(Enum<?> code, Object source, Throwable cause, Object... objects) {
        warn(code, toMap(objects), cause, source.getClass());
    }

    public static void warn(Enum<?> code, Map<String, ?> details, Throwable cause, Class<?> source) {
        org.slf4j.Logger logger = getLogger(source);

        if (logger.isWarnEnabled()) {
            logger.warn(toMessage(code, details), cause);
            Logger.removeIndexedFieldFromMdc();
        }
    }

    public static void warn(Enum<?> code, String detailKey, Object detailValue, Object source) {
        if (isWarnEnable(source))
            warn(code, Collections.singletonMap(detailKey, detailValue), source.getClass());
    }

    public static void warn(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, Object source) {
        if (isWarnEnable(source))
            warn(code, toMap(detailKey, detailValue, detail2Key, detail2Value), source.getClass());
    }

    public static void warn(Enum<?> code, String detailKey, Object detailValue, String detail2Key, Object detail2Value, String detail3Key, Object detail3Value, Object source) {
        if (isWarnEnable(source))
            warn(code, toMap(detailKey, detailValue, detail2Key, detail2Value, detail3Key, detail3Value), source.getClass());
    }

    public static boolean isWarnEnable(Object source) {
        return isWarnEnable(source.getClass());
    }

    public static boolean isWarnEnable(Class<?> source) {
        return getLogger(source).isWarnEnabled();
    }

//    private static String toMessage(Enum<?> code, Map<String, ?> details) {
//        AuthModel authModel = RequestHeadersContextHolder.getContext().getAuthModel();
//
//        StringBuffer message = new StringBuffer(ApplicationContextRefresh.getApplicationName());
//        String flowId = authModel != null ? authModel.getFlowId() : "UNKNOWN";
//        Long userId = authModel != null ? authModel.getUserId() : -1;
//        MDC.put(CODE_FIELD, code.name());
//        MDC.put(FLOW_ID, flowId);
//
//        message.append(" | ").append(flowId).append(" | ").append(userId).append(" | ").append(code.name()).append(" | ");
//        if (!ObjectUtils.isEmpty(details)) {
//            details.forEach((key, value) -> {
//                String sanitiseKey = sanitise(key);
//                Object sanitiseValue = sanitise(value);
//                if (IndexedField.PATTERN.matcher(sanitiseKey).matches()) {
//                    MDC.put(sanitiseKey, sanitiseValue.toString());
//                } else if (message.length() == code.name().length()) {
//                    message.append(":");
//                }
//                message.append(' ').append(sanitiseKey).append('=').append(escape(sanitiseValue));
//            });
//        }
//        return message.toString();
//    }

    private static String toMessage(Enum<?> code, Map<String, ?> details) {
        StringBuilder message = new StringBuilder("ApplicationName"); // Simplified application name
        String flowId = "UNKNOWN"; // Simplified flow ID
        Long userId = -1L; // Simplified user ID

        MDC.put(CODE_FIELD, code.name());
        MDC.put(FLOW_ID, flowId);

        message.append(" | ").append(flowId).append(" | ").append(userId).append(" | ").append(code.name()).append(" | ");
        if (!ObjectUtils.isEmpty(details)) {
            details.forEach((key, value) -> {
                String sanitiseKey = sanitise(key);
                Object sanitiseValue = sanitise(value);
                message.append(' ').append(sanitiseKey).append('=').append(escape(sanitiseValue));
            });
        }
        return message.toString();
    }

    private static <T> T sanitise(T object) {
        if (object instanceof String)
            return (T) ((String) object).replace("\n", "").replace("\r", "");
        return object;
    }

    private static Object escape(Object value) {
        if (value instanceof String)
            return "'" + value + "'";
        return value;
    }

    private static Map<String, Object> toMap(Object... objects) {
        Map<String, Object> map = new HashMap<>();
        for (int index = 0; index < objects.length; index = index + 2) {
            map.put((String) objects[index], objects[index + 1]);
        }
        return map;
    }

//    private static void removeIndexedFieldFromMdc() {
//        MDC.remove(CODE_FIELD);
//        for (IndexedField indexedField : IndexedField.values()) {
//            MDC.remove(indexedField.getName());
//        }
//    }

    private static void removeIndexedFieldFromMdc() {
        MDC.remove(CODE_FIELD);
        MDC.remove(FLOW_ID);
    }

    public static String format(String message,  Object... params)
    {
        return MessageFormat.format(message,params);
    }
}
