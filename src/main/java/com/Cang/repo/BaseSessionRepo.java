package com.Cang.repo;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 储存用户的session
 * @DateTime 2024/1/10 16:36
 */
public abstract class BaseSessionRepo {

    private static final Map<String, List<Session>> CLIENTS = new ConcurrentHashMap<>();

    public static void addClient(String userId, Session session) {
        if (CLIENTS.containsKey(userId)) {
            List<Session> sessions = CLIENTS.get(userId);
            sessions.add(session);
        } else {
            CLIENTS.put(userId, new ArrayList<Session>() {{
                add(session);
            }});
        }

    }

    public static void removeClient(String userId, Session session) throws IOException {
        List<Session> sessions = CLIENTS.get(userId);
        assert sessions != null;
        sessions.remove(session);
        if (sessions.size() == 0) {
            CLIENTS.remove(userId);
        } else {
            CLIENTS.put(userId, sessions);
        }
        session.close();
    }

    public static int getSize() {
        int totalSize = 0;
        // 遍历 HOME_CHAT_CLIENTS 中的所有 List<Session>，累加其大小
        for (List<Session> sessionList : CLIENTS.values()) {
            totalSize += sessionList.size();
        }
        return totalSize;
    }

    public static List<Session> getSession(Long userid) {
        return CLIENTS.get(String.valueOf(userid));
    }

    public static class Session2IdMapping {
        private static final Map<String, String> SESSION_2_ID = new ConcurrentHashMap<>();

        public static void addMapping(String sessionId, String userid) {
            SESSION_2_ID.put(sessionId, userid);
        }

        public static String getUserId(String sessionId) {
            return SESSION_2_ID.get(sessionId);
        }

        public static void removeMapping(String sessionId, String userid) {
            SESSION_2_ID.remove(sessionId, userid);
        }

    }
}
