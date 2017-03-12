package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 */

public class CommandKit extends ParserCommandBase implements ConfigurableCommand
{

    public static final String PERM = ModuleCommands.PERM + ".kit";
    public static final String PERM_ADMIN = ModuleCommands.PERM + ".admin";
    public static final String PERM_BYPASS_COOLDOWN = PERM + ".bypasscooldown";

    public static String kitForNewPlayers;

    public static Map<String, Kit> kits = new HashMap<>();

    public CommandKit()
    {
        APIRegistry.getFEEventBus().register(this);
    }

    @Override
    public String getCommandName()
    {
        return "fekit";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "kit" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/kit [nom] [set|del] [cooldown]: Utilise et modifie les kits";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_ADMIN, PermissionLevel.OP);
        APIRegistry.perms.registerPermission(PERM_BYPASS_COOLDOWN, PermissionLevel.OP);
    }

    public List<String> getAvailableKits(CommandParserArgs arguments)
    {
        List<String> availableKits = new ArrayList<>();
        for (Kit kit : kits.values())
            if (arguments.hasPermission(PERM + "." + kit.getName()))
                availableKits.add(kit.getName());
        return availableKits;
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            if (!arguments.isTabCompletion)
                arguments.confirm("Kits disponibles : %s", StringUtils.join(getAvailableKits(arguments), ", "));
            return;
        }

        arguments.tabComplete(getAvailableKits(arguments));
        final String kitName = arguments.remove().toLowerCase();
        Kit kit = kits.get(kitName);

        if (arguments.isEmpty())
        {
            if (kit == null)
                throw new TranslatedCommandException("Le kit %s n'existe pas", kitName);
            if (!arguments.hasPermission(PERM + "." + kit.getName()))
                throw new TranslatedCommandException("Vous n'\u00EAtes pas autoris\u00E9 \u00E0 utiliser ce kit");
            kit.giveKit(arguments.senderPlayer);
            return;
        }

        arguments.checkPermission(PERM_ADMIN);

        arguments.tabComplete("set", "del");
        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "set":
            QuestionerCallback callback = new QuestionerCallback() {
                @Override
                public void respond(Boolean response)
                {
                    if (response == null)
                        arguments.error("Question expir\u00E9e");
                    else if (!response)
                        return;
                    int cooldown = -1;
                    if (!arguments.isEmpty())
                        cooldown = arguments.parseInt();
                    addKit(new Kit(arguments.senderPlayer, kitName, cooldown));
                    if (cooldown < 0)
                        arguments.confirm("Kit %s sauvegard\u00E9 pour un usage unique", kitName);
                    else
                        arguments.confirm("Kit %s sauvegard\u00E9 avec un cooldown de %s", kitName, ChatOutputHandler.formatTimeDurationReadable(cooldown, true));
                }
            };
            if (kit == null)
                callback.respond(true);
            else
                Questioner.addChecked(arguments.sender, Translator.format("\u00E9craser le kit %s?", kitName), callback);
            break;
        case "del":
        case "delete":
            removeKit(kit);
            arguments.confirm("Kit %s supprim\u00E9", kitName);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
    }

    @SubscribeEvent
    public void newPlayerEvent(NoPlayerInfoEvent event)
    {
        Kit kit = kits.get(kitForNewPlayers);
        if (kit != null)
            kit.giveKit(event.entityPlayer);
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        kitForNewPlayers = config.get(category, "kitForNewPlayers", "", "Name of kit to issue to new players. If this is left blank, it will be ignored.")
                .getString();
    }

    @Override
    public void loadData()
    {
        kits = DataManager.getInstance().loadAll(Kit.class);
    }

    public static void removeKit(Kit kit)
    {
        kits.remove(kit.getName());
        DataManager.getInstance().delete(Kit.class, kit.getName());
    }

    public static void addKit(Kit kit)
    {
        kits.put(kit.getName(), kit);
        DataManager.getInstance().save(kit, kit.getName());
    }

}
