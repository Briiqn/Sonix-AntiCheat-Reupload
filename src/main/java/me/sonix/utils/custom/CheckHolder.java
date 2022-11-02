package me.sonix.utils.custom;

import me.sonix.checks.annotations.Testing;
import me.sonix.checks.impl.aim.AimA;
import me.sonix.checks.impl.aim.AimB;
import me.sonix.checks.impl.autoclicker.AutoclickerA;
import me.sonix.checks.impl.badpackets.BadpacketsA;
import me.sonix.checks.impl.badpackets.BadpacketsB;
import me.sonix.checks.impl.badpackets.BadpacketsC;
import me.sonix.checks.impl.fly.FlyA;
import me.sonix.checks.impl.fly.FlyB;
import me.sonix.checks.impl.fly.FlyC;
import me.sonix.checks.impl.fly.FlyD;
import me.sonix.checks.impl.ground.GroundA;
import me.sonix.checks.impl.ground.GroundB;
import me.sonix.checks.impl.inventory.InventoryA;
import me.sonix.checks.impl.killaura.KillauraA;
import me.sonix.checks.impl.killaura.KillauraB;
import me.sonix.checks.impl.killaura.KillauraC;
import me.sonix.checks.impl.movement.*;
import me.sonix.checks.impl.others.FastPlaceA;
import me.sonix.checks.impl.others.NoSlowdown;
import me.sonix.checks.impl.others.ReachA;
import me.sonix.checks.impl.others.TimerA;
import me.sonix.checks.types.Check;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

import java.util.Arrays;

public class CheckHolder {

    private final Profile profile;
    private Check[] checks;
    private int checksSize;
    private boolean testing; //Used for testing new checks

    public CheckHolder(Profile profile) {
        this.profile = profile;
    }

    public void runChecks(Packet packet) {
        /*
        Fastest way to loop through many objects, If you think this is stupid
        Then benchmark the long term perfomance yourself with many profilers and java articles.
         */
        for (int i = 0; i < this.checksSize; i++) this.checks[i].handle(packet);
    }

    public void registerAll() {

        /*
         * Check initialization
         */
        addChecks(
                new SpeedA(this.profile),
                new SpeedB(this.profile),
                new SpeedC(this.profile),
                new SpeedD(this.profile),
                new SpeedE(this.profile),
                new KillauraA(this.profile),
                new KillauraB(this.profile),
                new KillauraC(this.profile),
                new FlyA(this.profile),
                new FlyB(this.profile),
                new FlyD(this.profile),
                new GroundA(this.profile),
                new GroundB(this.profile),
                new InventoryA(this.profile),
                new FlyC(this.profile),
                new FastPlaceA(this.profile),
                new NoSlowdown(this.profile),
                new ReachA(this.profile),
                new TimerA(this.profile),
                new AimA(this.profile),
                new AimB(this.profile),
                new StrafeA(this.profile),
                new AutoclickerA(this.profile),
                new BadpacketsA(this.profile),
                new BadpacketsB(this.profile),
                new BadpacketsC(this.profile)

        );

        /*
        Remove checks if a testing check is present.
         */
        testing:
        {

            /*
            Testing check not present, break.
             */
            if (!this.testing) break testing;

            /*
            Remove the rest of the checks since a testing check is present.
             */
            this.checks = Arrays.stream(this.checks)
                    .filter(check -> check.getClass().isAnnotationPresent(Testing.class))
                    .toArray(Check[]::new);

            /*
            Update the size since we're only going to be running one check.
             */
            this.checksSize = 1;
        }
    }

    private void addChecks(Check... checks) {

        /*
        Create a new check array to account for reloads.
         */
        this.checks = new Check[0];

        /*
        Reset the check size to account for reloads
         */
        this.checksSize = 0;

        /*
        Loop through the input checks
         */
        for (Check check : checks) {

            /*
            Check if this is being used by a GUI, where we put null as the profile
            Or a check with the @Testing annotation is present or disabled.
             */
            if (this.profile != null && (!check.isEnabled() || isTesting(check))) continue;

            /*
            Copy the original array and increment the size just like an ArrayList.
             */
            this.checks = Arrays.copyOf(this.checks, this.checksSize + 1);

            /*
            Update the check.
             */
            this.checks[this.checksSize] = check;

            /*
            Update the check size variable for improved looping perfomance
             */
            this.checksSize++;
        }
    }

    /**
     * If a check with the testing annotation is present, It'll set the testing boolean to true, load it and then
     * Prevent any other checks from registering.
     */
    private boolean isTesting(Check check) {

        if (this.testing) return true;

        /*
        Update the variable and return false in order to register this check
        But not the next ones.
         */
        if (check.getClass().isAnnotationPresent(Testing.class)) this.testing = true;

        return false;
    }

    public Check[] getChecks() {
        return checks;
    }
}