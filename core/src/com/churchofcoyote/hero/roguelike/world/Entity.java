package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.engine.asciitile.Glyph;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Player;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.ai.Strategy;
import com.churchofcoyote.hero.roguelike.world.proc.Proc;
import com.churchofcoyote.hero.roguelike.world.proc.ProcEquippable;
import com.churchofcoyote.hero.roguelike.world.proc.ProcItem;
import com.churchofcoyote.hero.roguelike.world.proc.ProcMover;
import com.churchofcoyote.hero.util.Point;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    public String name;
    public Glyph glyph;
    public Point pos;

    public Strategy strategy;

    public List<Proc> procs = new ArrayList<>();
    public List<Entity> inventory = new ArrayList<>();
    public Body body;

    public boolean dead = false;
    public boolean destroyed = false;

    // combat stats
    public int hitPoints;
    public int spellPoints;
    public int divinePoints;
    public int maxHitPoints;
    public int maxSpellPoints;
    public int maxDivinePoints;

    public Phenotype phenotype;
    public String glyphName;
    public PaletteEntry palette;

    public Rank stats = Rank.C;

    public void addProc(Proc proc)
    {
        procs.add(proc);
    }

    public String getVisibleName(Player p) {
        return name;
    }

    public boolean equip(Entity e, BodyPart bp)
    {
        // TODO error messages?
        if (!inventory.contains(e)) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("That's not in your inventory.");
            }
            return false;
        }
        ProcEquippable pe = e.getEquippable();
        if (pe == null) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("That's not equippable in any slot.");
            }
            return false;
        }

        if (pe.equipmentFor != bp &&
                !(pe.equipmentFor == BodyPart.ANY_HAND && (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND))) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("That's not equippable in that slot.");
            }
            return false;
        }
        if (body == null) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("Your body can't equip anything.");
            }
            return false;
        }
        if (!body.bodyPlan.hasPart(bp)) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("You don't have a " + bp.getName() + ".");
            }
            return false;
        }

        // unequip the previously equipped item
        Entity alreadyEquipped = body.equipment.get(bp);
        if (alreadyEquipped != null) {
            for (Proc p : this.procs) {
                Boolean val = p.preDoUnequip(bp, alreadyEquipped);
                if (val != null && !val) {
                    if (this == Game.getPlayerEntity()) {
                        // TODO should be handled by other proc
                        Game.announce("You can't unequip your old item.");
                    }
                    return false;
                }
            }
            for (Proc p : alreadyEquipped.procs) {
                Boolean val = p.preBeUnequipped(bp, this) == false;
                if (val != null && !val) {
                    if (this == Game.getPlayerEntity()) {
                        // TODO should be handled by other proc
                        Game.announce("Your old item refuses to be unequipped.");
                    }
                    return false;
                }
            }

            if (this == Game.getPlayerEntity()) {
                // TODO announce with vis
                Game.announce("You unequip the " + alreadyEquipped.name + ".");
            }
            body.equipment.put(bp, null);
            inventory.add(alreadyEquipped);
            for (Proc p : this.procs) {
                p.postDoUnequip(bp, alreadyEquipped);
            }
            for (Proc p : alreadyEquipped.procs) {
                p.postBeUnequipped(bp, this);
            }
        }

        // equip the new item
        for (Proc p : this.procs) {
            Boolean val = p.preDoEquip(bp, e);
            if (val != null && !val) {
                if (this == Game.getPlayerEntity()) {
                    Game.announce("You fail to equip it.");
                }
                return false;
            }
        }
        for (Proc p : e.procs) {
            Boolean val = p.preBeEquipped(bp, this);
            if (val != null && !val) {
                if (this == Game.getPlayerEntity()) {
                    Game.announce("It can't be equipped.");
                }
                return false;
            }
        }

        // TODO announce with vis
        if (this == Game.getPlayerEntity()) {
            if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND ||
                    bp == BodyPart.ANY_HAND || bp == BodyPart.TWO_HAND) {
                Game.announce("You wield the " + e.name + ".");
            } else {
                Game.announce("You wear the " + e.name + ".");
            }
        }

        for (Proc p : this.procs) {
            p.postDoEquip(bp, e);
        }
        for (Proc p : e.procs) {
            p.postBeEquipped(bp, this);
        }
        body.equipment.put(bp, e);
        inventory.remove(alreadyEquipped);
        return true;
    }

    public ProcMover getMover() {
        for (Proc p : procs) {
            if (p instanceof ProcMover) {
                return (ProcMover)p;
            }
        }
        return null;
    }

    public ProcItem getItem() {
        for (Proc p : procs) {
            if (p instanceof ProcItem) {
                return (ProcItem)p;
            }
        }
        return null;
    }

    // temporary approach
    public ProcEquippable getEquippable() {
        for (Proc p : procs) {
            if (p instanceof ProcEquippable) {
                return (ProcEquippable)p;
            }
        }
        return null;
    }

    public Boolean containsProc(Class c) {
        for (Proc p : procs) {
            if (p.getClass() == c) {
                return true;
            }
        }
        return false;
    }

    public void receiveItem(Entity e) {
        inventory.add(e);
    }

    // TODO call this whenever things die or permanently leave the world
    // TODO maybe this needs a preDestroy instead?
    // TODO check that something isn't destroyed whenever interacting with it - throw error if it is
    public void destroy() {
        for (Proc p : procs) {
            p.beDestroyed();
        }
        GameLoop.glyphEngine.destroyEntity(this);
        destroyed = true;
    }
}
