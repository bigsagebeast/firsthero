package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.ChatBox;

import java.util.ArrayList;

public class LoadingTips {
    public static int nextIndex = 0;
    public static ArrayList<Tip> tips = new ArrayList<>();
    public static boolean noMoreTips = false;

    public static void showNextTip() {
        if (noMoreTips) {
            return;
        }

        Tip tip = tips.get(nextIndex);
        nextIndex = (nextIndex + 1) % tips.size();

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("Tips - " + tip.title, null)
                .withText(tip.message);

        ArrayList<ChatLink> links = new ArrayList<>();

        ChatLink linkClose = new ChatLink();
        linkClose.text = "Close";
        linkClose.terminal = true;
        links.add(linkClose);

        ChatLink linkNext = new ChatLink();
        linkNext.text = "Next tip";
        linkNext.runnable = LoadingTips::showNextTip;
        linkNext.terminal = true;
        links.add(linkNext);

        ChatLink linkNoMore = new ChatLink();
        linkNoMore.text = "Don't show any more tips";
        linkNoMore.runnable = LoadingTips::noMore;
        linkNoMore.terminal = true;
        links.add(linkNoMore);

        GameLoop.chatModule.openArbitrary(chatBox, links);
    }

    public static void noMore() {
        noMoreTips = true;
    }

    public static class Tip {
        public String title;
        public String message;
    }
}
