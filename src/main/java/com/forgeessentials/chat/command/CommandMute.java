package com.forgeessentials.chat.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMute extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "mute";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mute <joueur>: Rend le joueur muet.";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.mute";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (receiver == null)
                throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);

            PlayerUtil.getPersistedTag(receiver, true).setBoolean("mute", true);
            ChatOutputHandler.chatError(sender, Translator.format("Vous avez rendu muet %s.", args[0]));
            ChatOutputHandler.chatError(receiver, Translator.format("Vous avez \u00E9t\u00E9 r\u00E9duit au silence par %s.", sender.getCommandSenderName()));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

}
