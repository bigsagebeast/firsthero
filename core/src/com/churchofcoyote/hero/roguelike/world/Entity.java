package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Entity {

    public int entityId;

    // for deserialization only
    private Entity() {}

    public Entity(int entityId) {
        this.entityId = entityId;
    }

    public String name;
    //public Glyph glyph;
    public Point pos;

    public List<Proc> procs = new ArrayList<>();
    public Collection<Integer> inventoryIds = new ArrayList<>();
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
    public boolean peaceful = false;
    public int experience = 0;
    public int experienceToNext = 40;
    public int experienceAwarded = 0;
    public int level = 1;
    public int moveCost = 1000;
    public int naturalWeaponDamage;
    public int naturalWeaponToHit;
    public int naturalArmorClass;
    public int naturalArmorThickness;

    public int healingDelay = 3;
    public int healingRate = 1;

    public String phenotypeName;
    public String glyphName;
    public PaletteEntry palette;

    public boolean isManipulator;

    public String itemTypeName;

    public Rank stats = Rank.C;

    float visionRange = 15;
    float hearingRange = 30;

    public String toString() {
        return name + " " + pos;
    }

    public Collection<Entity> getInventoryEntities() {
        return inventoryIds.stream().map(EntityTracker::get).collect(Collectors.toList());
    }

    public Collection<Entity> getEquippedEntities() {
        if (body == null) {
            return Collections.emptyList();
        }
        return body.getParts().stream().map(body::getEquipment).filter(e -> e != null).collect(Collectors.toList());
    }

    public void addProc(Proc proc)
    {
        procs.add(proc);
    }
    public Proc getProcByType(Class klass) {
        for (Proc p : procs) {
            if (klass.isAssignableFrom(p.getClass()))
                return p;
        }
        return null;
    }

    public String getVisibleName() {
        return name;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public void heal(int amount) {
        hitPoints = Math.min(hitPoints + amount, maxHitPoints);
    }

    public void hurt(int amount) {
        hitPoints = Math.max(hitPoints - amount, 0);
    }

    public boolean canSee(Entity target) {
        return Fov.canSee(Game.getLevel(), pos, target.pos, visionRange, 0.01f);
    }

    public boolean canHear(Entity target) {
        return pos.distance(target.pos) <= hearingRange;
    }

    public boolean isObstructive() {
        for (Proc p : procs) {
            if (p.isObstructive() == Boolean.TRUE) {
                return true;
            }
        }
        return false;
    }

    public boolean isObstructiveToManipulators() {
        for (Proc p : procs) {
            if (p.isObstructiveToManipulators() == Boolean.TRUE) {
                return true;
            }
        }
        return false;
    }

    public boolean isObstructiveToVision() {
        for (Proc p : procs) {
            if (p.isObstructiveToVision() == Boolean.TRUE) {
                return true;
            }
        }
        return false;
    }

    // returns 'true' if an attempt was made
    public boolean tryOpen(Entity actor) {
        boolean canOpen = false;
        for (Proc p : procs) {
            Boolean canOpenThisProc = p.preBeOpened(actor);
            if (canOpenThisProc == Boolean.TRUE) {
                canOpen = true;
            } else if (canOpenThisProc == Boolean.FALSE) {
                // we tried, this probably uses up a turn
                // and more importantly, means we won't say "You can't open anything there."
                return true;
            }
        }
        if (canOpen) {
            for (Proc p : procs) {
                p.postBeOpened(actor);
            }
            return true;
        }
        return false;
    }

    // returns 'true' if an attempt was made
    public boolean tryClose(Entity actor) {
        boolean canClose = false;
        for (Proc p : procs) {
            Boolean canCloseThisProc = p.preBeClosed(actor);
            if (canCloseThisProc == Boolean.TRUE) {
                canClose = true;
            } else if (canCloseThisProc == Boolean.FALSE) {
                // we tried, this probably uses up a turn
                // and more importantly, means we won't say "You can't open anything there."
                return true;
            }
        }
        if (canClose) {
            for (Proc p : procs) {
                p.postBeClosed(actor);
            }
            return true;
        }
        return false;
    }

    public boolean equip(Entity e, BodyPart bp)
    {
        ProcEquippable pe = e.getEquippable();

        // wielding a 2h in the offhand? no, it's stored in the primary hand
        if (pe.equipmentFor == BodyPart.TWO_HAND && bp == BodyPart.OFF_HAND) {
            bp = BodyPart.PRIMARY_HAND;
        }

        // TODO error messages?
        if (!inventoryIds.contains(e.entityId)) {
            inventoryIds.add(e.entityId);
            /*
            if (this == Game.getPlayerEntity()) {
                Game.announce("That's not in your inventory.");
            }
            return false;
             */
        }

        if (pe == null) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("That's not equippable in any slot.");
            }
            return false;
        }

        if (pe.equipmentFor != bp &&
                !(pe.equipmentFor == BodyPart.ANY_HAND && (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND)) &&
                !(pe.equipmentFor == BodyPart.TWO_HAND && bp == BodyPart.PRIMARY_HAND)) {
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
        if (!body.hasBodyPart(bp)) {
            if (this == Game.getPlayerEntity()) {
                Game.announce("You don't have a " + bp.getName() + ".");
            }
            return false;
        }

        // unequip the previously equipped item
        HashMap<BodyPart, Entity> allToUnequip = new HashMap<>();
        if (body.getEquipment(bp) != null) {
            allToUnequip.put(bp, body.getEquipment(bp));
        }
        if (pe.equipmentFor == BodyPart.TWO_HAND && body.getEquipment(BodyPart.OFF_HAND) != null) {
            allToUnequip.put(BodyPart.OFF_HAND, body.getEquipment(BodyPart.OFF_HAND));
        }
        if (bp == BodyPart.OFF_HAND &&
                body.getEquipment(BodyPart.PRIMARY_HAND) != null &&
                body.getEquipment(BodyPart.PRIMARY_HAND).getEquippable().equipmentFor == BodyPart.TWO_HAND) {
            allToUnequip.put(BodyPart.PRIMARY_HAND, body.getEquipment(BodyPart.PRIMARY_HAND));
        }
        for (BodyPart alreadyEquippedBodyPart : allToUnequip.keySet()) {
            Entity alreadyEquipped = allToUnequip.get(alreadyEquippedBodyPart);
            for (Proc p : this.procs) {
                Boolean val = p.preDoUnequip(alreadyEquippedBodyPart, alreadyEquipped);
                if (val != null && !val) {
                    if (this == Game.getPlayerEntity()) {
                        // TODO should be handled by other proc
                        Game.announce("You can't unequip your old item.");
                    }
                    return false;
                }
            }
            for (Proc p : alreadyEquipped.procs) {
                Boolean val = p.preBeUnequipped(alreadyEquippedBodyPart, this);
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
            body.putEquipment(alreadyEquippedBodyPart, null);
            inventoryIds.add(alreadyEquipped.entityId);
            for (Proc p : this.procs) {
                p.postDoUnequip(alreadyEquippedBodyPart, alreadyEquipped);
            }
            for (Proc p : alreadyEquipped.procs) {
                p.postBeUnequipped(alreadyEquippedBodyPart, this);
            }
            if (this == Game.getPlayerEntity())
            {
                Game.roguelikeModule.updateEquipmentWindow();
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
        body.putEquipment(bp, e.entityId);
        inventoryIds.remove(e.entityId);
        if (this == Game.getPlayerEntity())
        {
            Game.roguelikeModule.updateEquipmentWindow();
        }
        getMover().setDelay(Game.ONE_TURN);
        return true;
    }

    public int getNaturalWeaponDamage() {
        return naturalWeaponDamage;
    }

    public int getNaturalWeaponToHit() {
        return naturalWeaponToHit;
    }

    public int getArmorClass() {
        int ac = naturalArmorClass;
        for (Proc p : allProcsIncludingEquipment().collect(Collectors.toList())) {
            ac += p.provideArmorClass();
        }
        return ac;
    }

    public int getArmorThickness() {
        int at = naturalArmorThickness;
        for (Proc p : allProcsIncludingEquipment().collect(Collectors.toList())) {
            at += p.provideArmorThickness();
        }
        return at;
    }

    public TextBlock getNameBlock() {
        // TODO also have methods for prefixes and suffixes?
        // Maybe this method also handles overall colors for parts that aren't overridden?
        for (Proc p : procs) {
            TextBlock tb = p.getNameBlock();
            if (tb != null) {
                return tb;
            }
        }
        return new TextBlock(getVisibleName());
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

    public Stream<Entity> recursiveInventoryAndEquipment() {
        LinkedList<Entity> allEntities = new LinkedList<>();
        allEntities.addAll(getInventoryEntities());
        allEntities.addAll(getEquippedEntities());
        LinkedList<Entity> subEntities = new LinkedList<>();
        for (Entity e : allEntities) {
            subEntities.addAll(e.recursiveInventoryAndEquipment().collect(Collectors.toList()));
        }
        return Stream.concat(allEntities.stream(), subEntities.stream());
    }

    public Stream<Entity> recursiveEquipment() {
        LinkedList<Entity> allEntities = new LinkedList<>();
        allEntities.addAll(getEquippedEntities());
        LinkedList<Entity> subEntities = new LinkedList<>();
        for (Entity e : allEntities) {
            subEntities.addAll(e.recursiveInventoryAndEquipment().collect(Collectors.toList()));
        }
        return Stream.concat(allEntities.stream(), subEntities.stream());
    }

    public Stream<Proc> allProcsIncludingEquipmentAndInventory() {
        return Stream.concat(procs.stream(),
                recursiveInventoryAndEquipment().flatMap(entity -> entity.procs.stream()));
    }

    public Stream<Proc> allProcsIncludingEquipment() {
        return Stream.concat(procs.stream(),
                recursiveEquipment().flatMap(entity -> entity.procs.stream()));
    }

    public void forEachProc(Consumer<Proc> lambda) {
        allProcsIncludingEquipmentAndInventory().forEach(lambda);
    }

    public void receiveItem(Entity e) {
        inventoryIds.add(e.entityId);
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

    public boolean isValid() {
        return !destroyed;
    }
}
