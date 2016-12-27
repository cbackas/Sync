package commands;

import cback.SyncBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;

public interface Command {
    String getName();

    List<String> getAliases();

    void execute(SyncBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate);

}

