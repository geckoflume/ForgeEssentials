package com.forgeessentials.api.permissions;

public final class FEPermissions
{

    public static final String MSG_NO_COMMAND_PERM = "Vous n'avez pas la permission d'utiliser cette commande !";
    public static final String MSG_NO_PERM = "Vous n'avez pas la permission pour faire cela !";

    public static final String MSG_NO_PLAYER_COMMAND = "Cette commande ne peut pas \u00EAtre utilis\u00E9e par un joueur";
    public static final String MSG_NO_CONSOLE_COMMAND = "Cette commande ne peut pas \u00EAtre utilis\u00E9e depuis la console";
    public static final String MSG_NO_BLOCK_COMMAND = "Cette commande ne peut pas \u00EAtre utilis\u00E9e par les blocs de commande";
    public static final String MSG_UNKNOWN_SUBCOMMAND = "Sous-commande %s inconnue";

    public static final String MSG_NOT_ENOUGH_ARGUMENTS = "Pas assez d'arguments !";
    public static final String MSG_INVALID_SYNTAX = "Syntaxe invalide !";
    public static final String MSG_INVALID_ARGUMENT = "L'argument %s est invalide !";

    // ------------------------------------------------
    // -- Internal permissions
    // ------------------------------------------------

    public static final String FE_INTERNAL = "fe.internal";
    public static final String DESCRIPTION_PROPERTY = ".$desc";

    public static final String ZONE = FE_INTERNAL + ".zone";
    public static final String ZONE_ENTRY_MESSAGE = ZONE + ".entry";
    public static final String ZONE_EXIT_MESSAGE = ZONE + ".exit";
    public static final String ZONE_HIDDEN = ZONE + ".hidden";

    public static final String PREFIX = FE_INTERNAL + ".prefix";
    public static final String SUFFIX = FE_INTERNAL + ".suffix";
    public static final String SPAWN_LOC = FE_INTERNAL + ".spawn.location";
    public static final String SPAWN_BED = FE_INTERNAL + ".spawn.bed";

    public static final String GROUP = FE_INTERNAL + ".group";
    public static final String GROUP_NAME = GROUP + ".name";
    public static final String GROUP_PRIORITY = GROUP + ".priority";
    public static final String GROUP_INCLUDES = GROUP + ".includes";
    public static final String GROUP_PARENTS = GROUP + ".parents";
    public static final String GROUP_PROMOTION = GROUP + ".promotion";

    public static final int GROUP_PRIORITY_DEFAULT = 20;

    public static final String PLAYER = FE_INTERNAL + ".player";
    public static final String PLAYER_UUID = PLAYER + ".uuid";
    public static final String PLAYER_NAME = PLAYER + ".name";
    public static final String PLAYER_GROUPS = PLAYER + ".groups";
    public static final String PLAYER_KNOWN = PLAYER + ".known";

}