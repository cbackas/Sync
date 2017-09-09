package cback.eventFunctions;

import cback.SyncBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.NicknameChangedEvent;
import sx.blah.discord.handle.obj.IGuild;

public class NicknameChange {
    private SyncBot bot;

    public NicknameChange(SyncBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void nicknameChangeEvent(NicknameChangedEvent event) {
        IGuild guild = event.getGuild();

        if (SyncBot.ALL_SERVERS.contains(guild.getLongID())) {

            SyncBot.ALL_SERVERS.stream()
                    .filter(c -> !c.equals(guild.getStringID()))
                    .forEach(c -> {
                        try {
                            event.getClient().getGuildByID(Long.parseLong(c)).setUserNickname(event.getUser(), event.getUser().getDisplayName(guild));
                        } catch (Exception e) {
                        }
                    });

        }
    }

}
