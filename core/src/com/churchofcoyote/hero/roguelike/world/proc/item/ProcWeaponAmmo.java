package com.churchofcoyote.hero.roguelike.world.proc.item;

import com.badlogic.gdx.graphics.Color;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.AmmoType;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.Util;

public class ProcWeaponAmmo extends Proc {

    protected ProcWeaponAmmo() { super(); }
    public ProcWeaponAmmo(int averageDamage, int toHitBonus, AmmoType ammoType) {
        this();
        this.averageDamage = averageDamage;
        this.toHitBonus = toHitBonus;
        this.ammoType = ammoType;
    }

    public int averageDamage;
    public int toHitBonus;
    public AmmoType ammoType;

    public int averageDamage(Entity wielder) {
        return averageDamage;
    }

    public int toHitBonus(Entity wielder) {
        return toHitBonus;
    }

    public int averageDamage() {
        return averageDamage(Game.getPlayerEntity());
    }

    public int toHitBonus() {
        return toHitBonus(Game.getPlayerEntity());
    }

    @Override
    public Boolean preBePickedUp(Entity entity, Entity actor) { return true; }

    @Override
    public void postBePickedUp(Entity entity, Entity actor) {}

    @Override
    public TextBlock getNameBlock(Entity entity) {
        Entity pcPrimaryWeapon = Game.getPlayerEntity().body.getEquipment(BodyPart.PRIMARY_HAND);
        ProcWeaponAmmo p = null;
        if (pcPrimaryWeapon != null) {
            p = (ProcWeaponAmmo)pcPrimaryWeapon.getProcByType(ProcWeaponAmmo.class);
        }

        int ad = averageDamage();
        int th = toHitBonus();

        int damageComparator = 0;
        int toHitComparator = 0;
        if (p != null) {
            damageComparator = ad - p.averageDamage();
            toHitComparator = th - p.toHitBonus();
        }
        Color adColor = (damageComparator < 0) ? Color.RED : (damageComparator == 0) ? Color.WHITE : Color.GREEN;
        Color thColor = (toHitComparator < 0) ? Color.RED : (toHitComparator == 0) ? Color.WHITE : Color.GREEN;

        String adString = "" + ad;
        String thString = "" + th;
        if (th > 0) {
            thString = "+" + th;
        }
        String entityName = entity.getVisibleNameWithQuantity();
        int adLocation = entityName.length() + 2;
        int thLocation = adLocation + adString.length() + 1;
        String mainString = String.format("%s (%s,%s)", entityName, Util.repeat(" ", adString.length()), Util.repeat(" ", thString.length()));
        TextBlock tbMain = new TextBlock(mainString, Color.WHITE);
        TextBlock tbDamage = new TextBlock(adString, adLocation, 0, adColor);
        TextBlock tbHit = new TextBlock(thString, thLocation, 0, thColor);
        tbMain.addChild(tbDamage);
        tbMain.addChild(tbHit);
        return tbMain;
    }

    @Override
    public Proc clone() {
        ProcWeaponAmmo pw = new ProcWeaponAmmo(averageDamage, toHitBonus, ammoType);
        return pw;
    }
}
