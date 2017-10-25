package cback;

import cback.commands.Command;
import cback.eventFunctions.*;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SyncBot {

    private static SyncBot instance;
    private IDiscordClient client;

    private ConfigManager configManager;

    public List<Command> registeredCommands = new ArrayList<>();

    private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\*([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);

    public static Color BOT_COLOR = Color.decode("#" + "023563");
    public static final long CBACK_USR_ID = 73416411443113984L;
    public static final long HUB_GLD_ID = 346104115169853440L;
    public static final List<String> ALL_SERVERS = Arrays.asList("192441520178200577", "256248900124540929", "263120914894422017");

    public static final long MEMBERLOG_CH_ID = 263122800313761792L;
    public static final long ERROR_CH_ID = 346104666796589056L;
    public static final long BOTLOG_CH_ID = 346483682376286208L;
    public static final long BOTPM_CH_ID = 346104720903110656L;
    public static final long TODO_CH_ID = 355971482289176576L;

    public static void main(String[] args) {
        new SyncBot();
    }

    public SyncBot() {
        instance = this;

        //instantiate config manager first as connect() relies on tokens
        configManager = new ConfigManager(this);

        connect();
        client.getDispatcher().registerListener(this);
        client.getDispatcher().registerListener(new MemberChange(this));
        client.getDispatcher().registerListener(new NicknameChange(this));
        client.getDispatcher().registerListener(new ReactionChange(this));
        //

        registerAllCommands();
    }

    private void connect() {
        //don't load external modules and don't attempt to create modules folder
        Configuration.LOAD_EXTERNAL_MODULES = false;

        Optional<String> token = configManager.getTokenValue("botToken");
        if (!token.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your bot's token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token.get());
        clientBuilder.setMaxReconnectAttempts(5);
        try {
            client = clientBuilder.login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message Central Choo Choo
     */
    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) return; //ignore bot messages
        IMessage message = event.getMessage();
        IGuild guild = null;
        boolean isPrivate = message.getChannel().isPrivate();
        if (!isPrivate) guild = message.getGuild();
        String text = message.getContent();
        Matcher matcher = COMMAND_PATTERN.matcher(text);
        if (matcher.matches()) {
            String baseCommand = matcher.group(1).toLowerCase();
            Optional<Command> command = registeredCommands.stream()
                    .filter(com -> com.getName().equalsIgnoreCase(baseCommand) || (com.getAliases() != null && com.getAliases().contains(baseCommand)))
                    .findAny();
            if (command.isPresent()) {
                System.out.println("@" + message.getAuthor().getName() + " issued \"" + text + "\" in " +
                        (isPrivate ? ("@" + message.getAuthor().getName()) : guild.getName()));

                String args = matcher.group(2);
                String[] argsArr = args.isEmpty() ? new String[0] : args.split(" ");

                List<Long> roleIDs = message.getAuthor().getRolesForGuild(guild).stream().map(role -> role.getLongID()).collect(Collectors.toList());

                IUser author = message.getAuthor();
                String content = message.getContent();

                Command cCommand = command.get();

                /*
                 * Permission check
                 */
                if (author.getLongID() == CBACK_USR_ID) {
                    cCommand.execute(message, content, argsArr, author, guild, roleIDs, isPrivate, client, this);
                    Util.botLog(message);
                }
            }
            /**
             * Forwards the random stuff people PM to the bot - to me
             */
        } else if (message.getChannel().isPrivate()) {
            EmbedBuilder bld = new EmbedBuilder()
                    .withColor(BOT_COLOR)
                    .withTimestamp(System.currentTimeMillis())
                    .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                    .withAuthorIcon(message.getAuthor().getAvatarURL())
                    .withDesc(message.getContent());

            for (IMessage.Attachment a : message.getAttachments()) {
                bld.withImage(a.getUrl());
            }

            Util.sendEmbed(client.getChannelByID(BOTPM_CH_ID), bld.build());
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        client = event.getClient();
        System.out.println("Logged in.");

    }

    public IDiscordClient getClient() {
        return client;
    }

    public static SyncBot getInstance() {
        return instance;
    }

    private void registerAllCommands() {
        new Reflections("cback.commands").getSubTypesOf(Command.class).forEach(commandImpl -> {
            try {
                Command command = commandImpl.newInstance();
                Optional<Command> existingCommand = registeredCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findAny();
                if (!existingCommand.isPresent()) {
                    registeredCommands.add(command);
                    System.out.println("Registered command: " + command.getName());
                } else {
                    System.out.println("Attempted to register two commands with the same name: " + existingCommand.get().getName());
                    System.out.println("Existing: " + existingCommand.get().getClass().getName());
                    System.out.println("Attempted: " + commandImpl.getName());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
