package cback;

import cback.commands.Command;
import cback.eventFunctions.*;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("FieldCanBeLocal")
public class SyncBot {

    private static SyncBot instance;
    private IDiscordClient client;

    private ConfigManager configManager;

    private List<String> botAdmins = new ArrayList<>();
    public List<Command> registeredCommands = new ArrayList<>();

    private static final Pattern COMMAND_PATTERN = Pattern.compile("^//([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
    public static final String MEMBERLOG_CHANNEL_ID = "263122800313761792";
    public static final String GENERAL_CHANNEL_ID = "256248900124540929";

    public static final List<String> GLOBAL_CHANNELS = Arrays.asList("263148121654034442", "263148339258720257", "263148412319301634");

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
        client.getDispatcher().registerListener(new NicknameChange());

        registerAllCommands();

        botAdmins.add("109109946565537792");
        botAdmins.add("148279556619370496");
        botAdmins.add("73416411443113984");
        botAdmins.add("144412318447435776");

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
                command.get().execute(this, client, argsArr, guild, message, isPrivate);
            }
        } else {
            String lowerCase = message.getContent().toLowerCase();
            List<IUser> mentions = message.getMentions();

            //Check for bot mentions
            if (mentions.contains(client.getOurUser()) || mentions.contains(client.getUserByID("229701685998518274")) || mentions.contains(client.getUserByID("261755032226103296")) || mentions.contains(client.getUserByID("229701685998518274"))) {
                Util.sendPrivateMessage(client.getUserByID("73416411443113984"), "A bot was mentioned in **" + message.getGuild().getName() + "/**" + message.getChannel().mention() + " by **" + message.getAuthor().getDisplayName(client.getGuildByID("256248900124540929")) + "**");
            }

            //cback mentions
            if (lowerCase.contains("cback")) {
                Util.sendPrivateMessage(client.getUserByID("73416411443113984"), "**" + message.getAuthor().getDisplayName(client.getGuildByID("256248900124540929")) + "** said your name in **" + message.getGuild().getName() + "/**" + message.getChannel().mention());
            }

            if (GLOBAL_CHANNELS.contains(message.getChannel().getID())) {

                GLOBAL_CHANNELS.stream()
                .filter(c -> !c.equals(message.getChannel().getID()))
                        .forEach(c -> {

                    String url = globalChannels.getChannel(c).serverURL;
                    Util.sendGlobalChat(url, message);

                });
            }

        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("Logged in.");
    }


    public ConfigManager getConfigManager() {
        return configManager;
    }

    public IDiscordClient getClient() {
        return client;
    }

    public List<String> getBotAdmins() {
        return botAdmins;
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

    public static SyncBot getInstance() {
        return instance;
    }

}
