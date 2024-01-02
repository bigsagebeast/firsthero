package com.bigsagebeast.hero.roguelike.world.proc.monster;

import com.bigsagebeast.hero.roguelike.world.proc.Proc;

public class ProcShooter extends Proc {
    public String projectile;
    public ProcShooter() {}

    public ProcShooter(String projectile) {
        this.projectile = projectile;
    }
    @Override
    public String provideProjectile() {
        return projectile;
    }
}
