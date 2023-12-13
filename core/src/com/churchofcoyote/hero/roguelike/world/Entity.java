package com.churchofcoyote.hero.roguelike.world;

import com.churchofcoyote.hero.GameLoop;
import com.churchofcoyote.hero.glyphtile.PaletteEntry;
import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.game.MoverLogic;
import com.churchofcoyote.hero.roguelike.game.Rank;
import com.churchofcoyote.hero.roguelike.world.dungeon.Room;
import com.churchofcoyote.hero.roguelike.world.proc.*;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcEquippable;
import com.churchofcoyote.hero.roguelike.world.proc.item.ProcItem;
import com.churchofcoyote.hero.text.TextBlock;
import com.churchofcoyote.hero.util.Fov;
import com.churchofcoyote.hero.util.Point;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;
import java.util.function.BiConsumer;
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
    public String pluralName;

    //public Glyph glyph;
    public Point pos;
    public int roomId = -1;

    public List<Proc> procs = new ArrayList<>();
    public Collection<Integer> inventoryIds = new ArrayList<>();
    public Body body;

    public int containingEntity = -1;
    public String containingLevel = null;

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
    // TODO update all natural weapons to proc-based?
    public int naturalWeaponDamage;
    public int naturalWeaponToHit;
    public int naturalRangedWeaponDamage;
    public int naturalRangedWeaponToHit;
    public int naturalArmorClass;
    public int naturalArmorThickness;

    public int healingDelay = 3;
    public int healingRate = 1;

    public String phenotypeName;
    public String glyphName;
    public PaletteEntry palette;

    public boolean isManipulator;

    public String itemTypeKey;

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

    public void removeProc(Proc proc) {
        procs.remove(proc);
    }

    public Proc getProcByType(Class clazz) {
        for (Proc p : procs) {
            if (clazz.isAssignableFrom(p.getClass()))
                return p;
        }
        return null;
    }

    public String getVisibleName() {
        ItemType it = getItemType();
        if (it != null && it.identityHidden && !it.identified) {
            return it.unidentifiedName;
        }
        return name;
    }

    public String getVisiblePluralName() {
        ItemType it = getItemType();
        if (it != null && it.identityHidden && !it.identified) {
            if (it.unidentifiedPluralName != null) {
                return it.unidentifiedPluralName;
            } else {
                if (it.unidentifiedName.endsWith("s")) {
                    return it.unidentifiedName + "es";
                } else {
                    return it.unidentifiedName + "s";
                }
            }
        }
        if (pluralName != null) {
            return pluralName;
        } else {
            if (name.endsWith("s")) {
                return name + "es";
            } else {
                return name + "s";
            }
        }
    }

    public String getVisibleNameWithQuantity() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return item.quantity + " " + getVisiblePluralName();
        }
        return getVisibleName();
    }

    public String getVisibleNameThe() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return getVisibleNameWithQuantity();
        }
        return "the " + getVisibleNameWithQuantity();
    }

    public String getVisibleNameSingularOrSpecific() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return getVisibleNameWithQuantity();
        }
        return "a " + getVisibleName();
    }

    public String getVisibleNameSingularOrVague() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return "some " + getVisiblePluralName();
        }
        return "a " + getVisibleName();
    }

    public int getMoveCost() {
        return moveCost;
    }

    public void heal(int amount) {
        hitPoints = Math.min(hitPoints + amount, maxHitPoints);
    }

    public void hurt(int amount) {
        hitPoints = Math.max(hitPoints - amount, 0);
        if (this == Game.getPlayerEntity()) {
            return;
        }
        if (hitPoints <= 0) {
            dead = true;
        }
        if (dead) {
            MoverLogic.createCorpse(Game.getLevel(), this);
            destroy();
        }
    }

    public boolean canSee(Entity target) {
        return Fov.canSee(Game.getLevel(), pos, target.pos, visionRange);
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
            Boolean canOpenThisProc = p.preBeOpened(this, actor);
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
                p.postBeOpened(this, actor);
            }
            return true;
        }
        return false;
    }

    /*
    // returns 'true' if an attempt was made
    public boolean tryClose(Entity actor) {
        boolean canClose = false;
        for (Proc p : procs) {
            Boolean canCloseThisProc = p.preBeClosed(this, actor);
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
                p.postBeClosed(this, actor);
            }
            return true;
        }
        return false;
    }
     */

    public boolean pickup(Entity target) {
        boolean canBePickedUp = false;
        for (Proc p : target.procs) {
            Boolean attempt = p.preBePickedUp(target, this);
            if (attempt == null) {
                continue;
            } else if (attempt == true) {
                canBePickedUp = true;
            } else {
                return false;
            }
        }

        if (!canBePickedUp) {
            return false;
        }

        boolean canDoPickup = false;
        for (Proc p : this.procs) {
            Boolean attempt = p.preDoPickup(this, target);
            if (attempt == null) {
                continue;
            } else if (attempt == true) {
                canDoPickup = true;
            } else {
                return false;
            }
        }

        if (!canDoPickup) {
            return false;
        }

        Game.announceVis(this, target, "You pick up " + target.getVisibleNameThe() + ".",
                "You are picked up by " + getVisibleNameThe() + ".",
                this.getVisibleNameThe() + " picks up " + target.getVisibleNameThe() + ".",
                null);

        Game.getLevel().removeEntity(target);

        target = acquireWithStacking(target);

        Entity stackedInto = null;
        for (Proc p : this.procs) {
            p.postDoPickup(this, target);
        }
        // careful with this: if stacked, this operates on the entire stack
        for (Proc p : target.procs) {
            p.postBePickedUp(target, this);
        }
        return true;
    }


    public boolean equip(Entity target, BodyPart bp)
    {
        ProcEquippable pe = null;
        if (target != null) {
            pe = target.getEquippable();
        }

        // wielding a 2h in the offhand? no, it's stored in the primary hand
        if (pe != null && pe.equipmentFor == BodyPart.TWO_HAND && bp == BodyPart.OFF_HAND) {
            bp = BodyPart.PRIMARY_HAND;
        }

        // TODO error messages?
        if (target != null && !inventoryIds.contains(target.entityId)) {
            acquireWithStacking(target);
        }

        if (target != null && pe == null) {
            Game.announceVis(this, null, "That's not equippable in any slot.", null,
                    "Failed to equip: " + this.name + ", " + target.name, "Failed to equip: " + this.name + ", " + target.name);
            return false;
        }

        if (target != null && pe.equipmentFor != bp &&
                !(pe.equipmentFor == BodyPart.ANY_HAND && (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND)) &&
                !(pe.equipmentFor == BodyPart.TWO_HAND && bp == BodyPart.PRIMARY_HAND)) {
            Game.announceVis(this, null, "That's not equippable in that slot.", null,
                    "Failed to equip: " + this.name + ", " + target.name, "Failed to equip: " + this.name + ", " + target.name);
            return false;
        }
        if (target != null && body == null) {
            Game.announceVis(this, null, "Your body can't equip anything.", null,
                    "Failed to equip: " + this.name + ", " + target.name, "Failed to equip: " + this.name + ", " + target.name);
            return false;
        }
        if (target != null && !body.hasBodyPart(bp)) {
            Game.announceVis(this, null, "You don't have a " + bp.getName() + ".", null,
                    "Failed to equip: " + this.name + ", " + target.name, "Failed to equip: " + this.name + ", " + target.name);
            return false;
        }

        // unequip the previously equipped item
        HashMap<BodyPart, Entity> allToUnequip = new HashMap<>();
        if (body.getEquipment(bp) != null) {
            allToUnequip.put(bp, body.getEquipment(bp));
        }
        if (pe != null && pe.equipmentFor == BodyPart.TWO_HAND && body.getEquipment(BodyPart.OFF_HAND) != null) {
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
                Boolean val = p.preDoUnequip(this, alreadyEquippedBodyPart, alreadyEquipped);
                if (val != null && !val) {
                    if (this == Game.getPlayerEntity()) {
                        // TODO should be handled by other proc
                        Game.announce("You can't unequip your old item.");
                    }
                    return false;
                }
            }
            for (Proc p : alreadyEquipped.procs) {
                Boolean val = p.preBeUnequipped(alreadyEquipped, alreadyEquippedBodyPart, this);
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
                Game.announce("You unequip the " + alreadyEquipped.getVisibleName() + ".");
            }
            body.putEquipment(alreadyEquippedBodyPart, null);
            acquireWithStacking(alreadyEquipped);
            for (Proc p : this.procs) {
                p.postDoUnequip(this, alreadyEquippedBodyPart, alreadyEquipped);
            }
            for (Proc p : alreadyEquipped.procs) {
                p.postBeUnequipped(alreadyEquipped, alreadyEquippedBodyPart, this);
            }
            if (this == Game.getPlayerEntity())
            {
                Game.roguelikeModule.updateEquipmentWindow();
            }
        }

        if (target == null) {
            return true;
        }

        // equip the new item
        for (Proc p : this.procs) {
            Boolean val = p.preDoEquip(this, bp, target);
            if (val != null && !val) {
                if (this == Game.getPlayerEntity()) {
                    Game.announce("You fail to equip it.");
                }
                return false;
            }
        }
        for (Proc p : target.procs) {
            Boolean val = p.preBeEquipped(target, bp, this);
            if (val != null && !val) {
                if (this == Game.getPlayerEntity()) {
                    Game.announce("It can't be equipped.");
                }
                return false;
            }
        }

        ProcItem pi = target.getItem();
        Entity actualTarget = target;
        if (bp != BodyPart.RANGED_AMMO && pi.quantity > 1) {
            actualTarget = target.split(1);
        }

        // TODO announce with vis
        if (this == Game.getPlayerEntity()) {
            if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND ||
                    bp == BodyPart.ANY_HAND || bp == BodyPart.TWO_HAND ||
                    bp == BodyPart.RANGED_AMMO || bp == BodyPart.RANGED_WEAPON) {
                Game.announce("You wield " + actualTarget.getVisibleNameThe() + ".");
            } else {
                Game.announce("You wear " + actualTarget.getVisibleNameThe() + ".");
            }
        }

        for (Proc p : this.procs) {
            p.postDoEquip(this, bp, actualTarget);
        }
        for (Proc p : actualTarget.procs) {
            p.postBeEquipped(actualTarget, bp, this);
        }
        body.putEquipment(bp, actualTarget.entityId);
        if (actualTarget == target) {
            inventoryIds.remove(actualTarget.entityId);
        }
        if (this == Game.getPlayerEntity())
        {
            Game.roguelikeModule.updateEquipmentWindow();
        }
        getMover().setDelay(this, Game.ONE_TURN);
        return true;
    }

    public void identifyItem() {
        ItemType it = getItemType();
        if (it == null) {
            throw new RuntimeException("Tried to identify a non-item");
        }
        if (!it.identityHidden || it.identified) {
            return;
        }
        ProcItem pi = getItem();
        String preIdentified = getVisibleNameThe();
        it.identified = true;
        pi.identified = true;
        String postIdentified = getVisibleNameSingularOrSpecific();
        Game.announce("You identify " + preIdentified + " as " + postIdentified + ".");
    }

    public Entity acquireWithStacking(Entity target) {
        target.containingLevel = null;
        target.containingEntity = this.entityId;
        Entity stackedInto = null;
        for (int mergeTargetId : inventoryIds) {
            Entity mergeTarget = EntityTracker.get(mergeTargetId);
            if (mergeTarget.canStackWith(target)) {
                stackedInto = mergeTarget;
                mergeTarget.beStackedWith(target);
                target.destroy();
            }
        }
        if (stackedInto == null) {
            inventoryIds.add(target.entityId);
        }
        return (stackedInto != null) ? stackedInto : target;
    }

    public void dropItem(Entity target) {
        if (!inventoryIds.contains(target.entityId)) {
            throw new RuntimeException("Tried to drop item not in inventory: " + target.name);
        }
        // TODO predrop, postdrop
        inventoryIds.remove(target.entityId);
        Game.getLevel().addEntityWithStacking(target, pos);
        Game.announceVis(this, target, "You drop " + target.getVisibleNameThe() + ".",
                this.getVisibleNameThe() + " drops you.",
                this.getVisibleNameThe() + " drops " + target.getVisibleNameSingularOrSpecific(),
                null);
    }

    public void quaffItem(Entity target) {
        for (Proc p : this.procs) {
            Boolean val = p.preDoQuaff(this, target);
            if (val != null && !val) {
                return;
            }
        }
        for (Proc p : target.procs) {
            Boolean val = p.preBeQuaffed(target, this);
            if (val != null && !val) {
                return;
            }
        }
        Entity quaffedPotion = target;
        if (target.getItem().quantity > 1) {
            quaffedPotion = target.split(1);
        }
        Game.announceVis(this, null, "You quaff " + quaffedPotion.getVisibleNameThe() + ".",
                null,
                this.getVisibleNameThe() + " quaffs " + quaffedPotion.getVisibleNameSingularOrSpecific() + ".",
                null);
        for (Proc p : this.procs) {
            p.postDoQuaff(this, quaffedPotion);
        }
        for (Proc p : target.procs) {
            p.postBeQuaffed(quaffedPotion, this);
        }
        quaffedPotion.destroy();
        getMover().setDelay(this, Game.ONE_TURN);
    }

    public void readItem(Entity target) {
        for (Proc p : this.procs) {
            Boolean val = p.preDoRead(this, target);
            if (val != null && !val) {
                return;
            }
        }
        for (Proc p : target.procs) {
            Boolean val = p.preBeRead(target, this);
            if (val != null && !val) {
                return;
            }
        }
        Entity readScroll = target;
        if (target.getItem().quantity > 1) {
            readScroll = target.split(1);
        }
        Game.announceVis(this, null, "You read " + readScroll.getVisibleNameThe() + ".",
                null,
                this.getVisibleNameThe() + " reads " + readScroll.getVisibleNameSingularOrSpecific() + ".",
                null);
        for (Proc p : this.procs) {
            p.postDoRead(this, readScroll);
        }
        for (Proc p : target.procs) {
            p.postBeRead(readScroll, this);
        }
        readScroll.destroy();
        getMover().setDelay(this, Game.ONE_TURN);
    }

    public int getNaturalWeaponDamage() {
        return naturalWeaponDamage;
    }

    public int getNaturalWeaponToHit() {
        return naturalWeaponToHit;
    }

    public int getNaturalRangedWeaponDamage() {
        return naturalRangedWeaponDamage;
    }

    public int getNaturalRangedWeaponToHit() {
        return naturalRangedWeaponToHit;
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
            TextBlock tb = p.getNameBlock(this);
            if (tb != null) {
                return tb;
            }
        }
        return new TextBlock(getVisibleNameWithQuantity());
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

    public ItemType getItemType() {
        return Itempedia.get(itemTypeKey);
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

    public void forEachProc(BiConsumer<Entity, Proc> lambda) {
        for (Proc p : allProcsIncludingEquipmentAndInventory().collect(Collectors.toList())) {
            lambda.accept(this, p);
        }
    }

    public void changeRoom(Room oldRoom, Room newRoom) {
        if (oldRoom != null) {
            oldRoom.leave(this);
        }
        if (newRoom != null) {
            newRoom.enter(this);
        }
    }

    public void receiveItem(Entity e) {
        inventoryIds.add(e.entityId);
        e.containingEntity = this.entityId;
        e.containingLevel = null;
    }

    // TODO call this whenever things die or permanently leave the world
    // TODO maybe this needs a preDestroy instead?
    // TODO check that something isn't destroyed whenever interacting with it - throw error if it is
    public void destroy() {
        for (Proc p : procs) {
            p.beDestroyed();
        }

        if (containingEntity > -1) {
            Entity container = EntityTracker.get(containingEntity);
            if (container.inventoryIds.contains(entityId)) {
                container.inventoryIds.remove(entityId);
            } else {
                BodyPart bp = container.body.getParts().stream().filter(b -> container.body.getEquipment(b) == this).findFirst().orElse(null);
                if (bp != null) {
                    container.body.putEquipment(bp, -1);
                } else {
                    //throw new RuntimeException("Tried to destroy a " + name + " contained by " + container.name + " that wasn't in inventory or equipped");
                }
            }
        } else if (containingLevel != null) {
            // TODO other levels
            if (!Game.dungeon.getLevel(containingLevel).getEntities().contains(this)) {
                //throw new RuntimeException("Tried to destroy a " + name + " contained by a level, when it wasn't there!");
            }
            Game.dungeon.getLevel(containingLevel).removeEntity(this);
        } else {
            //System.out.println("DEBUG: Destroyed entity " + entityId + " (" + name + ") contained in no entity or level");
        }

        GameLoop.glyphEngine.destroyEntity(this);
        destroyed = true;
    }

    public boolean canStackWith(Entity other) {
        ProcItem thisItem = getItem();
        ProcItem otherItem = other.getItem();
        if (thisItem == null || otherItem == null) {
            return false;
        }
        if (itemTypeKey != other.itemTypeKey) {
            return false;
        }
        ItemType itemType = getItemType();
        if (!itemType.stackable) {
            return false;
        }
        if (thisItem.identified != otherItem.identified ||
            thisItem.status != otherItem.status) {
            return false;
        }
        return true;
    }

    public void beStackedWith(Entity other) {
        // assume canStackWith has already been tested
        getItem().quantity += other.getItem().quantity;
    }


    // TODO better error reporting...
    public void assertValid() {
        if (destroyed) {
            throw new RuntimeException("interacted with destroyed object: " + name);
        }
    }

    public boolean isValid() {
        return !destroyed;
    }

    public Entity split(int quantity) {
        assertValid();
        ProcItem pi = getItem();
        if (quantity < 1 || quantity >= pi.quantity) {
            throw new IllegalArgumentException("Tried to split " + quantity + " out of " + pi.quantity);
        }
        Entity other = EntityTracker.create();
        other.name = name;
        other.pluralName = pluralName;
        other.itemTypeKey = itemTypeKey;
        other.glyphName = glyphName;
        other.palette = palette;
        for (Proc p : procs) {
            Proc op = p.clone();
            if (op != null) {
                other.procs.add(op);
            }
        }
        ProcItem opi = other.getItem();
        pi.quantity -= quantity;
        opi.quantity = quantity;
        return other;
    }


}
