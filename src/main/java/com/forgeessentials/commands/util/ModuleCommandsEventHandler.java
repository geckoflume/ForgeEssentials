package com.forgeessentials.commands.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.player.CommandAFK;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAFKEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ModuleCommandsEventHandler extends ServerEventHandler implements Runnable
{

    private static final Set<UserIdent> afkPlayers = new HashSet<>();

    public ModuleCommandsEventHandler()
    {
        TaskRegistry.scheduleRepeated(this, 1000 * 2);
    }

    @Override
    public void run()
    {
        for (PlayerInfo pi : PlayerInfo.getAll())
        {
            if (!pi.ident.hasPlayer())
                continue;
            Integer autoTime = ServerUtil.tryParseInt(pi.ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME));
            if (autoTime != null && autoTime > 10)
                if (pi.getInactiveTime() / 1000 > autoTime)
                    setAfk(pi.ident);
        }
    }

    /* ------------------------------------------------------------ */

    public static boolean isAfk(UserIdent uuid)
    {
        return afkPlayers.contains(uuid);
    }

    public void setAfk(UserIdent player)
    {
        if (isAfk(player))
            return;

        if (player.checkPermission(CommandAFK.PERM_AUTOKICK))
        {
            player.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.translate("Vous avez \u00E9t\u00E9 expuls\u00E9 pour inactivit\u00E9"));
            return;
        }

        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), true);
        APIRegistry.getFEEventBus().post(event);

        player.getPlayerMP().capabilities.disableDamage = true;
        if (player.checkPermission(CommandAFK.PERM_ANNOUNCE))
            ChatOutputHandler.broadcast(ChatOutputHandler.confirmation(Translator.format("%s est d\u00E9sormais AFK", player.getUsernameOrUuid())));
        else
            ChatOutputHandler.chatConfirmation(player.getPlayer(), Translator.translate("Vous \u00EAtes d\u00E9sormais absent/AFK"));

        afkPlayers.add(player);
    }

    public void clearAfk(UserIdent player)
    {
        if (!isAfk(player))
            return;
        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), false);
        APIRegistry.getFEEventBus().post(event);

        switch (player.getPlayerMP().theItemInWorldManager.getGameType())
        {
        case NOT_SET:
        case SURVIVAL:
        case ADVENTURE:
            player.getPlayerMP().capabilities.disableDamage = false;
            break;
        default:
            break;
        }

        if (player.checkPermission(CommandAFK.PERM_ANNOUNCE))
            ChatOutputHandler.broadcast(ChatOutputHandler.confirmation(Translator.format("%s n'est plus AFK", player.getUsernameOrUuid())));
        else
            ChatOutputHandler.chatConfirmation(player.getPlayer(), Translator.translate("Vous n'\u00EAtes plus absent/AFK"));

        afkPlayers.remove(player);
    }

    public void playerActive(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        pi.setActive();
        clearAfk(pi.ident);
    }

    public static void checkAfkMessage(ICommandSender target, IChatComponent message)
    {
        if (!(target instanceof EntityPlayerMP))
            return;
        UserIdent targetIdent = UserIdent.get((EntityPlayerMP) target);
        if (target instanceof EntityPlayerMP && isAfk(targetIdent))
        {
            ChatOutputHandler.notification(Translator.format("%s est actuellement AFK", targetIdent.getUsernameOrUuid()));
            return;
        }
        String msg = message.getUnformattedText().toLowerCase();
        for (UserIdent player : afkPlayers)
            if (msg.contains(player.getUsernameOrUuid().toLowerCase()))
                ChatOutputHandler.notification(Translator.format("%s est actuellement AFK", player.getUsernameOrUuid()));
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerMoveEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        playerActive((EntityPlayerMP) event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        playerActive((EntityPlayerMP) event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatEvent(ServerChatEvent event)
    {
        playerActive(event.player);
        String msg = event.component.getUnformattedText().toLowerCase();
        for (UserIdent player : afkPlayers)
            if (msg.contains(player.getUsernameOrUuid().toLowerCase()))
                ChatOutputHandler.notification(Translator.format("%s est actuellement AFK", player.getUsernameOrUuid()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void commandEvent(CommandEvent event)
    {
        if (event.command instanceof CommandAFK)
            return;
        if (event.sender instanceof EntityPlayerMP)
            playerActive((EntityPlayerMP) event.sender);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLogin(PlayerLoggedInEvent event)
    {
        afkPlayers.remove(UserIdent.get(event.player));

        PlayerInfo pi = PlayerInfo.get(event.player);
        if (!pi.checkTimeout("tempban"))
        {
            pi.ident.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.format("Vous \u00EAtes toujours banni pour %s",
                    ChatOutputHandler.formatTimeDurationReadable(pi.getRemainingTimeout("tempban") / 1000, true)));
        }
    }

}
