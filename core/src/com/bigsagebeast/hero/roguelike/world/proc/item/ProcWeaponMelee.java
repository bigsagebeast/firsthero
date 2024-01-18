package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.enums.WeaponType;
import com.bigsagebeast.hero.glyphtile.EntityGlyph;
import com.bigsagebeast.hero.glyphtile.GlyphIndex;
import com.bigsagebeast.hero.glyphtile.IconGlyph;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.util.Util;

import java.util.HashMap;
import java.util.Map;

public class ProcWeaponMelee extends Proc {

    public ProcWeaponMelee() { super(); }

    public WeaponType weaponType = WeaponType.BROAD_BLADE;
    public float damage = 7;
    public float toHit = 3;
    public float defense = 0;
    public float penetration = 0;
    public Map<Stat, EquipmentScaling> scaling = new HashMap<>();

    @Override
    public String getUnidDescription(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append(Util.capitalize(weaponType.name));
        //sb.append(String.format(". Base Stats: Damage %.0f To-Hit %.0f Defense %.0f Penetration %.0f.\nScaling: ", damage, toHit, defense, penetration));
        sb.append(". Base stats: Damage ").append(Util.formatFloat(damage));
        sb.append(" To-Hit ").append(Util.formatFloat(toHit));
        sb.append(" Defense ").append(Util.formatFloat(defense));
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
            if (es.defense != 0) {
                sb.append(" Defense ").append(Util.formatFloat(es.defense));
            }
            if (es.penetration != 0) {
                sb.append(" Penetration ").append(Util.formatFloat(es.penetration));
            }
            sb.append(". ");
        }

        sb.append(String.format("\nFor you: Damage %s To-Hit %s Penetration %s Defense %s", Util.formatFloat(getDamage(Game.getPlayerEntity())), Util.formatFloat(getToHit(Game.getPlayerEntity())), Util.formatFloat(getPenetration(Game.getPlayerEntity())), Util.formatFloat(getDefense(Game.getPlayerEntity()))));
        return sb.toString();
    }

    public float getDamage(Entity wielder) {
        float accum = damage;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).damage);
        }
        return accum;
    }

    public float getToHit(Entity wielder) {
        float accum = toHit;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).toHit);
        }
        return accum;
    }

    public float getPenetration(Entity wielder) {
        float accum = penetration;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).penetration);
        }
        return accum;
    }

    public float getDefense(Entity wielder) {
        float accum = defense;
        for (Stat stat : scaling.keySet()) {
            accum += Stat.getScalingWithMinimum(wielder.statblock.get(stat), scaling.get(stat).defense);
        }
        return accum;
    }

    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {}

    @Override
    public TextBlock getNameBlock(Entity entity) {

        Entity pcPrimaryWeapon = Game.getPlayerEntity().body.getEquipment(BodyPart.PRIMARY_HAND);
        ProcWeaponMelee p = null;
        if (pcPrimaryWeapon != null) {
            p = (ProcWeaponMelee)pcPrimaryWeapon.getProcByType(ProcWeaponMelee.class);
        }

        int ad = (int) getDamage(Game.getPlayerEntity());
        int th = (int) getToHit(Game.getPlayerEntity());
        int pn = (int) getPenetration(Game.getPlayerEntity());
        int de = (int) getDefense(Game.getPlayerEntity());

        int damageComparator = 0;
        int toHitComparator = 0;
        int pnComparator = 0;
        int deComparator = 0;
        if (p != null) {
            damageComparator = ad - (int)p.getDamage(Game.getPlayerEntity());
            toHitComparator = th - (int)p.getToHit(Game.getPlayerEntity());
            pnComparator = pn - (int)p.getPenetration(Game.getPlayerEntity());
            deComparator = de - (int)p.getDefense(Game.getPlayerEntity());
        }
        Color adColor = (damageComparator < 0) ? Color.RED : (damageComparator == 0) ? Color.WHITE : Color.GREEN;
        Color thColor = (toHitComparator < 0) ? Color.RED : (toHitComparator == 0) ? Color.WHITE : Color.GREEN;
        Color pnColor = (pnComparator < 0) ? Color.RED : (pnComparator == 0) ? Color.WHITE : Color.GREEN;
        Color deColor = (deComparator < 0) ? Color.RED : (deComparator == 0) ? Color.WHITE : Color.GREEN;
/*
        String adString = "" + ad;
        String thString = "" + th;

        String entityName = entity.getVisibleNameWithQuantity();
        int adLocation = entityName.length() + 2;
        int thLocation = adLocation + adString.length() + 1;
        String mainString = String.format("%s (%s,%s)", entityName, Util.repeat(" ", adString.length()), Util.repeat(" ", thString.length()));
        TextBlock tbMain = new TextBlock(mainString, Color.WHITE);
        TextBlock tbDamage = new TextBlock(adString, adLocation, 0, adColor);
        TextBlock tbHit = new TextBlock(thString, thLocation, 0, thColor);
        tbMain.addChild(tbDamage);
        tbMain.addChild(tbHit);

 */
        TextBlock tbMain = new TextBlock(entity.getVisibleNameWithQuantity() + " (", Color.WHITE);
        tbMain.append(new TextBlock("`" + ad, adColor, IconGlyph.DAMAGE.icon()))
                .append(new TextBlock("`" + th, thColor, IconGlyph.TOHIT.icon()))
                .append(new TextBlock("`" + pn, pnColor, IconGlyph.PENETRATION.icon()))
                .append(new TextBlock("`" + de, deColor, IconGlyph.DEFENSE.icon()))
                .append(new TextBlock(")", Color.WHITE));

        return tbMain;

        //String mainString = entity.getVisibleNameWithQuantity();
        //return new TextBlock(mainString, Color.WHITE);
    }

    @Override
    public String getStatline(Entity entity, Entity wielder) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append((int)getToHit(wielder));
        sb.append(",").append((int)getDamage(wielder));
        sb.append(" P").append((int)getPenetration(wielder));
        int defense = (int)getDefense(wielder);
        if (defense != 0) {
            sb.append(" D").append(defense);
        }
        sb.append(")");
        return sb.toString();
    }


    @Override
    public Proc clone(Entity entity) {
        ProcWeaponMelee pw = new ProcWeaponMelee();
        pw.damage = damage;
        pw.toHit = toHit;
        pw.penetration = penetration;
        pw.defense = defense;
        for (Stat key : scaling.keySet()) {
            pw.scaling.put(key, scaling.get(key));
        }
        return pw;
    }
}
