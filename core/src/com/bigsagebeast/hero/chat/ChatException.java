package com.bigsagebeast.hero.chat;

public class ChatException extends RuntimeException {
    public ChatException(String string) {
        super(string);
    }
    public ChatException(Exception e) {
        super(e);
    }
}
