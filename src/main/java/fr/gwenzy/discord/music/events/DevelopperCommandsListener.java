package fr.gwenzy.discord.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.gwenzy.discord.music.Main;
import fr.gwenzy.discord.music.Tokens;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.IOException;
import java.util.List;


/**
 * Created by gwend on 06/08/2017.
 */
public class DevelopperCommandsListener implements IListener<MessageReceivedEvent> {
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        if(messageReceivedEvent.getChannel().isPrivate())
            if(messageReceivedEvent.getMessage().getFormattedContent().startsWith(Main.COMMAND_PREFIX)&&messageReceivedEvent.getAuthor().getLongID()== Tokens.AUTHOR_ID){
                String message = messageReceivedEvent.getMessage().getFormattedContent();
                while(message.contains("  ")){message = message.replaceAll("  ", " ");}
                String[] args = message.split(" ");
                    if(args.length==2){
                        if(args[1].equalsIgnoreCase("streamon")){
                            Main.client.streaming("My author is improving me !", "https://www.twitch.tv/thaksin_");
                            messageReceivedEvent.getChannel().sendMessage("Streaming mode is now enabled");
                        }
                        else if(args[1].equalsIgnoreCase("streamoff")){
                            Main.client.online("music");
                            messageReceivedEvent.getChannel().sendMessage("Streaming mode is now disabled");
                        }

                    }


        }
    }
}
