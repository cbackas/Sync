package cback.commands;

import cback.SyncBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public interface Command {
    String getName();

    List<String> getAliases();

    void execute(IMessage message, String content, String[] args,IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, SyncBot bot);
}

