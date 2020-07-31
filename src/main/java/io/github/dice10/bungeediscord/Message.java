package io.github.dice10.bungeediscord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Optional;

public class Message implements Listener{//, addMessageCreateListener {
    protected String token;
    protected DiscordApi api;
    protected String senderServer;
    protected long chatChannelID;

    public Message(String _token,DiscordApi _api,long _chatChannelID){
        setToken(_token);
        setApi(_api);
        setChatChannelID(_chatChannelID);
    }
    @EventHandler
    public void getChatMessage(ChatEvent event){
        if(!event.isCommand()) {
            long channelID = getChatChannelID();
            DiscordApi api = getApi();
            TextChannel channel = api.getTextChannelById(channelID).get();
            final ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
            this.senderServer = sender.getServer().getInfo().getName();
            String message = event.getMessage();
            String nameOnServer = "**[" + sender + "@" + senderServer + "]**";
            new MessageBuilder()
                    .append(nameOnServer + message)
                    .send(channel);
        }
    }

    public void setToken(String _token){
        this.token = _token;
    }

    public String getToken(){
        return this.token;
    }

    public void setApi(DiscordApi _api){
        this.api = _api;
    }

    public DiscordApi getApi(){
        return this.api;
    }

    public void setChatChannelID(long Id){
        this.chatChannelID = Id;
    }

    public long getChatChannelID(){
        return this.chatChannelID;
    }

}
