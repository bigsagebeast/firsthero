package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public class Proc {

    @JsonIgnore
    public Entity entity;

    public long nextAction = -1;
    public boolean active;

    public Proc(Entity e) {
        entity = e;
        active = true;
    }

    public void setDelay(long delay) {
        nextAction = Game.time + delay;
    }

    public boolean hasAction() { return false; }

    public boolean isMover() {
        return false;
    }

    public void act() { }

    // activates every 1000
    public void turnPassed() { }

    // return true if pickup is allowed, false if it's aborted, null if no opinion
    public Boolean preBePickedUp(Entity actor) { return null; }
    public void postBePickedUp(Entity actor) {}

    public Boolean preDoPickup(Entity target) { return null; }
    public void postDoPickup(Entity target) {}

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

    public void beDestroyed() {}
}
