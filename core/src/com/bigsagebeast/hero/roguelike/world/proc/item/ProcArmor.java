package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.glyphtile.IconGlyph;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Util;

public class ProcArmor extends Proc {
    int armorClass = 0;
    int armorThickness = 0;
    public ProcArmor() { super(); }
    public ProcArmor(int armorClass, int armorThickness) {
        this();
        this.armorClass = armorClass;
        this.armorThickness = armorThickness;
    }

    @Override
    public String getUnidDescription(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Base stats: Armor class ").append(Util.formatFloat(armorClass));
        sb.append(" Armor thickness ").append(Util.formatFloat(armorThickness));
        sb.append(". ");
        if (entity.getItem().identifiedBeatitude && entity.getBeatitude() == Beatitude.BLESSED) {
            sb.append("\nIt reduces damage taken by 5% due to its blessing.");
        } else if (entity.getItem().identifiedBeatitude && entity.getBeatitude() == Beatitude.CURSED) {
            sb.append("\nIt increases damage taken by 10% due to its curse.");
        }
        return sb.toString();
    }

    @Override
    public TextBlock getNameBlock(Entity entity, int width) {
        int de = (int) provideDefense(entity);
        int at = (int) provideArmorThickness(entity);
        int deComparator = 0;
        int atComparator = 0;
        BodyPart bp = entity.getEquippable().equipmentFor;
        Integer equippedEntityId = Game.getPlayerEntity().body.equipment.get(bp.getName());
        if (equippedEntityId != null) {
            Entity equippedEntity = EntityTracker.get(equippedEntityId);
            ProcArmor pa = (ProcArmor)equippedEntity.getProcByType(ProcArmor.class);
            deComparator = de - pa.provideDefense(equippedEntity);
            atComparator = at - pa.provideArmorThickness(equippedEntity);
        }
        Color deColor = (deComparator < 0) ? Color.RED : (deComparator == 0) ? Color.WHITE : Color.GREEN;
        Color atColor = (atComparator < 0) ? Color.RED : (atComparator == 0) ? Color.WHITE : Color.GREEN;
        Color beatitudeColor = entity.getItem().identifiedBeatitude ? entity.getBeatitude().color : Color.WHITE;
        TextBlock tbMain = new TextBlock(entity.getVisibleNameWithQuantity(), beatitudeColor);
        tbMain.text = tbMain.text.substring(0, Math.min(width, tbMain.text.length()));
        tbMain.append(new TextBlock(" (", Color.WHITE))
                .append(new TextBlock("`" + de, deColor, IconGlyph.DEFENSE.icon()))
                .append(new TextBlock("`" + at, atColor, IconGlyph.THICKNESS.icon()))
                .append(new TextBlock(")", Color.WHITE));
        tbMain.children.stream().findFirst().get().x = width;

        return tbMain;
    }

    @Override
    public int provideDefense(Entity entity) {
        return armorClass;
    }

    @Override
    public int provideArmorThickness(Entity entity) {
        return armorThickness;
    }

    @Override
    public float provideDamageReceivedMultiplier(Entity entity) {
        switch (entity.getBeatitude()) {
            case CURSED:
                return 1.1f;
            case UNCURSED:
                return 1.0f;
            default /* BLESSED*/:
                return 0.95f;
        }
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcArmor(armorClass, armorThickness);
    }
}
