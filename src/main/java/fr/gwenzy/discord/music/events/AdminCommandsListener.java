package fr.gwenzy.discord.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.gwenzy.discord.music.Main;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

/**
 * Created by gwend on 06/08/2017.
 */
public class AdminCommandsListener implements IListener<MessageReceivedEvent> {
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        if(messageReceivedEvent.getMessage().getFormattedContent().startsWith(Main.COMMAND_PREFIX)){
            String[] args = messageReceivedEvent.getMessage().getFormattedContent().split(" ");



            if(Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID()))
                if(args.length>1)
                    if(args[1].equalsIgnoreCase("join")){
                        messageReceivedEvent.getAuthor().getVoiceStateForGuild(messageReceivedEvent.getGuild()).getChannel().join();
                    }
                    else if(args[1].equalsIgnoreCase("stop")){
                        Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).player.stopTrack();

                    }
                    else if(args[1].equalsIgnoreCase("leave")){
                        messageReceivedEvent.getAuthor().getVoiceStateForGuild(messageReceivedEvent.getGuild()).getChannel().leave();
                    }
                    else if(args[1].equalsIgnoreCase("next")){
                        Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).scheduler.nextTrack();
                    }
                    else if(args[1].equalsIgnoreCase("disconnect")){
                        Main.client.logout();
                    }
                    else if(args[1].equalsIgnoreCase("infos")){

                        try {
                            AudioTrackInfo infos = Main.musicManagers.get(338429681474863115L).player.getPlayingTrack().getInfo();
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.withColor(Color.CYAN);
                            eb.withTitle("Track informations");
                            eb.withDesc("------------------------------------------------------------------");
                            eb.appendField("Title", infos.title, false);
                            eb.appendField("Youtube Channel", infos.author, false);
                            eb.appendField("Duration", (infos.length/1000)+"s", false);
                            eb.appendField("URL", infos.uri, false);

                            messageReceivedEvent.getChannel().sendMessage(eb.build());
                            messageReceivedEvent.getMessage().delete();

                        }catch(Exception e){
                            messageReceivedEvent.getChannel().sendMessage("No song is currently playing");
                            e.printStackTrace();
                        }
                    }
                if(args.length>2)
                    if(args[1].equalsIgnoreCase("play")){
                        String path = args[2];
                        for(int i=3; i<args.length; i++){
                            path += " "+ args[i];

                        }
                        System.out.println("Playing "+path);
                        Main.loadAndPlay(messageReceivedEvent.getChannel(), path);
                    }









        }
    }
}
