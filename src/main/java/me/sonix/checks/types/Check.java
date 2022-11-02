package me.sonix.checks.types;

import me.sonix.checks.enums.CheckType;
import me.sonix.managers.profile.Profile;
import me.sonix.processors.Packet;

/*
 * Abstract class for Checks
 */
public abstract class Check extends AbstractCheck {

    public Check(Profile profile, CheckType check, String type, String description) {
        super(profile, check, type, description);
    }

    public Check(Profile profile, CheckType check, String description) {
        super(profile, check, "", description);
    }

    public abstract void handle(Packet packet);
}