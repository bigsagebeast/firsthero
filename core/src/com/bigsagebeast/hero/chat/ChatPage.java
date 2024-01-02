package com.bigsagebeast.hero.chat;

import java.util.List;

public class ChatPage {
    public String key;
    public String text;
    public List<ChatLink> links;
    public boolean auto;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + key + ": \"" + text + "\"");
        if (auto) {
            sb.append(" AUTO");
        }
        if (links != null) {
            for (ChatLink link : links) {
                sb.append(" ");
                sb.append(link.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
