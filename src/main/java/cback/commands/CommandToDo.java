package cback.commands;

import cback.SyncBot;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
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
            IChannel todoChannel = client.getChannelByID(SyncBot.TODO_CH_ID);
            sendNewItem(todoChannel, ideaName, ideaDesc);

            IChannel messageChannel = message.getChannel();
            if (messageChannel.getGuild().getLongID() != SyncBot.HUB_GLD_ID) {
                Util.simpleEmbed(messageChannel, "New todo added. \"" + ideaDesc + "\"");
            }

            Util.deleteMessage(message);
        }
    }

    public void sendNewItem(IChannel channel, String ideaName, String ideaDesc) {
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .withAuthorName("\uD83D\uDDC3 new todo item")
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: null")
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.WHITE);

            IMessage todoMessage = Util.sendEmbed(channel, embed.build());
            updateMessageID(todoMessage);
        } catch (Exception e) {
            Util.reportHome(e);
        }
    }

    public void updateMessageID(IMessage message) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "\uD83D\uDDC3 - new todo item";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.WHITE);

            RequestBuffer.request(() -> message.edit(text, embed.build()));
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }
}
