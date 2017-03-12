package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandKill extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "fekill";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "kill" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/kill <joueur> Commet un suicide ou tue d'autres joueurs";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".kill";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length >= 1 && PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                ChatOutputHandler.chatError(player, Translator.translate("Vous \u00EAtes mort. Vous le m\u00E9ritiez probablement."));
            }
            else
                throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
        }
        else
        {
            sender.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
            ChatOutputHandler.chatError(sender, Translator.translate("You were killed. You probably deserved it."));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                ChatOutputHandler.chatError(player, Translator.translate("Vous \u00EAtes mort. Vous le m\u00E9ritiez probablement."));
            }
            else
                throw new TranslatedCommandException("Le joueur %s n'existe pas ou n'est pas en ligne.", args[0]);
        }
        else
            throw new TranslatedCommandException(getCommandUsage(sender));
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
