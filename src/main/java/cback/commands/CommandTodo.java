package cback.commands;

import cback.SyncBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandTodo implements Command {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, SyncBot bot) {

    }
}
