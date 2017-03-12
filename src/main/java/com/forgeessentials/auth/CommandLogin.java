package com.forgeessentials.auth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandLogin extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "login";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            if (!ModuleAuth.isEnabled())
            {
                ChatOutputHandler.chatWarning(sender, "Le service d'authentification a \u00E9t\u00E9 d\u00E9sactiv\u00E9 par l'administrateur.");
                return;
            }

            if (ModuleAuth.isRegistered(sender.getPersistentID()))
            {
                if (ModuleAuth.isAuthenticated(sender))
                {
                    ChatOutputHandler.chatNotification(sender, "Vous \u00EAtes connect\u00E9.");
                }
                else
                {
                    ChatOutputHandler.chatNotification(sender, "Vous \u00EAtes inscrit mais vous n'\u00EAtes pas connect\u00E9.");
                }
            }
            else
            {
                ChatOutputHandler.chatWarning(sender, "Vous n'\u00EAtes pas inscrit.");
            }

            throw new TranslatedCommandException("command.auth.usage");
        }

        boolean hasAdmin = PermissionManager.checkPermission(sender, getPermissionNode() + ".admin");

        // one arg? must be password.
        if (args.length == 1)
        {
            if (!ModuleAuth.isRegistered(sender.getPersistentID()))
                throw new TranslatedCommandException("Le joueur %s n'est pas inscrit !", sender.getPersistentID());

            if (PasswordManager.checkPassword(sender.getPersistentID(), args[0]))
            {
                // login worked
                ModuleAuth.authenticate(sender.getPersistentID());
                ChatOutputHandler.chatConfirmation(sender, "Connexion effectu\u00E9e !");
                APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success(sender, Source.COMMAND));
            }
            else
            {
                APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Failure(sender));
                throw new TranslatedCommandException("Mauvais mot de passe !");
            }

            return;
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.auth";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String s = "Syntaxe invalide !";
        if (sender instanceof EntityPlayer)
        {
            s = s + " G\u00E8re votre profil.";
        }
        else
        {
            s = s + " Contr\u00F4le le module d'authentification.";
        }
        return s;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

}
