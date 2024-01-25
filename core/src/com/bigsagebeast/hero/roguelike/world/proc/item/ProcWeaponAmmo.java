package com.bigsagebeast.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.bigsagebeast.hero.enums.Beatitude;
import com.bigsagebeast.hero.enums.Stat;
import com.bigsagebeast.hero.glyphtile.IconGlyph;
import com.bigsagebeast.hero.roguelike.game.EquipmentScaling;
import com.bigsagebeast.hero.roguelike.world.AmmoType;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Util;
import com.bigsagebeast.hero.roguelike.game.Game;
import com.bigsagebeast.hero.roguelike.world.BodyPart;
import com.bigsagebeast.hero.roguelike.world.Entity;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcWeaponAmmo extends Proc {

    public ProcWeaponAmmo() { super(); }
    public ProcWeaponAmmo(int damage, int toHit, int penetration, AmmoType ammoType, boolean canThrow, int throwRange) {
        this();
        this.damage = damage;
        this.toHit = toHit;
        this.penetration = penetration;
        this.ammoType = ammoType;
        this.canThrow = canThrow;
        this.throwRange = throwRange;
    }

    public int damage;
    public int toHit;
    public int penetration;
    public AmmoType ammoType;
    public boolean canThrow;
    public int throwRange = 4;

    @Override
    public String getUnidDescription(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ammo type: " + ammoType.description);
        sb.append(". Base stats: Damage ").append(Util.formatFloat(damage));
        sb.append(" To-Hit ").append(Util.formatFloat(toHit));
        sb.append(" Penetration ").append(Util.formatFloat(penetration));
        sb.append("\n\nAmmo stats are combined with ranged weapon stats. See the ranged weapon for more details.");

        return sb.toString();
    }


    public int averageDamage(Entity entity, Entity wielder) {
        return damage;
    }

    public int toHitBonus(Entity entity, Entity wielder) {
        return toHit;
    }

    public int penetration(Entity entity, Entity wielder) { return penetration; }

    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {}
    @Override
    public TextBlock getNameBlock(Entity entity, int width) {
        int nameWidth = width - 8;
        int ad = (int) averageDamage(entity, Game.getPlayerEntity());
        int th = (int) toHitBonus(entity, Game.getPlayerEntity());
        int pn = (int) penetration(entity, Game.getPlayerEntity());

        Color beatitudeColor = entity.getItem().identifiedBeatitude ? entity.getBeatitude().color : Color.WHITE;
        TextBlock tbMain = new TextBlock(entity.getVisibleNameWithQuantity(), beatitudeColor);
        tbMain.text = tbMain.text.substring(0, Math.min(nameWidth, tbMain.text.length()));
        tbMain.append(new TextBlock(" (", Color.WHITE))
                .append(new TextBlock("`" + ad, Color.WHITE, IconGlyph.DAMAGE.icon()))
                .append(new TextBlock("`" + th, Color.WHITE, IconGlyph.TOHIT.icon()))
                .append(new TextBlock("`" + pn, Color.WHITE, IconGlyph.PENETRATION.icon()))
                .append(new TextBlock(")", Color.WHITE));
        tbMain.children.stream().findFirst().get().x = nameWidth;

        return tbMain;
    }


    @Override
    public Proc clone(Entity entity) {
        ProcWeaponAmmo pw = new ProcWeaponAmmo(damage, toHit, penetration, ammoType, canThrow, throwRange);
        return pw;
    }
}
