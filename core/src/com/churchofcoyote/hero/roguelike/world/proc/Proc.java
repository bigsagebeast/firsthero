package com.churchofcoyote.hero.roguelike.world.proc;

import com.churchofcoyote.hero.roguelike.game.Game;
import com.churchofcoyote.hero.roguelike.world.BodyPart;
import com.churchofcoyote.hero.roguelike.world.Element;
import com.churchofcoyote.hero.roguelike.world.Entity;
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

    public long nextAction = -1;
    public boolean active;

    // for deserialization
    protected Proc() {
        active = true;
    }

    /*
    // Custom logic before serialization
    @JsonGetter("entityId")
    public int getEntityIdForSerialization() {
        entityId = entity.entityId;
        return entityId;
    }
    */

    public void clearDelay() {
        nextAction = Game.time;
    }

    public void setDelay(Entity entity, long delay) {
        if (delay == 0 && entity != Game.getPlayerEntity()) {
            //throw new RuntimeException("Set delay of 0 for " + this.getClass() + " on " + entity.name);
        }
        nextAction = Game.time + (delay * 100 / entity.statblock.speed);
    }

    public void initialize() {
        active = true;
    }

    public boolean hasAction() { return false; }

    public boolean isMover() {
        return false;
    }

    public void act(Entity entity) { }

    // activates after something on the entity has taken an action
    public void onAction(Entity entity) { }

    // activates every 1000
    public void turnPassed(Entity entity) { }

    public TextBlock getNameBlock(Entity entity) { return null; }

    // return true if pickup is allowed, false if it's aborted, null if no opinion
    public Boolean preBePickedUp(Entity entity, Entity actor) { return null; }
    public void postBePickedUp(Entity entity, Entity actor) {}
    public Boolean preBeDropped(Entity entity, Entity actor) { return null; }
    public void postBeDropped(Entity entity, Entity actor) {}

    public Boolean preDoPickup(Entity entity, Entity target) { return null; }
    public void postDoPickup(Entity entity, Entity target) {}
    public Boolean preDoDrop(Entity entity, Entity target) { return null; }
    public void postDoDrop(Entity entity, Entity target) {}

    public Boolean preBeEquipped(Entity entity, BodyPart bp, Entity actor) { return null; }
    public void postBeEquipped(Entity entity, BodyPart bp, Entity actor) {}
    public Boolean preBeUnequipped(Entity entity, BodyPart bp, Entity actor) { return null; }
    public void postBeUnequipped(Entity entity, BodyPart bp, Entity actor) {}

    public Boolean preDoEquip(Entity entity, BodyPart bp, Entity target) { return null; }
    public void postDoEquip(Entity entity, BodyPart bp, Entity target) {}
    public Boolean preDoUnequip(Entity entity, BodyPart bp, Entity target) { return null; }
    public void postDoUnequip(Entity entity, BodyPart bp, Entity target) {}

    public void postBeSteppedOn(Entity entity, Entity actor) { }

    public void actPlayerLos(Entity entity) {}

    // True iff any "True"
    public Boolean wantsMoverLos() { return null; }
    public void handleMoverLos(Entity entity, List<Entity> movers) {}

    // True iff any "True"
    public Boolean isObstructive() { return null; }
    public Boolean isObstructiveToManipulators() { return null; }
    public Boolean isObstructiveToVision() { return null; }

    // False iff any "False"
    public Boolean pathfindable(Entity actor) { return null; }

    public Boolean preBeOpened(Entity entity, Entity actor) { return null; }
    public void postBeOpened(Entity entity, Entity actor) { }

    public Boolean preBeLocked(Entity entity, Entity actor) { return null; }
    public void postBeLocked(Entity entity, Entity actor) { }
    public Boolean preBeUnlocked(Entity entity, Entity actor) { return null; }
    public void postBeUnlocked(Entity entity, Entity actor) { }

    public Boolean preBeHit(Entity entity, Entity actor, Entity tool) { return null; }
    public void postBeHit(Entity entity, Entity actor, Entity tool) { }
    public Boolean preDoHit(Entity entity, Entity target, Entity tool) { return null; }
    public void postDoHit(Entity entity, Entity target, Entity tool) { }

    public Boolean preBeShot(Entity entity, Entity actor, Entity tool) { return null; }
    public void postBeShot(Entity entity, Entity actor, Entity tool) { }
    public Boolean preDoShoot(Entity entity, Entity target, Entity tool) { return null; }
    public void postDoShoot(Entity entity, Entity target, Entity tool) { }

    public String provideProjectile() { return null; }

    public void postBeMissed(Entity entity, Entity actor, Entity tool) { }
    public void postDoMiss(Entity entity, Entity target, Entity tool) { }

    public void postBeKilled(Entity entity, Entity actor, Entity tool) { }
    public void postDoKill(Entity entity, Entity target, Entity tool) { }

    public int provideArmorClass() { return 0; }
    public int provideArmorThickness() { return 0; }

    public Boolean targetForQuaff(Entity entity) { return null; }
    public Boolean preBeQuaffed(Entity entity, Entity actor) { return null; }
    public void postBeQuaffed(Entity entity, Entity actor) { }
    public Boolean preDoQuaff(Entity entity, Entity target) { return null; }
    public void postDoQuaff(Entity entity, Entity target) { }

    public Boolean targetForRead(Entity entity) { return null; }
    public Boolean preBeRead(Entity entity, Entity actor) { return null; }
    public void postBeRead(Entity entity, Entity actor) { }
    public Boolean preDoRead(Entity entity, Entity target) { return null; }
    public void postDoRead(Entity entity, Entity target) { }

    public Boolean isEdible(Entity entity, Entity actor) { return null; }
    public Boolean preBeEaten(Entity entity, Entity actor) { return null; }
    public void postBeEaten(Entity entity, Entity actor) { }

    // null actor = can you even consider it?  For precalculating a list
    public Boolean canPrayAt(Entity entity, Entity actor) { return null; }
    public void prayAt(Entity entity, Entity actor) { }

    // null actor and target = can you even consider it?  For precalculating a list
    public Boolean canOfferAt(Entity entity, Entity actor, Entity target) { return null; }
    public void offerAt(Entity entity, Entity actor, Entity target) { }

    // Is this a valid target to draw an element from?  If null, ignore it.
    public Element providesElement(Entity entity) { return null; }
    // Attempt to draw up to 'max' charges.  Return the actual charges that were drawn.
    public int drawElement(Entity entity, Entity actor, int requested) { return 0; }

    public void beDestroyed() {}

    public Float getJitter(Entity entity) { return null; }

    public Proc clone() { return null; }
}
