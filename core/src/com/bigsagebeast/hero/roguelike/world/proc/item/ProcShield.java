package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.glyphtile.IconGlyph;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.game.SwingResult;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.EntityTracker;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Util;

public class ProcShield extends Proc {
    int deflectionMelee = 20;
    int deflectionRanged = 40;
    int armorClass = 0;
    int armorThickness = 0;
    public ProcShield() { super(); }
    public ProcShield(int armorClass, int armorThickness, int deflectionMelee, int deflectionRanged) {
        this();
        this.armorClass = armorClass;
        this.armorThickness = armorThickness;
        this.deflectionMelee = deflectionMelee;
        this.deflectionRanged = deflectionRanged;
    }

    @Override
    public int provideDefense(Entity entity) {
        return armorClass;
    }

    @Override
    public int provideArmorThickness(Entity entity) {
        return armorThickness;
    }

    public int getDeflectionMelee(Entity entity) {
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            return this.deflectionMelee + 5;
        } else if (entity.getBeatitude() == Beatitude.CURSED) {
            return this.deflectionMelee - 5;
        }
        return this.deflectionMelee;
    }

    public int getDeflectionRanged(Entity entity) {
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            return this.deflectionRanged + 5;
        } else if (entity.getBeatitude() == Beatitude.CURSED) {
            return this.deflectionRanged - 5;
        }
        return this.deflectionRanged;
    }

    @Override
    public String getUnidDescription(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Base stats: Armor class ").append(Util.formatFloat(armorClass));
        sb.append(" Armor thickness ").append(Util.formatFloat(armorThickness));
        sb.append(" Melee deflection ").append(Util.formatFloat(deflectionMelee));
        sb.append(" Ranged deflection ").append(Util.formatFloat(deflectionRanged));
        sb.append(". ");
        if (entity.getItem().identifiedBeatitude && entity.getBeatitude() == Beatitude.BLESSED) {
            sb.append("\nIt is receiving a deflection bonus due to its blessing.");
        } else if (entity.getItem().identifiedBeatitude && entity.getBeatitude() == Beatitude.CURSED) {
            sb.append("\nIt is receiving a deflection penalty due to its curse.");
        }
        return sb.toString();
    }

    @Override
    public TextBlock getNameBlock(Entity entity, int width) {
        int nameWidth = width - 6;
        int de = (int) provideDefense(entity);
        int at = (int) provideArmorThickness(entity);
        int deComparator = 0;
        int atComparator = 0;
        BodyPart bp = entity.getEquippable().equipmentFor;
        Integer equippedEntityId = Game.getPlayerEntity().body.equipment.get(bp.getName());
        if (equippedEntityId != null) {
            Entity equippedEntity = EntityTracker.get(equippedEntityId);
            ProcShield ps = (ProcShield)equippedEntity.getProcByType(ProcShield.class);
            deComparator = de - ps.provideDefense(equippedEntity);
            atComparator = at - ps.provideArmorThickness(equippedEntity);
        }
        Color deColor = (deComparator < 0) ? Color.RED : (deComparator == 0) ? Color.WHITE : Color.GREEN;
        Color atColor = (atComparator < 0) ? Color.RED : (atComparator == 0) ? Color.WHITE : Color.GREEN;

        Color beatitudeColor = entity.getItem().identifiedBeatitude ? entity.getBeatitude().color : Color.WHITE;
        TextBlock tbMain = new TextBlock(entity.getVisibleNameWithQuantity(), beatitudeColor);
        tbMain.text = tbMain.text.substring(0, Math.min(nameWidth, tbMain.text.length()));
        tbMain.append(new TextBlock(" (", Color.WHITE))
                .append(new TextBlock("`" + de, deColor, IconGlyph.DEFENSE.icon()))
                .append(new TextBlock("`" + at, atColor, IconGlyph.THICKNESS.icon()))
                .append(new TextBlock(")", Color.WHITE));
        tbMain.children.stream().findFirst().get().x = nameWidth;

        return tbMain;
    }


    @Override
    public Boolean preBeHit(Entity entity, Entity actor, Entity tool, SwingResult result) {
        int deflectionRoll = Game.random.nextInt(100);
        Entity target = entity.getTopLevelContainer();
        if (tool != null && tool.containsProc(ProcWeaponAmmo.class) && deflectionRoll < getDeflectionRanged(entity)) {
            Game.announceVis(actor, target,
                    "Your shot is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s shot is deflected by your " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s shot is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    null);
            return Boolean.FALSE;
        } else if (deflectionRoll < getDeflectionMelee(entity)) {
            Game.announceVis(actor, target,
                    "Your blow is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s blow is deflected by your " + entity.getVisibleName() + ".",
                    actor.getVisibleNameDefinite() + "'s blow is deflected by " + target.getVisibleNameDefinite() + "'s " + entity.getVisibleName() + ".",
                    null);
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public Proc clone(Entity entity) {
        return new ProcShield(armorClass, armorThickness, deflectionMelee, deflectionRanged);
    }
}
