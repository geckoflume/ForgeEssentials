package com.forgeessentials.chat.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.moduleLauncher.config.ConfigSaver;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.JsonParseException;

public class CommandTimedMessages extends ParserCommandBase implements ConfigSaver, Runnable
{

    public static final String CATEGORY = ModuleChat.CONFIG_CATEGORY + ".TimedMessage";

    public static final String MESSAGES_HELP = "Chaque ligne est 1 message. \nVous pouvez utiliser les arguments de scripts et les codes de couleurs. "
            + "\nL'utilisation de messages json(tellraw) est aussi support\u00E9e";

    public static final String[] MESSAGES_DEFAULT = new String[] { "Ce serveur ex\u00E9cute le mod de gestion de serveur ForgeEssentials" };

    protected static List<String> messages = new ArrayList<>();

    protected static int interval;

    protected static boolean shuffle;

    protected static boolean enabled;

    protected List<Integer> messageOrder = new ArrayList<>();

    protected int currentIndex;

    public CommandTimedMessages()
    {
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_FILE, this);
    }

    @Override
    public String getCommandName()
    {
        return "timedmessage";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "tm" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/timedmessage: G\u00E8re les messages envoy\u00E9s automatiquement";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.timedmessage";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/tm add <message>: Ajoute un message");
            arguments.confirm("/tm list: Liste les messages");
            arguments.confirm("/tm delete <id>: Supprime un message");
            arguments.confirm("/tm send <id>: Envoie un message");
            arguments.confirm("/tm interval <sec>: D\u00E9finit l'intervalle des messages");
            arguments.confirm("/tm shuffle <true|false>: Active/D\u00E9sactive l'affichage de messages al\u00E9atoirement");
            return;
        }

        arguments.tabComplete("add", "list", "delete", "interval", "random");
        String sumCmd = arguments.remove().toLowerCase();
        switch (sumCmd)
        {
        case "add":
            parseAdd(arguments);
            break;
        case "list":
            parseList(arguments);
            break;
        case "delete":
            parseDelete(arguments);
            break;
        case "send":
            parseSend(arguments);
            break;
        case "interval":
            parseInterval(arguments);
            break;
        case "shuffle":
            parseShuffle(arguments);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, sumCmd);
        }
    }

    public void parseAdd(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        if (arguments.isEmpty())
        {
            arguments.confirm("/timedmessage add <message...>: Ajoute un message pr\u00E9vu");
            return;
        }
        String message = arguments.toString();
        addMessage(message);
        arguments.confirm("Le message suivant a \u00E9t\u00E9 ajout\u00E9 :");
        arguments.sendMessage(formatMessage(message));
        ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_FILE);
    }

    public void parseList(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        arguments.confirm("Liste des messages:");
        for (int i = 0; i < messages.size(); i++)
            arguments.sendMessage(new ChatComponentTranslation(String.format("%d: %s", i, formatMessage(messages.get(i)))));
    }

    public void parseDelete(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        if (arguments.isEmpty())
        {
            arguments.confirm("/timedmessage delete <index>: Supprime un message pr\u00E9vu");
            return;
        }
        int index = arguments.parseInt();
        if (index < 0 || index >= messages.size())
            throw new TranslatedCommandException("Index out of bounds");
        messages.remove(index);
        arguments.confirm("Message supprim\u00E9");
        ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_FILE);
    }

    public void parseSend(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        if (arguments.isEmpty())
        {
            arguments.confirm("/timedmessage send <index>: Envoie un message pr\u00E9vu");
            return;
        }
        int index = arguments.parseInt();
        if (index < 0 || index >= messages.size())
            throw new TranslatedCommandException("Index out of bounds");
        broadcastMessage(index);
    }

    public void parseInterval(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        if (arguments.isEmpty())
        {
            arguments.confirm("/tm interval <sec>: D\u00E9finit l'intervalle des messages (0 = d\u00E9sactiv\u00E9)");
            return;
        }
        setInterval(arguments.parseInt());
        ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_FILE);
    }

    public void parseShuffle(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/tm shuffle <true|false>: Active/D\u00E9sactive l'affichage de messages al\u00E9atoirement");
            return;
        }
        boolean newShuffle = arguments.parseBoolean();
        if (arguments.isTabCompletion)
            return;
        if (newShuffle != shuffle)
        {
            shuffle = newShuffle;
            initMessageOrder();
            ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_FILE);
        }
    }

    @Override
    public void run()
    {
        if (messages.isEmpty() || !enabled)
            return;
        if (currentIndex >= messages.size())
            currentIndex = 0;
        broadcastMessage(messageOrder.get(currentIndex));
        currentIndex++;
    }

    public void broadcastMessage(int index)
    {
        if (index >= 0 && index < messages.size())
            ChatOutputHandler.broadcast(formatMessage(messages.get(index)));
    }

    public void addMessage(String message)
    {
        messages.add(message);
        initMessageOrder();
    }

    public void initMessageOrder()
    {
        messageOrder.clear();
        for (int i = 0; i < messages.size(); i++)
            messageOrder.add(i);
        if (shuffle)
            Collections.shuffle(messageOrder);
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval(int interval)
    {
        if (interval < 0)
            interval = 0;
        if (CommandTimedMessages.interval == interval)
            return;
        if (CommandTimedMessages.interval > 0)
            TaskRegistry.remove(this);
        CommandTimedMessages.interval = interval;
        if (interval > 0)
            TaskRegistry.scheduleRepeated(this, interval * 1000);
    }

    public static IChatComponent formatMessage(String message)
    {
        message = ModuleChat.processChatReplacements(null, message);
        try
        {
            return IChatComponent.Serializer.func_150699_a(message);
        }
        catch (JsonParseException e)
        {
            if (message.contains("{"))
            {
                LoggingHandler.felog.warn("Erreur dans le format du timedmessage : " + ExceptionUtils.getRootCause(e).getMessage());
            }
            return new ChatComponentText(message);
        }
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CATEGORY, "Automated spam");
        setInterval(config.get(CATEGORY, "inverval", 60, "Interval in seconds. 0 to disable").getInt());
        enabled = config.get(CATEGORY, "enabled", false).getBoolean();
        shuffle = config.get(CATEGORY, "shuffle", false).getBoolean();
        messages = new ArrayList<String>(Arrays.asList(config.get(CATEGORY, "messages", MESSAGES_DEFAULT, MESSAGES_HELP).getStringList()));
        initMessageOrder();
    }

    @Override
    public void save(Configuration config)
    {
        config.get(CATEGORY, "inverval", 60).set(interval);
        config.get(CATEGORY, "shuffle", false).set(shuffle);
        config.get(CATEGORY, "messages", MESSAGES_DEFAULT).set(messages.toArray(new String[messages.size()]));
    }

}
