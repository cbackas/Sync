public enum globalChannels {
    LOUNGE("263148121654034442", "https://ptb.discordapp.com/api/webhooks/257588227517448192/ZwFhqeK8CrsMVKB9qwsBiSSPHKjzn96f5E5NCMizYzFD4MAr7Q2kqJ48wdNGHRwaUQYU/slack"),
    CINEMA("263148339258720257", "https://ptb.discordapp.com/api/webhooks/257588104263761921/WKnG8NWmgOD96Sy1nWPTD28Gvkc6o9BasEhT9vSkfW7UJixiJVm83E5nR6X8MJiBjDxl/slack"),
    GAMING("263148412319301634", "https://ptb.discordapp.com/api/webhooks/263153240919506945/3LDWBV90IUOCnu0pfQECzrV95p3Wwd37vZ5sKopZhXVXRox68ypcfs3cYaJNwLIcQFGp/slack");

    public String channelID;
    public String serverURL;

    globalChannels(String channelID, String serverURL) {
        this.channelID = channelID;
        this.serverURL = serverURL;
    }

    public static globalChannels getChannel(String ID) {
        for (globalChannels channel : values()) {
            if (channel.channelID.equalsIgnoreCase(ID)) {
                return channel;
            }
        }
        return null;
    }
}