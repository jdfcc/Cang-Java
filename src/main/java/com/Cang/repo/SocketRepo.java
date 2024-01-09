package com.Cang.repo;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 储存用户的webSocket
 * @DateTime 2024/1/8 11:20
 */
public class SocketRepo {
    private static final Map<String, List<Session>> HOME_CHAT_CLIENTS = new ConcurrentHashMap<>();

    public static void addHomeChatClient(String userId, Session session) throws IOException {
        if (HOME_CHAT_CLIENTS.containsKey(userId)) {
            List<Session> sessions = HOME_CHAT_CLIENTS.get(userId);
            sessions.add(session);
        } else {
            HOME_CHAT_CLIENTS.put(userId, new ArrayList<Session>() {{
                add(session);
            }});
        }

    }

    public static void removeHomeChatClient(String userId, Session session) throws IOException {
        List<Session> sessions = HOME_CHAT_CLIENTS.get(userId);
        assert sessions != null;
        boolean remove = sessions.remove(session);
        System.out.println(remove);
        if (sessions.size() == 0) {
            HOME_CHAT_CLIENTS.remove(userId);
        } else {
            HOME_CHAT_CLIENTS.put(userId, sessions);
        }
        session.close();
    }

    public static int getHomeChatSize() {
        int totalSize = 0;
        // 遍历 HOME_CHAT_CLIENTS 中的所有 List<Session>，累加其大小
        for (List<Session> sessionList : HOME_CHAT_CLIENTS.values()) {
            totalSize += sessionList.size();
        }
        return totalSize;
    }

    public static List<Session> getHomeChatSession(Long userid) {
        return HOME_CHAT_CLIENTS.get(String.valueOf(userid));
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
