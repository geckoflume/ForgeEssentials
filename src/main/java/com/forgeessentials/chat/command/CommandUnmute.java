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

public class CommandUnmute extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "unmute";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/unmute <joueur>: Rend la parole au joueur sp\u00E9cifi\u00E9.";
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

            PlayerUtil.getPersistedTag(receiver, false).removeTag("mute");
            ChatOutputHandler.chatError(sender, Translator.format("Vous avez rendu la parole \u00E0 %s.", args[0]));
            ChatOutputHandler.chatError(receiver, Translator.format("%s vous a rendu la parole.", sender.getCommandSenderName()));
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
