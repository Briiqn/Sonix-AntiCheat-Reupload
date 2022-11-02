package me.sonix.enums;

/**
 * A permissions enumerations class in order to cache our permissions and easily grab them
 */
public enum Permissions {
    ADMIN("sonix.admin"),
    BYPASS("sonix.bypass"),
    COMMAND_ALERTS("sonix.commands.alerts");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}