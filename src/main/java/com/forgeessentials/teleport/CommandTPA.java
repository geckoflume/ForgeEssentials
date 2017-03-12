package com.forgeessentials.teleport;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;

public class CommandTPA extends ParserCommandBase
{

    public static final String PERM_HERE = TeleportModule.PERM_TPA + ".here";
    public static final String PERM_LOCATION = TeleportModule.PERM_TPA + ".loc";

    @Override
    public String getCommandName()
    {
        return "tpa";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/tpa [joueur] <joueur|<x> <y> <z>|accept|decline> Requ\u00EAte pour t\u00E9l\u00E9porter vous ou un autre joueur";
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
        return TeleportModule.PERM_TPA;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_HERE, PermissionLevel.TRUE, "Allow teleporting other players to your own location (inversed TPA)");
        APIRegistry.perms.registerPermission(PERM_LOCATION, PermissionLevel.OP, "Allow teleporting other players to any location");
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/tpa <joueur>: Requ\u00EAte pour se t\u00E9l\u00E9porter \u00E0 un autre joueur");
            arguments.confirm("/tpa <joueur> <here|x y z>: Proposer \u00E0 un autre joueur de se t\u00E9l\u00E9porter");
            return;
        }

        final UserIdent player = arguments.parsePlayer(true, true);
        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            arguments.confirm("Requ\u00EAte envoy\u00E9e \u00E0 %s", player.getUsernameOrUuid());
            QuestionerCallback callback = new QuestionerCallback() {
                @Override
                public void respond(Boolean response)
                {
                    if (response == null)
                        arguments.error("La demande de t\u00E9l\u00E9portation a expir\u00E9.");
                    else if (response == false)
                        arguments.error("Demande de t\u00E9l\u00E9portation refus\u00E9e.");
                    else
                        TeleportHelper.teleport(arguments.senderPlayer, new WarpPoint(player.getPlayer()));
                }
            };
            Questioner.addChecked(player.getPlayer(), Translator.format("Autoriser la t\u00E9l\u00E9portation de %s \u00E0 votre emplacement ?", arguments.sender.getCommandSenderName()),
                    callback, 20);
            return;
        }

        arguments.tabComplete("here");

        final WarpPoint point;
        final String locationName;
        if (arguments.peek().equalsIgnoreCase("here"))
        {
            arguments.checkPermission(PERM_HERE);
            point = new WarpPoint(arguments.senderPlayer);
            locationName = arguments.sender.getCommandSenderName();
            arguments.remove();
        }
        else
        {
            arguments.checkPermission(PERM_LOCATION);
            point = new WarpPoint((WorldServer) arguments.senderPlayer.worldObj, //
                    arguments.parseDouble(), arguments.parseDouble(), arguments.parseDouble(), //
                    player.getPlayer().rotationPitch, player.getPlayer().rotationYaw);
            locationName = point.toReadableString();
        }

        if (arguments.isTabCompletion)
            return;
        Questioner.addChecked(player.getPlayer(), Translator.format("Voulez-vous \u00EAtre t\u00E9l\u00E9port\u00E9 \u00E0 %s?", locationName), new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                    arguments.error("La demande de t\u00E9l\u00E9portation a expir\u00E9.");
                else if (response == false)
                    arguments.error("Demande de t\u00E9l\u00E9portation refus\u00E9e.");
                else
                    TeleportHelper.teleport(player.getPlayerMP(), point);
            }
        }, 20);
    }

}
