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
        String ideaName = "";
        String ideaDesc = "";
        try {
            args = content.split(" \\| ");
            ideaName = args[0].split(" ", 2)[1];
            ideaDesc = args[1];
        } catch (Exception e) {
            Util.reportHome(message, e);
        }

        if (!ideaName.equals("") && !ideaDesc.equals("")) {
            EmbedObject embed = ReactionChange.buildNewItem(ideaName, ideaDesc);

            Util.deleteMessage(message);

            final IMessage todoMessage = Util.sendEmbed(client.getChannelByID(SyncBot.TODO_CH_ID), embed);

            ReactionChange.updateMessageID(todoMessage);
        } else {
            Util.simpleEmbed(message.getChannel(), "Error: check " + client.getChannelByID(SyncBot.ERROR_CH_ID) + " more info.");
        }
    }
}
