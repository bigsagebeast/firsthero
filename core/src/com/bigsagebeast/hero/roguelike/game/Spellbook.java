package com.bigsagebeast.hero.roguelike.game;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.chat.ChatLink;
import com.bigsagebeast.hero.dialogue.ChatBox;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.roguelike.spells.Spell;
import com.bigsagebeast.hero.roguelike.world.*;
import com.bigsagebeast.hero.dialogue.DialogueBox;
import com.bigsagebeast.hero.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Spellbook {
    public List<String> spells = new ArrayList<>();

    public void addSpell(String key) {
        if (spells.contains(key)) {
            return;
        }
        spells.add(key);
        Collections.sort(spells);

        // ensure that the spellbook is identified, even if the player learned the spell some other way
        for (ItemType it : Itempedia.map.values()) {
            LoadProc procLoader = it.procLoaders.stream().filter(pl -> pl.procName.equals("item.book.ProcBookSpell")).findAny().orElse(null);
            if (procLoader != null) {
                String spell = procLoader.fields.get("spell");
                if (spell.equals(key)) {
                    it.identified = true;
                }
            }
        }
    }

    public boolean hasSpell(String key) {
        return spells.contains(key);
    }

    public List<Spell> getSpells() {
        return spells.stream().map(k -> Spellpedia.get(k)).collect(Collectors.toList());
    }

    public void openSpellbookToCast() {
        if (spells.isEmpty()) {
            Game.announce("You don't know any spells.");
            return;
        }
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select spell to cast")
                .withAllowLetters(true)
                .withMargins(60, 60);
        String format = "%-15s %-8s %-5s %-3s %-5s";
        box.addHeader(String.format("  " + format, "Name", "Type", "Range", "Dur", "Cost"));
        for (Spell.SpellType type : Spell.SpellType.values()) {
            List<Spell> spellsOfType = getSpells().stream().filter(s -> s.getSpellType() == type).collect(Collectors.toList());
            if (!spellsOfType.isEmpty()) {
                box.addHeader(type.name());
                for (Spell spell : spellsOfType) {
                    Map<Element, Integer> elementCost = spell.getElementCost(Game.getPlayerEntity());
                    StringBuilder elementString = new StringBuilder();
                    for (Element element : elementCost.keySet()) {
                        for (int i = 0; i < elementCost.get(element); i++) {
                            elementString.append(element.symbol);
                        }
                    }
                    String rangeString;
                    Float actualRange = spell.getRange(Game.getPlayerEntity());
                    if (actualRange == null) {
                        rangeString = "-";
                    } else {
                        if (actualRange == actualRange.intValue()) {
                            rangeString = "" + actualRange.intValue();
                        } else {
                            rangeString = "" + actualRange;
                        }
                    }
                    String durationString;
                    Integer actualDuration = spell.getDuration(Game.getPlayerEntity());
                    if (actualDuration == null) {
                        durationString = "-";
                    } else {
                        durationString = "" + actualDuration;
                    }
                    box.addItem(String.format(format,
                                    spell.getName(), spell.getTypeDescription(),
                                    rangeString, durationString, spell.getCost(Game.getPlayerEntity()) + " " + elementString)
                            , spell);
                }
            }
        }
        box.addHeader("");
        box.addItem("Spell descriptions", "descriptions");
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleSpellbookToCastResponse);
    }

    public void handleSpellbookToCastResponse(Object chosenSpell) {
        if (chosenSpell == null) {
            return;
        }
        if (chosenSpell.getClass().isAssignableFrom(Spell.class)) {
            Spell spell = (Spell) chosenSpell;
            spell.playerStartSpell();
        } else if (chosenSpell.getClass().isAssignableFrom(String.class)) {
            openSpellbookToDescribe();
        }
    }

    public void openSpellbookToDescribe() {
        if (spells.isEmpty()) {
            Game.announce("You don't know any spells.");
            return;
        }
        DialogueBox box = new DialogueBox()
                .withFooterClosable()
                .withTitle("Select spell to describe")
                .withAllowLetters(true)
                .withMargins(60, 60);
        String format = "%-15s %-8s %-5s %-3s %-5s";
        box.addHeader(String.format("  " + format, "Name", "Type", "Range", "Dur", "Cost"));
        for (Spell.SpellType type : Spell.SpellType.values()) {
            List<Spell> spellsOfType = getSpells().stream().filter(s -> s.getSpellType() == type).collect(Collectors.toList());
            if (!spellsOfType.isEmpty()) {
                box.addHeader(type.name());
                for (Spell spell : spellsOfType) {
                    Map<Element, Integer> elementCost = spell.getElementCost(Game.getPlayerEntity());
                    StringBuilder elementString = new StringBuilder();
                    for (Element element : elementCost.keySet()) {
                        for (int i = 0; i < elementCost.get(element); i++) {
                            elementString.append(element.symbol);
                        }
                    }
                    String rangeString;
                    Float actualRange = spell.getRange(Game.getPlayerEntity());
                    if (actualRange == null) {
                        rangeString = "-";
                    } else {
                        if (actualRange == actualRange.intValue()) {
                            rangeString = "" + actualRange.intValue();
                        } else {
                            rangeString = "" + actualRange;
                        }
                    }
                    String durationString;
                    Integer actualDuration = spell.getDuration(Game.getPlayerEntity());
                    if (actualDuration == null) {
                        durationString = "-";
                    } else {
                        durationString = "" + actualDuration;
                    }
                    box.addItem(String.format(format,
                                    spell.getName(), spell.getTypeDescription(),
                                    rangeString, durationString, spell.getCost(Game.getPlayerEntity()) + " " + elementString)
                            , spell);
                }
            }
        }
        GameLoop.dialogueBoxModule.openDialogueBox(box, this::handleSpellbookToDescribeResponse);
    }

    public void handleSpellbookToDescribeResponse(Object chosenObject) {
        if (chosenObject == null) {
            return;
        }
        Spell spell = (Spell)chosenObject;
        Entity caster = Game.getPlayerEntity();

        StringBuilder sb = new StringBuilder();
        sb.append(spell.getName() + " - " + spell.getSpellType().name + "\n");
        sb.append("Target type: " + spell.getTargetType().name + "\n");
        sb.append("Cost: " + spell.getCost(caster) + " " + spell.getSpellType().cost);

        for (Element element : spell.getElementCost(caster).keySet()) {
            int charges = spell.getElementCost(caster).get(element);
            sb.append(", " + charges + " " + element.name);
            sb.append(charges == 1 ? " charge" : " charges");
        }

        sb.append("\n\n");
        sb.append(spell.getDescription());
        sb.append("\n");

        if (spell.getBaseDamage() != null) {
            sb.append("\nBase damage ").append(Util.formatFloat(spell.getBaseDamage()));
            for (Stat stat : spell.getScaling().keySet()) {
                float mod = spell.getScaling().get(stat).damage;
                if (mod != 0) {
                    String sign = mod > 0 ? "+" : "-";
                    sb.append(", " + sign + Util.formatFloat(mod) + "/" + stat.description());
                }
            }
        }

        if (spell.getBaseRange() != null) {
            sb.append("\nBase range ").append(Util.formatFloat(spell.getBaseRange()));
            for (Stat stat : spell.getScaling().keySet()) {
                float mod = spell.getScaling().get(stat).range;
                if (mod != 0) {
                    String sign = mod > 0 ? "+" : "-";
                    sb.append(", " + sign + Util.formatFloat(mod) + "/" + stat.description());
                }
            }
        }

        if (spell.getBaseDuration() != null) {
            sb.append("\nBase duration ").append(Util.formatFloat(spell.getBaseDuration()));
            for (Stat stat : spell.getScaling().keySet()) {
                float mod = spell.getScaling().get(stat).duration;
                if (mod != 0) {
                    String sign = mod > 0 ? "+" : "-";
                    sb.append(", " + sign + Util.formatFloat(mod) + "/" + stat.description());
                }
            }
        }
        if (spell.getBaseDamage() != null || spell.getBaseRange() != null || spell.getBaseDuration() != null) {
            sb.append("\n");
        }

        if (spell.isResistable() && !spell.isDodgeable()) {
            sb.append("\nIt can be resisted.\n");
        } else if (spell.isResistable() && spell.isDodgeable()) {
            sb.append("\nIt can be dodged and resisted.\n");
        } else if (!spell.isResistable() && spell.isDodgeable()) {
            sb.append("\nIt can be dodged.\n");
        }
        sb.append("\n");

        if (spell.getBaseDamage() != null || spell.getBaseRange() != null || spell.getBaseDuration() != null) {
            if (spell.getBaseDamage() != null) {
                sb.append("Your damage: ").append(Util.formatFloat(spell.getDamage(caster))).append(". ");
            }

            if (spell.getBaseRange() != null) {
                sb.append("Your range: ").append(Util.formatFloat(spell.getRange(caster))).append(". ");
            }

            if (spell.getBaseDuration() != null) {
                sb.append("Your duration: ").append(Util.formatFloat(spell.getRange(caster))).append(". ");
            }
        }

        ChatBox chatBox = new ChatBox()
                .withMargins(60, 60)
                .withTitle("Spell description", null)
                .withText(sb.toString());

        ArrayList<ChatLink> links = new ArrayList<>();
        ChatLink linkOk = new ChatLink();
        linkOk.text = "OK";
        links.add(linkOk);

        GameLoop.chatModule.openArbitrary(chatBox, links);
    }
}
