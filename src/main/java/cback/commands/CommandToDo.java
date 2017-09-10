package cback.commands;

import cback.SyncBot;
import cback.Util;
import cback.eventFunctions.ReactionChange;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandToDo implements Command {
    @Override
    public String getName() {
        return "todo";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, SyncBot bot) {
        args = content.split(" \\| ");
        String ideaName = args[0].split(" ", 2)[1];
        String ideaDesc = args[1];

        EmbedObject embed = ReactionChange.buildNewItem(ideaName, ideaDesc);

        Util.deleteMessage(message);

        final IMessage todoMessage = Util.sendEmbed(client.getChannelByID(SyncBot.TODO_CH_ID), embed);

        ReactionChange.updateMessageID(todoMessage);
        ReactionChange.resetReactions(todoMessage);
    }
}
