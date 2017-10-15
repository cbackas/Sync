package cback.eventFunctions;

import cback.SyncBot;
import cback.Util;
import cback.commands.CommandToDo;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;

public class ReactionChange {
    private SyncBot bot;

    public ReactionChange(SyncBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void todoMessageReactions(ReactionAddEvent event) {
        if (event.getUser().isBot() || event.getChannel().getLongID() != SyncBot.TODO_CH_ID) {
            return; //ignores bot reactions and reactions not related to the list
        }

        IMessage message = event.getMessage();
        IUser user = event.getUser();
        IReaction reaction = event.getReaction();
        if (user.getLongID() == SyncBot.CBACK_USR_ID) {
            String emojiName = event.getReaction().getEmoji().getName();

            if (emojiName.equals("📗")) {
                updateCompletedItem(message);
            } else if (emojiName.equals("📙")) {
                updateStartedItem(message);
            } else if (emojiName.equals("📕")) {
                message.delete();
            } else {
                removeReaction(message, user, reaction);
            }

        } else {
            removeReaction(message, user, reaction);
        }
    }

    private void updateStartedItem(IMessage message) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "📙 - todo item started";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.ORANGE);

            IChannel todoChannel = message.getChannel();
            Util.deleteMessage(message);
            message = todoChannel.sendMessage(text, embed.build());
            CommandToDo.setReactOptions(message);
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }

    private void updateCompletedItem(IMessage message) {
        try {
            IEmbed oldEmbed = message.getEmbeds().get(0);

            String ideaName = oldEmbed.getEmbedFields().get(0).getName();
            String ideaDesc = oldEmbed.getEmbedFields().get(0).getValue();

            String text = "📗 - todo item completed";
            EmbedBuilder embed = new EmbedBuilder()
                    .appendField(ideaName, ideaDesc, false)
                    .withFooterText("ID: " + message.getStringID())
                    .withTimestamp(System.currentTimeMillis())
                    .withColor(Color.GREEN);

            RequestBuffer.request(() -> message.removeAllReactions());
            RequestBuffer.request(() -> message.edit(text, embed.build()));
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }

    private void removeReaction(IMessage message, IUser user, IReaction reaction) {
        try {
            RequestBuffer.request(() -> message.removeReaction(user, reaction));
        } catch (DiscordException | MissingPermissionsException e) {
            Util.reportHome(e);
        }
    }
}
