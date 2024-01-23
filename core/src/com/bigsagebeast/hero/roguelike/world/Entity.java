package com.bigsagebeast.hero.roguelike.world;

import com.bigsagebeast.hero.GameLoop;
import com.bigsagebeast.hero.enums.*;
import com.bigsagebeast.hero.glyphtile.PaletteEntry;
import com.bigsagebeast.hero.roguelike.game.*;
import com.bigsagebeast.hero.roguelike.world.ai.Tactic;
import com.bigsagebeast.hero.roguelike.world.dungeon.Room;
import com.bigsagebeast.hero.roguelike.world.proc.Proc;
import com.bigsagebeast.hero.roguelike.world.proc.ProcMover;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectConfusion;
import com.bigsagebeast.hero.roguelike.world.proc.effect.ProcEffectParalysis;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcItem;
import com.bigsagebeast.hero.roguelike.world.proc.monster.ProcMonster;
import com.bigsagebeast.hero.text.TextBlock;
import com.bigsagebeast.hero.util.Point;
import com.bigsagebeast.hero.roguelike.world.proc.item.ProcEquippable;
import com.bigsagebeast.hero.util.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
    public boolean proper = false;

    public Point pos;
    public int roomId = -1;
    public ArrayList<Entity> visibleEntities = new ArrayList<>();

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
    public int experienceToNext = 100;
    public int experienceAwarded = 0;
    public int level = 1;
    public int moveCost = 1000;
    // TODO update all natural weapons to proc-based?
    public int naturalWeaponDamage;
    public int naturalWeaponToHit;
    public int naturalWeaponPenetration;
    public int naturalRangedWeaponDamage;
    public int naturalRangedWeaponToHit;
    public int naturalRangedWeaponRange;
    public int naturalRangedWeaponPenetration;
    public int naturalArmorClass;
    public int naturalArmorThickness;
    public Statblock statblock = new Statblock(20);

    // TODO remove
    public int healingRate = 1;

    public String phenotypeName;
    public String[] glyphNames;
    public PaletteEntry palette;
    public boolean glyphFlipH; // flip glyph horizontally
    public boolean hide;
    public Gender gender = Gender.AGENDER;

    public boolean isManipulator;
    public Ambulation ambulation;
    public boolean incorporeal;

    public String itemTypeKey;

    public float visionRange = 7;
    public float hearingRange = 30;

    // to count the number of wandering monsters
    public boolean wanderer;
    // summoned monsters don't drop items or corpses
    public boolean summoned;

    public String toString() {
        return name + " " + pos;
    }

    public Collection<Entity> getInventoryEntities() {
        return inventoryIds.stream().map(EntityTracker::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Collection<Entity> getEquippedEntities() {
        if (body == null) {
            return Collections.emptyList();
        }
        return body.getParts().stream().map(body::getEquipment).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public boolean isEquipped() {
        if (containingEntity != EntityTracker.NONE) {
            // TODO: Better way to implement than searching every time?
            return EntityTracker.get(containingEntity).body.equipment.containsValue(entityId);
        }
        return false;
    }

    public Entity getContainer() {
        if (containingEntity < 0) {
            return null;
        }
        return EntityTracker.get(containingEntity);
    }

    public Entity getTopLevelContainer() {
        Entity currentEntity = this;
        while (currentEntity.containingEntity >= 0) {
            currentEntity = EntityTracker.get(currentEntity.containingEntity);
        }
        return currentEntity;
    }

    public void addProc(Proc proc)
    {
        procs.add(proc);
        proc.initialize(this);
    }

    public void addProcWithoutInitialize(Proc proc) {
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

    public List<Proc> getProcsByType(List<Class> classes) {
        ArrayList<Proc> matchingProcs = new ArrayList<>();
        for (Class<Proc> clazz : classes) {
            matchingProcs.addAll(procs.stream()
                    .filter(p -> clazz.isAssignableFrom(p.getClass())).collect(Collectors.toList()));
        }
        return matchingProcs;
    }

    // only returns the first
    public Proc getProcByTypeIncludingEquipment(Class clazz) {
        EntityProc found = allEntityProcsIncludingEquipment().filter(ep -> clazz.isAssignableFrom(ep.proc.getClass()))
                .findFirst().orElse(null);
        return found == null ? null : found.proc;
    }

    public List<Proc> getProcsByTypeIncludingEquipment(List<Class> classes) {
        ArrayList<Proc> matchingProcs = new ArrayList<>();
        for (Class<Proc> clazz : classes) {
            matchingProcs.addAll(allEntityProcsIncludingEquipment()
                    .filter(ep -> clazz.isAssignableFrom(ep.proc.getClass())).map(ep -> ep.proc).collect(Collectors.toList()));
        }
        return matchingProcs;
    }

    public Beatitude getBeatitude() {
        if (getItem() == null) {
            return Beatitude.UNCURSED;
        }
        return getItem().beatitude;
    }

    public String getBeatitudeString() {
        if (getItem() == null || !getItem().identifiedBeatitude || !getItemType().hasBeatitude) {
            return "";
        }
        return getBeatitude().description + " ";
    }

    public String getVisibleName() {
        String beatitude = getBeatitudeString();
        ItemType it = getItemType();
        if (it != null && it.identityHidden && !it.identified) {
            return beatitude + it.unidentifiedName;
        }

        return beatitude + name;
    }

    public String getVisiblePluralName() {
        String beatitude = getBeatitudeString();
        ItemType it = getItemType();
        if (it != null && it.identityHidden && !it.identified) {
            if (it.unidentifiedPluralName != null) {
                return beatitude + it.unidentifiedPluralName;
            } else {
                if (it.unidentifiedName.endsWith("s")) {
                    return beatitude + it.unidentifiedName + "es";
                } else {
                    return beatitude + it.unidentifiedName + "s";
                }
            }
        }
        if (pluralName != null) {
            return beatitude + pluralName;
        } else {
            if (name.endsWith("s")) {
                return beatitude + name + "es";
            } else {
                return beatitude + name + "s";
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

    public String getVisibleNameDefinite() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return getVisibleNameWithQuantity();
        }
        if (proper) {
            return getVisibleNameWithQuantity();
        }
        return "the " + getVisibleNameWithQuantity();
    }

    public String getVisibleNameIndefiniteOrSpecific() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return getVisibleNameWithQuantity();
        }
        if (proper) {
            return getVisibleNameWithQuantity();
        }
        String visibleName = getVisibleName();
        return Util.indefinite(visibleName) + " " + visibleName;
    }

    public String getVisibleNameIndefiniteOrVague() {
        ProcItem item = getItem();
        if (item != null && item.quantity > 1) {
            return "some " + getVisiblePluralName();
        }
        if (proper) {
            return getVisibleName();
        }
        String visibleName = getVisibleName();
        return Util.indefinite(visibleName) + " " + visibleName;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public void heal(float floatAmount) {
        int amount = Util.randomRound(floatAmount);
        hitPoints = Math.min(hitPoints + amount, maxHitPoints);
    }

    public void hurt(float floatAmount, boolean announceDeath, String deathMessage) {
        int amount = Util.randomRound(floatAmount);
        int preHitPoints = hitPoints;
        hitPoints = Math.max(hitPoints - amount, 0);
        if (hitPoints <= 0) {
            dead = true;
            if (this == Game.getPlayerEntity()) {
                Game.die(deathMessage);
                return;
            }
            if (announceDeath) {
                // TODO move player death elsewhere?
                Game.announceVis(this, null, null,
                        null,
                        getVisibleNameDefinite() + " dies.",
                        null);
            }
            if (getMover().wasRecentlyAttacked()) {
                // Maybe this should be setting death at the end of a turn, so, after death messages are processed
                Game.getPlayer().registerExperienceForKill(this);
            }
        }
        if (dead && GameLoop.roguelikeModule.isRunning()) {
            // TODO: Right now, this is getting called multiple times, and this is the first.
            this.forEachProcIncludingEquipment((e, p) -> p.postBeKilled(e, null, null));

            // tests to make sure we're not in a test duel
            MoverLogic.createCorpse(Game.getLevel(), this);
            destroy();
        }
        if (this == Game.getPlayerEntity()) {
            int threshold = Game.hpWarningThreshold * maxHitPoints / 100;
            if (hitPoints <= threshold && preHitPoints > threshold) {
                if (Game.lastHpWarning + 10000 < Game.time) {
                    Game.interruptAndBreak("HP warning!  HP under " + Game.hpWarningThreshold + "%.");
                    Game.lastHpWarning = Game.lastHpWarning;
                }
            }
        }
    }

    public void hurt(float amount, DamageType damageType, boolean announceDeath, String deathMessage) {
        ResistanceLevel resistLevel = getDamageTypeResist(damageType);
        amount = (int)Math.ceil(resistLevel.multiplier * amount);
        hurt(amount, announceDeath, deathMessage);
    }

    public void hurt(float amount, String deathMessage) {
        hurt(amount, true, deathMessage);
    }

    public void hurt(float amount, DamageType damageType, String deathMessage) {
        hurt(amount, damageType, true, deathMessage);
    }

    public boolean canSee(Entity target) {
        // TODO: Omniscient flag, instead of implying that incorporeal can see everything?
        if (incorporeal) {
            return true;
        }
        return visibleEntities.contains(target);
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

    public boolean pickupItem(Entity target) {
        return pickupItemWithQuantity(target, target.getItem().quantity);
    }

    public boolean pickupItemWithQuantity(Entity target, int quantity) {
        if (quantity == 0) {
            return false;
        }
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

        Entity pickupTarget;
        if (quantity == target.getItem().quantity) {
            pickupTarget = target;
        } else {
            pickupTarget = target.split(quantity);
        }

        Game.announceVis(this, target, "You pick up " + pickupTarget.getVisibleNameDefinite() + ".",
                "You are picked up by " + getVisibleNameDefinite() + ".",
                this.getVisibleNameDefinite() + " picks up " + pickupTarget.getVisibleNameDefinite() + ".",
                null);

        if (target == pickupTarget) {
            Game.getLevel().removeEntity(target);
        }

        pickupTarget = acquireWithStacking(pickupTarget);

        Entity stackedInto = null;
        for (Proc p : this.procs) {
            p.postDoPickup(this, pickupTarget);
        }
        // careful with this: if stacked, this operates on the entire stack
        for (Proc p : target.procs) {
            p.postBePickedUp(pickupTarget, this);
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
                !(pe.equipmentFor == BodyPart.TWO_HAND && bp == BodyPart.PRIMARY_HAND) &&
                !(pe.equipmentFor == BodyPart.RING && (bp == BodyPart.LEFT_RING || bp == BodyPart.RIGHT_RING))
        ) {
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
            if (alreadyEquipped.getItem().beatitude == Beatitude.CURSED) {
                Game.announce("Your " + alreadyEquipped.getVisibleName() + " is stuck to you!");
                return false;
            }
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
                GameLoop.roguelikeModule.updateEquipmentWindow();
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
                    bp == BodyPart.RANGED_WEAPON) {
                Game.announce("You wield " + actualTarget.getVisibleNameDefinite() + ".");
            } else if (bp == BodyPart.RANGED_AMMO) {
                Game.announce("You reload with " + actualTarget.getVisibleNameDefinite() + ".");
            } else {
                Game.announce("You wear " + actualTarget.getVisibleNameDefinite() + ".");
            }
        }

        if (bp != BodyPart.RANGED_AMMO && pi.beatitude == Beatitude.CURSED) {
            String weldpart;
            if (pe.equipmentFor == BodyPart.TWO_HAND) {
                weldpart = "hands";
            } else if (bp == BodyPart.PRIMARY_HAND || bp == BodyPart.OFF_HAND) {
                // include ranged?
                weldpart = "hand";
            } else {
                weldpart = "body";
            }
            Game.announceBad(actualTarget.getVisibleNameDefinite() + " welds itself to your " + weldpart + "!");
            actualTarget.identifyItemBeatitude();
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
            GameLoop.roguelikeModule.updateEquipmentWindow();
        }
        getMover().setDelay(this, Game.ONE_TURN);
        return true;
    }

    public void identifyItemType() {
        ItemType it = getItemType();
        if (it == null) {
            throw new RuntimeException("Tried to identify a non-item");
        }
        if (!it.identityHidden || it.identified) {
            return;
        }
        ProcItem pi = getItem();
        String preIdentified = getVisibleNameDefinite();
        it.identified = true;
        String postIdentified = getVisibleNameIndefiniteOrSpecific();
        Game.announceLoud("You identify " + preIdentified + " as " + postIdentified + ".");
        if (containingEntity >= 0) {
            EntityTracker.get(containingEntity).restack(this);
        }
    }

    public void identifyItemFully() {
        identifyItemFully(false);
    }

    public void identifyItemFully(boolean silent) {
        ItemType it = getItemType();
        ProcItem pi = getItem();
        if (it == null) {
            throw new RuntimeException("Tried to identify a non-item");
        }
        if ((!it.hasBeatitude || pi.identifiedBeatitude) && (!it.identityHidden || it.identified)) {
            return;
        }
        String preIdentified = getVisibleNameDefinite();
        it.identified = true;
        pi.identifiedBeatitude = true;
        String postIdentified = getVisibleNameIndefiniteOrSpecific();
        if (!silent) {
            Game.announceLoud("You identify " + preIdentified + " as " + postIdentified + ".");
        }
        if (containingEntity >= 0) {
            EntityTracker.get(containingEntity).restack(this);
        }
    }

    public void silentIdentifyItemFully() {
        ItemType it = getItemType();
        ProcItem pi = getItem();
        if (it == null) {
            throw new RuntimeException("Tried to identify a non-item");
        }
        if ((!it.hasBeatitude || pi.identifiedBeatitude) && (!it.identityHidden || it.identified)) {
            return;
        }
        it.identified = true;
        pi.identifiedBeatitude = true;
        if (containingEntity >= 0) {
            EntityTracker.get(containingEntity).restack(this);
        }
    }

    public void identifyItemBeatitude() {
        ProcItem pi = getItem();
        pi.identifiedBeatitude = true;
        // TODO should be distinct
    }

    public Entity acquireWithStacking(Entity target) {
        target.containingLevel = null;
        target.containingEntity = this.entityId;
        Entity stackedInto = null;
        for (int mergeTargetId : inventoryIds) {
            Entity mergeTarget = EntityTracker.get(mergeTargetId);
            if (mergeTarget != null && mergeTarget.canStackWith(target)) {
                stackedInto = mergeTarget;
            }
        }
        if (stackedInto == null && body.hasBodyPart(BodyPart.RANGED_AMMO)) {
            Entity ammo = body.getEquipment(BodyPart.RANGED_AMMO);
            if (ammo != null && ammo.canStackWith(target)) {
                stackedInto = ammo;
                if (this == Game.getPlayerEntity()) {
                    Game.announce("You add " + target.getVisibleNameIndefiniteOrSpecific() + " to your ammo.");
                }
            }
        }
        if (stackedInto != null) {
            stackedInto.beStackedWith(target);
        } else {
            inventoryIds.add(target.entityId);
        }
        return (stackedInto != null) ? stackedInto : target;
    }

    public void restack(Entity target) {
        if (target.isEquipped()) {
            return;
        }
        Entity foundTarget = null;
        for (int mergeTargetId : inventoryIds) {
            if (mergeTargetId == target.entityId) {
                continue;
            }
            Entity mergeTarget = EntityTracker.get(mergeTargetId);
            if (mergeTarget.canStackWith(target)) {
                foundTarget = mergeTarget;
            }
        }
        if (foundTarget != null) {
            foundTarget.beStackedWith(target);
        }
    }

    public void dropItem(Entity target) {
        dropItemWithQuantity(target, target.getItem().quantity);
    }

    public void dropItemWithQuantity(Entity target, int quantity) {
        if (quantity == 0) {
            return;
        }
        if (!inventoryIds.contains(target.entityId)) {
            throw new RuntimeException("Tried to drop item not in inventory: " + target.name);
        }
        Entity dropped;
        if (quantity == target.getItem().quantity) {
            dropped = target;
            inventoryIds.remove(dropped.entityId);
        } else {
            dropped = target.split(quantity);
        }
        // TODO predrop, postdrop
        Game.getLevel().addEntityWithStacking(dropped, pos);
        Game.announceVis(this, target, "You drop " + dropped.getVisibleNameDefinite() + ".",
                this.getVisibleNameDefinite() + " drops you.",
                this.getVisibleNameDefinite() + " drops " + dropped.getVisibleNameIndefiniteOrSpecific(),
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
        Game.announceVis(this, null, "You quaff " + quaffedPotion.getVisibleNameDefinite() + ".",
                null,
                this.getVisibleNameDefinite() + " quaffs " + quaffedPotion.getVisibleNameIndefiniteOrSpecific() + ".",
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

    public void eatItem(Entity target) {
        for (Proc p : target.procs) {
            Boolean result = p.preBeEaten(target, Game.getPlayerEntity());
            if (result == Boolean.FALSE) {
                return;
            }
        }

        ProcItem pi = target.getItem();
        Entity eatenEntity;
        if (pi.quantity == 1) {
            eatenEntity = target;
        } else {
            eatenEntity = target.split(1);
        }
        Game.announce("You eat " + eatenEntity.getVisibleNameDefinite() + ".");

        for (Proc p : eatenEntity.procs) {
            p.postBeEaten(eatenEntity, Game.getPlayerEntity());
        }
        eatenEntity.destroy();
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
        Game.announceVis(this, null, "You read " + readScroll.getVisibleNameDefinite() + ".",
                null,
                this.getVisibleNameDefinite() + " reads " + readScroll.getVisibleNameIndefiniteOrSpecific() + ".",
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

    public int getNaturalWeaponPenetration() {
        return naturalWeaponPenetration;
    }

    public int getNaturalRangedWeaponDamage() {
        return naturalRangedWeaponDamage;
    }

    public int getNaturalRangedWeaponToHit() {
        return naturalRangedWeaponToHit;
    }

    public int getNaturalRangedWeaponPenetration() {
        return naturalRangedWeaponPenetration;
    }

    public int getStat(Stat stat) {
        int val = statblock.get(stat);
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            val += ep.proc.getStatModifier(ep.entity, this, stat);
        }
        return Statblock.normalize(stat, val);
    }

    public float getSpeed() {
        float speed = statblock.speed;
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            Float speedMod = ep.proc.getSpeedMultiplier(ep.entity, this);
            if (speedMod != null) {
                speed *= speedMod;
            }
        }
        return speed;
    }

    public int getArmorClass() {
        int ac = naturalArmorClass;
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            ac += ep.proc.provideDefense(ep.entity);
        }
        ac += Stat.getScaling(statblock.get(Stat.AGILITY), 0.5f);
        return ac;
    }

    public int getArmorThickness() {
        int at = naturalArmorThickness;
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            at += ep.proc.provideArmorThickness(ep.entity);
        }
        return at;
    }

    public int getToHitBonus() {
        int bonus = 0;
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            bonus += ep.proc.provideToHitBonus(ep.entity);
        }
        return bonus;
    }

    public int getDamageBonus() {
        int bonus = 0;
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            bonus += ep.proc.provideDamageBonus(ep.entity);
        }
        return bonus;
    }

    public int getPenetrationBonus() {
        int bonus = 0;
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            bonus += ep.proc.providePenetrationBonus(ep.entity);
        }
        return bonus;
    }

    public List<StatusType> getStatusResists() {
        ArrayList<StatusType> resists = new ArrayList<>();
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            List<StatusType> pr = ep.proc.provideStatusResist(ep.entity);
            if (pr != null) {
                resists.addAll(pr);
            }
        }
        return resists;
    }

    public boolean testResistStatus(StatusType status) {
        return getStatusResists().contains(status);
    }

    public ResistanceLevel getDamageTypeResist(DamageType damageType) {
        ArrayList<DamageType> resists = new ArrayList<>();
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            List<DamageType> procResists = ep.proc.provideDamageTypeResist(ep.entity);
            if (procResists != null) {
                resists.addAll(procResists);
            }
        }
        long resistCount = resists.stream().filter(resist -> resist == damageType).count();
        return ResistanceLevel.counterToEnum((int)resistCount);
    }

    public TextBlock getNameBlock() {
        return getNameBlock(35);
    }

    public TextBlock getNameBlock(int width) {
        // TODO also have methods for prefixes and suffixes?
        // Maybe this method also handles overall colors for parts that aren't overridden?
        for (Proc p : procs) {
            TextBlock tb = p.getNameBlock(this, width);
            if (tb != null) {
                return tb;
            }
        }
        if (getItem() != null) {
            return new TextBlock(getVisibleNameWithQuantity(), getItem().getBeatitudeColor(this));
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

    public Tactic getTactic() {
        ProcMonster monster = (ProcMonster)getProcByType(ProcMonster.class);
        return monster.tactic;
    }

    public Phenotype getPhenotype() {
        return Bestiary.get(phenotypeName);
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

    public void postLoad() {
        for (Proc p : procs) {
            p.postLoad(this);
        }
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

    public Stream<EntityProc> entityProcs() {
        Stream<EntityProc> roomProcs = Stream.empty();
        if (roomId >= 0) {
            if (roomId < Game.getLevel().rooms.size()) {
                Room room = Game.getLevel().rooms.get(roomId);
                roomProcs = room.procs.stream().map(p -> new EntityProc(this, p));
            } else {
                GameLoop.error("Tried to get entityProc from bad room");
            }
        }
        return Stream.concat(procs.stream().map(p -> new EntityProc(this, p)), roomProcs);
    }

    public Stream<EntityProc> allEntityProcsIncludingEquipmentAndInventory() {
        return Stream.concat(entityProcs(),
                recursiveInventoryAndEquipment().flatMap(Entity::entityProcs));
    }

    public Stream<EntityProc> allEntityProcsIncludingEquipment() {
        return Stream.concat(entityProcs(),
                recursiveEquipment().flatMap(Entity::entityProcs));
    }

    public void forEachProc(BiConsumer<Entity, Proc> lambda) {
        for (Proc proc : procs) {
            lambda.accept(this, proc);
        }
    }

    public void forEachProcIncludingEquipment(BiConsumer<Entity, Proc> lambda) {
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            lambda.accept(ep.entity, ep.proc);
        }
    }

    public boolean forEachProcIncludingEquipmentAndInventoryFailOnFalse(BiFunction<Entity, Proc, Boolean> lambda) {
        for (EntityProc ep : allEntityProcsIncludingEquipmentAndInventory().collect(Collectors.toList())) {
            if (lambda.apply(ep.entity, ep.proc) == Boolean.FALSE) {
                return false;
            };
        }
        return true;
    }

    public boolean forEachProcIncludingEquipmentFailOnFalse(BiFunction<Entity, Proc, Boolean> lambda) {
        for (EntityProc ep : allEntityProcsIncludingEquipment().collect(Collectors.toList())) {
            if (lambda.apply(ep.entity, ep.proc) == Boolean.FALSE) {
                return false;
            };
        }
        return true;
    }

    public void changeRoom(Room oldRoom, Room newRoom) {
        if (oldRoom != null) {
            oldRoom.leave(this);
        }
        if (newRoom != null) {
            newRoom.enter(this);
            roomId = newRoom.roomId;
        } else {
            roomId = -1;
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
            if (container != null) {
                if (container.inventoryIds.contains(entityId)) {
                    container.inventoryIds.remove(entityId);
                } else {
                    BodyPart bp = container.body.getParts().stream().filter(b -> container.body.getEquipment(b) == this).findFirst().orElse(null);
                    if (bp != null) {
                        container.body.putEquipment(bp, null);
                    } else {
                        //throw new RuntimeException("Tried to destroy a " + name + " contained by " + container.name + " that wasn't in inventory or equipped");
                    }
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
        return thisItem.canStackWith(otherItem);
    }

    public void beStackedWith(Entity other) {
        // assume canStackWith has already been tested
        getItem().quantity += other.getItem().quantity;
        other.destroy();
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

        other.containingEntity = containingEntity;
        other.pluralName = pluralName;
        other.itemTypeKey = itemTypeKey;
        other.glyphNames = glyphNames;
        other.hide = hide;
        other.palette = palette;
        for (Proc p : procs) {
            Proc op = p.clone(other);
            if (op != null) {
                other.procs.add(op);
            }
        }
        ProcItem opi = other.getItem();
        pi.quantity -= quantity;
        opi.quantity = quantity;
        return other;
    }

    public void recalculateSecondaryStats() {
        int effectiveLevel = level == 0 ? 0 : level + 2;
        int newMaxHitPoints = (int)(Bestiary.get(phenotypeName).hitPoints + (effectiveLevel * GameEntities.hitPointsPerLevel(this)));
        int newMaxSpellPoints = (int)(Bestiary.get(phenotypeName).spellPoints + (effectiveLevel * GameEntities.spellPointsPerLevel(this)));
        int newMaxDivinePoints = (int)(Bestiary.get(phenotypeName).divinePoints + GameEntities.divinePoints(this));
        int deltaHitPoints = newMaxHitPoints - maxHitPoints;
        int deltaSpellPoints = newMaxSpellPoints - maxSpellPoints;
        int deltaDivinePoints = newMaxDivinePoints - maxDivinePoints;
        maxHitPoints += deltaHitPoints;
        maxSpellPoints += deltaSpellPoints;
        maxDivinePoints += deltaDivinePoints;
        hitPoints = Math.min(Math.max(hitPoints + deltaHitPoints, hitPoints), newMaxHitPoints);
        spellPoints = Math.min(Math.max(spellPoints + deltaSpellPoints, spellPoints), newMaxSpellPoints);
        divinePoints = Math.min(Math.max(divinePoints + deltaDivinePoints, divinePoints), newMaxDivinePoints);

    }

    // status tests
    public boolean isConfused() {
        return getProcByType(ProcEffectConfusion.class) != null;
    }

    public boolean isParalyzed() {
        return getProcByType(ProcEffectParalysis.class) != null;
    }
}
