package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.churchofcoyote.hero.roguelike.world.EntityTracker;
import com.churchofcoyote.hero.text.TextBlock;
import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Proc {

    @JsonIgnore
    public Entity entity;

    public int entityId;

    public long nextAction = -1;
    public boolean active;

    // for deserialization
    protected Proc() {}
    public Proc(Entity e) {
        entity = e;
        active = true;
    }

    // Custom logic before serialization
    @JsonGetter("entityId")
    public int getEntityIdForSerialization() {
        entityId = entity.entityId;
        return entityId;
    }

    public void clearDelay() {
        nextAction = Game.time;
    }

    public void setDelay(long delay) {
        if (delay == 0) {
            throw new RuntimeException("Set delay of 0 for " + this.getClass() + " on " + entity.name);
        }
        nextAction = Game.time + delay;
    }

    public boolean hasAction() { return false; }

    public boolean isMover() {
        return false;
    }

    public void act() { }

    // activates every 1000
    public void turnPassed() { }

    public TextBlock getNameBlock() { return null; }

    // return true if pickup is allowed, false if it's aborted, null if no opinion
    public Boolean preBePickedUp(Entity actor) { return null; }
    public void postBePickedUp(Entity actor) {}
    public Boolean preBeDropped(Entity actor) { return null; }
    public void postBeDropped(Entity actor) {}

    public Boolean preDoPickup(Entity target) { return null; }
    public void postDoPickup(Entity target) {}
    public Boolean preDoDrop(Entity target) { return null; }
    public void postDoDrop(Entity target) {}

    public Boolean preBeEquipped(BodyPart bp, Entity actor) { return null; }
    public void postBeEquipped(BodyPart bp, Entity actor) {}
    public Boolean preBeUnequipped(BodyPart bp, Entity actor) { return null; }
    public void postBeUnequipped(BodyPart bp, Entity actor) {}

    public Boolean preDoEquip(BodyPart bp, Entity target) { return null; }
    public void postDoEquip(BodyPart bp, Entity target) {}
    public Boolean preDoUnequip(BodyPart bp, Entity target) { return null; }
    public void postDoUnequip(BodyPart bp, Entity target) {}

    public void postBeSteppedOn(Entity actor) { }

    public void actPlayerLos() {}

    // True iff any "True"
    public Boolean wantsMoverLos() { return null; }
    public void handleMoverLos(List<ProcMover> movers) {}

    // True iff any "True"
    public Boolean isObstructive() { return null; }
    public Boolean isObstructiveToManipulators() { return null; }
    public Boolean isObstructiveToVision() { return null; }

    // False iff any "False"
    public Boolean pathfindable(Entity actor) { return null; }

    public Boolean preBeOpened(Entity actor) { return null; }
    public void postBeOpened(Entity actor) { }
    public Boolean preBeClosed(Entity actor) { return null; }
    public void postBeClosed(Entity actor) { }

    public Boolean preBeLocked(Entity actor) { return null; }
    public void postBeLocked(Entity actor) { }
    public Boolean preBeUnlocked(Entity actor) { return null; }
    public void postBeUnlocked(Entity actor) { }

    public Boolean preBeHit(Entity actor, Entity tool) { return null; }
    public void postBeHit(Entity actor, Entity tool) { }
    public Boolean preDoHit(Entity target, Entity tool) { return null; }
    public void postDoHit(Entity target, Entity tool) { }

    public Boolean preBeShot(Entity actor, Entity tool) { return null; }
    public void postBeShot(Entity actor, Entity tool) { }
    public Boolean preDoShoot(Entity target, Entity tool) { return null; }
    public void postDoShoot(Entity target, Entity tool) { }

    public void postBeMissed(Entity actor, Entity tool) { }
    public void postDoMiss(Entity target, Entity tool) { }

    public void postBeKilled(Entity actor, Entity tool) { }
    public void postDoKill(Entity target, Entity tool) { }

    public int provideArmorClass() { return 0; }
    public int provideArmorThickness() { return 0; }

    public void beDestroyed() {}

    public Proc clone(Entity other) { return null; }
}
