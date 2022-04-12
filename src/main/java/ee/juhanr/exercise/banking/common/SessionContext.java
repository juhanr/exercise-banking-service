package ee.juhanr.exercise.banking.common;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();
    private static final String SESSION_ID = "sessionId";

    public void setSessionId() {
        String sessionId = generateSessionId();
        CONTEXT.set(sessionId);
        MDC.put(SESSION_ID, sessionId);
    }

    public String getSessionId() {
        return CONTEXT.get();
    }

    public void clear() {
        CONTEXT.remove();
        MDC.remove(SESSION_ID);
    }

    private String generateSessionId() {
        String sessionId = String.valueOf(System.currentTimeMillis());
        sessionId = sessionId.substring(sessionId.length() / 2);
        return sessionId + RandomStringUtils.randomNumeric(3);
    }

}
