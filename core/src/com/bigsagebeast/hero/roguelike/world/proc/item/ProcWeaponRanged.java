package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.glyphtile.IconGlyph;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.AmmoType;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Util;

import java.util.HashMap;
import java.util.Map;

public class ProcWeaponRanged extends Proc {

    public ProcWeaponRanged() { super(); }
    public ProcWeaponRanged(int damage, int toHit, int penetration, int range, AmmoType ammoType) {
        this();
        this.damage = damage;
        this.toHit = toHit;
        this.penetration = penetration;
        this.range = range;
        this.ammoType = ammoType;
    }

    public int damage;
    public int toHit;
    public int penetration;
    public int range;
    public Map<Stat, EquipmentScaling> scaling = new HashMap<>();

    public AmmoType ammoType;


    @Override
    public String getUnidDescription(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Fires " + ammoType.name() + "s");
        sb.append(". Base stats: Damage ").append(Util.formatFloat(damage));
        sb.append(" To-Hit ").append(Util.formatFloat(toHit));
        sb.append(" Penetration ").append(Util.formatFloat(penetration));
        sb.append("\nScaling: ");
        for (Stat stat : scaling.keySet()) {
            sb.append(Util.capitalize(stat.description())).append(":");
            EquipmentScaling es = scaling.get(stat);
            if (es.damage != 0) {
                sb.append(" Damage ").append(Util.formatFloat(es.damage));
            }
            if (es.toHit != 0) {
                sb.append(" To-Hit ").append(Util.formatFloat(es.toHit));
            }
            if (es.penetration != 0) {
                sb.append(" Penetration ").append(Util.formatFloat(es.penetration));
            }
            sb.append(". ");
        }

        sb.append(String.format("\n\nFor you: Damage %s To-Hit %s Penetration %s Defense %s",
                Util.formatFloat(getVisibleDamage(entity, Game.getPlayerEntity())),
                Util.formatFloat(getVisibleToHit(entity, Game.getPlayerEntity())),
                Util.formatFloat(getVisiblePenetration(entity, Game.getPlayerEntity()))));
        if (entity.getItem().identifiedBeatitude && entity.getBeatitude() == Beatitude.BLESSED) {
            sb.append("\n\nIt is receiving a damage and to-hit bonus due to its blessing.");
        } else if (entity.getItem().identifiedBeatitude && entity.getBeatitude() == Beatitude.CURSED) {
            sb.append("\n\nIt is receiving a damage and to-hit penalty due to its curse.");
        }
        sb.append("\n\nRanged weapon stats are combined with ammo stats. See the ammo for more details.");

        return sb.toString();
    }

    public float getDamage(Entity entity, Entity wielder) {
        float accum = damage;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).damage);
        }
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            accum += 1;
        } else if (entity.getBeatitude() == Beatitude.CURSED) {
            accum -= 1;
        }
        return accum;
    }

    public float getToHit(Entity entity, Entity wielder) {
        float accum = toHit;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).toHit);
        }
        if (entity.getBeatitude() == Beatitude.BLESSED) {
            accum += 1;
        } else if (entity.getBeatitude() == Beatitude.CURSED) {
            accum -= 1;
        }
        return accum;
    }

    public float getPenetration(Entity entity, Entity wielder) {
        float accum = penetration;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).penetration);
        }
        return accum;
    }

    public float getVisibleDamage(Entity entity, Entity wielder) {
        float accum = damage;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).damage);
        }
        return accum;
    }

    public float getVisibleToHit(Entity entity, Entity wielder) {
        float accum = toHit;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).toHit);
        }
        return accum;
    }

    public float getVisiblePenetration(Entity entity, Entity wielder) {
        float accum = penetration;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).penetration);
        }
        return accum;
    }
    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {}

    @Override
    public TextBlock getNameBlock(Entity entity, int width) {
        Entity pcPrimaryWeapon = Game.getPlayerEntity().body.getEquipment(BodyPart.PRIMARY_HAND);
        ProcWeaponRanged p = null;
        if (pcPrimaryWeapon != null) {
            p = (ProcWeaponRanged)pcPrimaryWeapon.getProcByType(ProcWeaponRanged.class);
        }

        int ad = (int) getVisibleDamage(entity, Game.getPlayerEntity());
        int th = (int) getVisibleToHit(entity, Game.getPlayerEntity());
        int pn = (int) getVisiblePenetration(entity, Game.getPlayerEntity());

        int damageComparator = 0;
        int toHitComparator = 0;
        int pnComparator = 0;
        if (p != null) {
            damageComparator = ad - (int)p.getVisibleDamage(pcPrimaryWeapon, Game.getPlayerEntity());
            toHitComparator = th - (int)p.getVisibleToHit(pcPrimaryWeapon, Game.getPlayerEntity());
            pnComparator = pn - (int)p.getVisiblePenetration(pcPrimaryWeapon, Game.getPlayerEntity());
        }
        Color adColor = (damageComparator < 0) ? Color.RED : (damageComparator == 0) ? Color.WHITE : Color.GREEN;
        Color thColor = (toHitComparator < 0) ? Color.RED : (toHitComparator == 0) ? Color.WHITE : Color.GREEN;
        Color pnColor = (pnComparator < 0) ? Color.RED : (pnComparator == 0) ? Color.WHITE : Color.GREEN;

        Color beatitudeColor = entity.getItem().identifiedBeatitude ? entity.getBeatitude().color : Color.WHITE;
        TextBlock tbMain = new TextBlock(entity.getVisibleNameWithQuantity(), beatitudeColor);
        tbMain.text = tbMain.text.substring(0, Math.min(width, tbMain.text.length()));
        tbMain.append(new TextBlock(" (", Color.WHITE))
                .append(new TextBlock("`" + ad, adColor, IconGlyph.DAMAGE.icon()))
                .append(new TextBlock("`" + th, thColor, IconGlyph.TOHIT.icon()))
                .append(new TextBlock("`" + pn, pnColor, IconGlyph.PENETRATION.icon()))
                .append(new TextBlock(")", Color.WHITE));
        tbMain.children.stream().findFirst().get().x = width;

        return tbMain;
    }

    @Override
    public Proc clone(Entity entity) {
        ProcWeaponRanged pw = new ProcWeaponRanged(damage, toHit, penetration, range, ammoType);
        return pw;
    }
}
