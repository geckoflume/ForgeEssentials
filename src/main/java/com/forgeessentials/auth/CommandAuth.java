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

public class CommandAuth extends ForgeEssentialsCommandBase
{
    private static String[] playerCommands = new String[] { "help", "login", "register", "changepass", "kick", "setpass", "unregister" };
    private static String[] serverCommands = new String[] { "help", "kick", "setpass", "unregister" };

    @Override
    public String getCommandName()
    {
        return "auth";
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

        // one arg? must be help.
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                ChatOutputHandler.chatConfirmation(sender, " - /auth register <motdepasse>");
                ChatOutputHandler.chatConfirmation(sender, " - /auth login <motdepasse>");
                ChatOutputHandler.chatConfirmation(sender, " - /auth changepass <ancienmotdepasse> <nouveaumotdepasse>  - change votre mot de passe");

                if (!hasAdmin)
                {
                    return;
                }

                ChatOutputHandler.chatConfirmation(sender, " - /auth kick <joueur>  - force le joueur \u00E0 se reconnecter");
                ChatOutputHandler.chatConfirmation(sender, " - /auth setpass <joueur> <motdepasse>  - d\u00E9finit le mot de passe du joueur");
                ChatOutputHandler.chatConfirmation(sender, " - /auth unregister <joueur>  - force le joueur \u00E0 se r\u00E9inscrire");
                return;
            }
            else
            {
                throw new TranslatedCommandException("Syntaxe invalide !");
            }
        }

        // 2 args? seconds needs to be the player.
        if (args.length == 2)
        {
            // parse login
            if (args[0].equalsIgnoreCase("login"))
            {
                if (!ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Le joueur %s n'est pas inscrit !", sender.getPersistentID());

                if (PasswordManager.checkPassword(sender.getPersistentID(), args[1]))
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
            // parse register
            else if (args[0].equalsIgnoreCase("register"))
            {
                if (ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Le joueur %s est d\u00E9j\u00E0 inscrit !", sender.getPersistentID());

                if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineRegistration)
                    throw new TranslatedCommandException("Les inscriptions ont \u00E9t\u00E9 d\u00E9sactiv\u00E9es.");

                PasswordManager.setPassword(sender.getPersistentID(), args[1]);
                ChatOutputHandler.chatConfirmation(sender, "Inscription effectu\u00E9e !");
                return;
            }

            // stop if unlogged.
            if (!ModuleAuth.isAuthenticated(sender))
                throw new TranslatedCommandException("Veuillez vous connecter avec /auth login motdepasse");

            boolean isLogged = true;

            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                ChatOutputHandler.chatWarning(sender, "Un joueur avec ce nom n'est pas pr\u00E9sent sur le serveur. Action r\u00E9alis\u00E9e tout de m\u00EAme.");
                isLogged = false;
            }

            // parse ./auth kick
            if (args[0].equalsIgnoreCase("kick"))
            {
                if (!hasAdmin)
                {
                    throw new PermissionDeniedException();
                }
                else if (!isLogged)
                {
                    throw new PlayerNotFoundException();
                }
                else
                {
                    ModuleAuth.deauthenticate(player.getPersistentID());
                    ChatOutputHandler.chatConfirmation(sender,
                            Translator.format("Le joueur %s a \u00E9t\u00E9 expuls\u00E9 du service d'authentification.", player.getCommandSenderName()));
                    ChatOutputHandler.chatWarning(player, "Vous avez \u00E9t\u00E9 expuls\u00E9 du service d'authentification. Veuillez vous reconnecter.");
                    return;
                }
            }
            // parse ./auth setpass
            else if (args[0].equalsIgnoreCase("setpass"))
            {
                if (!hasAdmin)
                {
                    throw new PermissionDeniedException();
                }

                throw new TranslatedCommandException("/auth setpass <joueur> <motdepasse>");
            }

            // parse ./auth unregister
            else if (args[0].equalsIgnoreCase("unregister"))
            {
                if (!hasAdmin)
                    throw new PermissionDeniedException();

                if (!ModuleAuth.isRegistered(player.getPersistentID()))
                    throw new TranslatedCommandException("Le joueur %s n'est pas inscrit !", player.getCommandSenderName());

                PasswordManager.setPassword(player.getPersistentID(), null);
                ChatOutputHandler.chatConfirmation(sender,
                        Translator.format("Le joueur %s a \u00E9t\u00E9 supprim\u00E9 du service d'authentification", player.getCommandSenderName()));
                return;
            }

            // ERROR! :D
            else
            {
                throw new TranslatedCommandException("Syntaxe invalide !");
            }
        }
        // 3 args? must be a comtmand - player - pass
        else if (args.length == 3)
        {
            if (!ModuleAuth.isAuthenticated(sender))
                throw new TranslatedCommandException("Veuillez vous connecter avec /auth login motdepasse");

            // parse changePass
            if (args[0].equalsIgnoreCase("changepass"))
            {
                if (args[1].equals(args[2]))
                {
                    ChatOutputHandler.chatConfirmation(sender, "Vous ne pouvez pas utiliser ce nouveau mot de passe : il s'agit du m\u00EAme que vous aviez pr\u00E9c\u00E9demment.");
                    return;
                }

                if (!ModuleAuth.isRegistered(sender.getPersistentID()))
                    throw new TranslatedCommandException("Le joueur %s n'est pas inscrit !", sender.getCommandSenderName());

                if (!PasswordManager.checkPassword(sender.getPersistentID(), args[1]))
                {
                    ChatOutputHandler.chatConfirmation(sender, "Impossible de changer votre mot de passe : votre ancien mot de passe est erron\u00E9.");
                    return;
                }

                PasswordManager.setPassword(sender.getPersistentID(), args[2]);
                ChatOutputHandler.chatConfirmation(sender, "Mot de passe chang\u00E9 !");
                return;

            }

            // check if the player is logged.
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
            if (player == null)
            {
                ChatOutputHandler.chatWarning(sender, "Un joueur avec ce nom n'est pas pr\u00E9sent sur le serveur. Action r\u00E9alis\u00E9e tout de m\u00EAme.");
            }

            // pasre setPass
            if (args[0].equalsIgnoreCase("setPass"))
            {
                if (!hasAdmin)
                    throw new PermissionDeniedException();
                PasswordManager.setPassword(player.getPersistentID(), args[2]);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Mot de passe d\u00E9fini pour %s", player.getCommandSenderName()));
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            throw new TranslatedCommandException("Syntaxe invalide !");
        }

        // one arg? must be help.
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                ChatOutputHandler.chatNotification(sender, " - /auth kick <joueur>  - force le joueur \u00E0 se reconnecter");
                ChatOutputHandler.chatNotification(sender, " - /auth setpass <joueur> <motdepasse>  - d\u00E9finit le mot de passe du joueur");
                ChatOutputHandler.chatNotification(sender, " - /auth unregister <joueur>  - force le joueur \u00E0 se r\u00E9inscrire");
                return;
            }
            else
            {
                throw new TranslatedCommandException("Syntaxe invalide !");
            }
        }

        boolean isLogged = true;

        // check if the player is logged.
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
        if (player == null)
        {
            ChatOutputHandler.chatWarning(sender, "Un joueur avec ce nom n'est pas pr\u00E9sent sur le serveur. Action r\u00E9alis\u00E9e tout de m\u00EAme.");
            isLogged = false;
        }

        // 2 args? seconds needs to be the player.
        if (args.length == 2)
        {
            // parse ./auth kick
            if (args[0].equalsIgnoreCase("kick"))
            {
                if (!isLogged)
                {
                    throw new TranslatedCommandException("/auth kick <player");
                }
                else
                {
                    ModuleAuth.deauthenticate(player.getPersistentID());
                    ChatOutputHandler.chatConfirmation(sender,
                            Translator.format("Le joueur %s a \u00E9t\u00E9 expuls\u00E9 du service d'authentification.", player.getCommandSenderName()));
                    ChatOutputHandler.chatWarning(player, "Vous avez \u00E9t\u00E9 expuls\u00E9 du service d'authentification. Veuillez vous reconnecter.");
                    return;
                }
            }
            // parse ./auth setpass
            else if (args[0].equalsIgnoreCase("setPass"))
            {
                throw new TranslatedCommandException("/auth setpass <joueur> <motdepasse>");
            }
            else if (args[0].equalsIgnoreCase("unregister"))
            {
                if (!ModuleAuth.isRegistered(player.getPersistentID()))
                    throw new TranslatedCommandException("message.auth.error.notregisterred", args[1]);
                PasswordManager.setPassword(player.getPersistentID(), null);
                return;
            }

            // ERROR! :D
            else
            {
                throw new TranslatedCommandException("command.auth.usage");
            }
        }
        // 3 args? must be a command - player - pass
        else if (args.length == 3)
        {
            // pasre setPass
            if (args[0].equalsIgnoreCase("setPass"))
            {
                PasswordManager.setPassword(player.getPersistentID(), args[2]);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Mot de passe d\u00E9ini pour %s", player.getCommandSenderName()));
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        ArrayList<String> list = new ArrayList<String>();
        switch (args.length)
        {
        case 1:
            if (sender instanceof EntityPlayer)
            {
                list.addAll(getListOfStringsMatchingLastWord(args, playerCommands));
            }
            else
            {
                list.addAll(getListOfStringsMatchingLastWord(args, serverCommands));
            }
            break;
        case 2:
            if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("setpass") || args[0].equalsIgnoreCase("unregister"))
            {
                list.addAll(getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames()));
            }
        }
        return list;
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
