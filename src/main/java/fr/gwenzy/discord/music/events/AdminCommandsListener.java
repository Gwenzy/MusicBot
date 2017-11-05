package fr.gwenzy.discord.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.gwenzy.discord.music.Main;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.IOException;
import java.util.List;


/**
 * Created by gwend on 06/08/2017.
 */
public class AdminCommandsListener implements IListener<MessageReceivedEvent> {
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        if(!messageReceivedEvent.getChannel().isPrivate())
        if(messageReceivedEvent.getMessage().getFormattedContent().startsWith(Main.COMMAND_PREFIX)){
            String message = messageReceivedEvent.getMessage().getFormattedContent();
            while(message.contains("  ")){message = message.replaceAll("  ", " ");}
            String[] args = message.split(" ");




                if(args.length>1)
                    if(args[1].equalsIgnoreCase("join") && Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID())){
                        messageReceivedEvent.getAuthor().getVoiceStateForGuild(messageReceivedEvent.getGuild()).getChannel().join();
                    }
                    else if(args[1].equalsIgnoreCase("stop") && (Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID()))){
                        Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).player.stopTrack();

                    }
                    else if(args[1].equalsIgnoreCase("queue")){
                        System.out.println("Current song proposed by "+Main.currentAuthor.get(messageReceivedEvent.getGuild().getLongID()));
                        List<AudioTrack> tracks = Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).scheduler.getQueueTracks();
                        for(int i=0; i<tracks.size(); i++){
                            final int iFinal = i;
                            RequestBuffer.request(()->{
                                messageReceivedEvent.getChannel().sendMessage("Queue rank #"+(iFinal+1)+" : sumbitted by "+messageReceivedEvent.getGuild().getUserByID(Main.authors.get(messageReceivedEvent.getGuild().getLongID()).get(iFinal)).getName()+" - "+tracks.get(iFinal).getInfo().title);
                            });
                        }

                    }
                    else if(args[1].equalsIgnoreCase("leave") && Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID())){
                        messageReceivedEvent.getAuthor().getVoiceStateForGuild(messageReceivedEvent.getGuild()).getChannel().leave();
                    }
                    else if(args[1].equalsIgnoreCase("next")){
                        if(Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID()) || Main.currentAuthor.get(messageReceivedEvent.getGuild().getLongID()) == (messageReceivedEvent.getAuthor().getLongID())){
                            Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).scheduler.nextTrack(messageReceivedEvent.getChannel(), messageReceivedEvent.getGuild().getLongID());
                        }
                        else if(Main.nextCount.get(messageReceivedEvent.getGuild().getLongID()).contains(messageReceivedEvent.getAuthor().getLongID())&&Main.nextCount.get(messageReceivedEvent.getGuild().getLongID()).size()>=0.4*messageReceivedEvent.getGuild().getConnectedVoiceChannel().getUsersHere().size() ||
                                (!Main.nextCount.get(messageReceivedEvent.getGuild().getLongID()).contains(messageReceivedEvent.getAuthor().getLongID())&&Main.nextCount.get(messageReceivedEvent.getGuild().getLongID()).size()+1>=0.4*messageReceivedEvent.getGuild().getConnectedVoiceChannel().getUsersHere().size())){
                                Main.getGuildAudioPlayer(messageReceivedEvent.getGuild()).scheduler.nextTrack(messageReceivedEvent.getChannel(), messageReceivedEvent.getGuild().getLongID());
                        }
                        else if(Main.nextCount.get(messageReceivedEvent.getGuild().getLongID()).contains(messageReceivedEvent.getAuthor().getLongID())){
                            messageReceivedEvent.getChannel().sendMessage("Vous avez déjà demandé à passer cette musique, au moins 40% des personnes doivent indiquer vouloir passer pour forcer le passage à la musique suivante : "+Main.nextCount.get(messageReceivedEvent.getGuild().getLongID())+"/"+messageReceivedEvent.getGuild().getConnectedVoiceChannel().getUsersHere().size());
                        }
                        else if(!Main.nextCount.get(messageReceivedEvent.getGuild().getLongID()).contains(messageReceivedEvent.getAuthor().getLongID())){
                            List<Long> next = Main.nextCount.get(messageReceivedEvent.getGuild().getLongID());
                            next.add(messageReceivedEvent.getAuthor().getLongID());
                            Main.nextCount.put(messageReceivedEvent.getGuild().getLongID(), next);

                            messageReceivedEvent.getChannel().sendMessage("Vous avez demandé à passer cette musique, au moins 40% des personnes doivent indiquer vouloir passer pour forcer le passage à la musique suivante : "+Main.nextCount.get(messageReceivedEvent.getGuild().getLongID())+"/"+messageReceivedEvent.getGuild().getConnectedVoiceChannel().getUsersHere().size());
                        }
                    }
                    else if(args[1].equalsIgnoreCase("disconnect") && Main.operatorsID.contains(messageReceivedEvent.getAuthor().getStringID())){
                        Main.client.logout();
                    }
                    else if(args[1].equalsIgnoreCase("help")){
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.withColor(Color.GREEN);
                        eb.withAuthorName(Main.client.getApplicationName());
                        eb.withAuthorIcon(Main.client.getApplicationIconURL());
                        eb.withFooterText("Music bot developped by "+Main.client.getUserByID(205809466514472960L).getDisplayName(messageReceivedEvent.getGuild()));
                        eb.withTitle("Sagiri bot help <3\n-------------------------");
                        eb.appendField("What does sagiri do ?", "Sagiri is a music bot able to provide music from different sources, able to search on youtube and play musics from previous search. Hope I'll be useful \\(^_^)/", false);
                        eb.appendField("Play music from YT url", "@Sagiri play <YT Url/YT Video ID>", false);
                        eb.appendField("Play music from YT search", "Will play 1st result from YT search\n@Sagiri fastplay <Search>", false);
                        eb.appendField("Search musics on YT", "@Sagiri search <Search>", false);
                        eb.appendField("Play music from previous Sagiri search", "@Sagiri play #<Result ID>\nResult ID is a number from 1 to 5", false);

                        messageReceivedEvent.getChannel().sendMessage(eb.build());

                    }
                    else if(args[1].equalsIgnoreCase("infos")){

                        try {
                            AudioTrackInfo infos = Main.musicManagers.get(messageReceivedEvent.getGuild().getLongID()).player.getPlayingTrack().getInfo();
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.withColor(Color.CYAN);
                            eb.withTitle("Track informations");
                            eb.withDesc("------------------------------------------------------------------");
                            eb.appendField("Title", infos.title, false);
                            eb.appendField("Youtube Channel", infos.author, false);
                            eb.appendField("Duration", (infos.length/1000)+"s", false);
                            eb.appendField("URL", infos.uri, false);
                            eb.appendField("Progress", ""+((System.currentTimeMillis()-Main.startingTimestamp)/1000)+"/"+(infos.length/1000)+"s - "+Math.round(((double)System.currentTimeMillis()-(double)Main.startingTimestamp)/(double)infos.length*100)+"% - "+((infos.length-System.currentTimeMillis()+Main.startingTimestamp)/1000)+"s left" , false);
                            eb.appendField("User who added this music", Main.client.getUserByID(Main.currentAuthor.get(messageReceivedEvent.getGuild().getLongID())).mention()+"", false);
                            messageReceivedEvent.getChannel().sendMessage(eb.build());
                            messageReceivedEvent.getMessage().delete();



                        }catch(Exception e){
                            messageReceivedEvent.getChannel().sendMessage("No song is currently playing");
                            e.printStackTrace();
                        }
                    }
                if(args.length>2)
                    if(!messageReceivedEvent.getGuild().getConnectedVoiceChannel().getUsersHere().contains(messageReceivedEvent.getAuthor())){
                        messageReceivedEvent.getChannel().sendMessage("Pour des raisons évidentes, vous ne pouvez pas jouer de musique ni effectuer les commandes de recherche si vous n'êtes pas dans le canal vocal du bot.");

                    }else {
                        if (args[1].equalsIgnoreCase("play")) {


                            String path = args[2];
                            for (int i = 3; i < args.length; i++) {
                                path += " " + args[i];

                            }
                            if (path.startsWith("#") && Main.canUseIDs)
                                try {
                                    path = Main.videoIDs.get(Integer.parseInt(path.substring(1, path.length())));
                                } catch (Exception e) {
                                }


                            Main.loadAndPlay(messageReceivedEvent.getChannel(), path, messageReceivedEvent.getAuthor().getLongID(), messageReceivedEvent.getGuild());

                        } else if (args[1].equalsIgnoreCase("search")) {
                            String query = args[2];
                            for (int i = 3; i < args.length; i++) {
                                query += " " + args[i];

                            }
                            List<String> results = null;
                            try {
                                results = Main.search.search(query);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            int id = 1;
                            if (results == null)
                                messageReceivedEvent.getChannel().sendMessage("No results");
                            for (String str : results) {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.withColor(Color.CYAN);
                                eb.withTitle("Query result | ID #" + id);
                                eb.withDesc("------------------------------------------------------------------");

                                String title = str.split("!;;!")[0];
                                String videoID = str.split("!;;!")[1];
                                String duration = str.split("!;;!")[2];
                                String channel = str.split("!;;!")[3];
                                String thumbnailURL = str.split("!;;!")[4];

                                PeriodFormatter formatter = ISOPeriodFormat.standard();
                                Period p = formatter.parsePeriod(duration);
                                duration = String.format("%02d", p.getMinutes()) + ":" + String.format("%02d", p.getSeconds());


                                eb.appendField("Title", title, false);
                                eb.appendField("ID", videoID, false);

                                eb.appendField("Duration", duration, false);
                                eb.appendField("Channel", channel, false);
                                eb.withThumbnail(thumbnailURL);
                                Main.canUseIDs = true;
                                Main.videoIDs.put(id, videoID);

                                RequestBuffer.request(() -> {
                                    messageReceivedEvent.getChannel().sendMessage(eb.build());
                                });
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                }
                                id++;
                            }


                        } else if (args[1].equalsIgnoreCase("fastplay")) {
                            String query = args[2];
                            for (int i = 3; i < args.length; i++) {
                                query += " " + args[i];

                            }
                            List<String> results = null;
                            try {
                                results = Main.search.search(query);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                Main.loadAndPlay(messageReceivedEvent.getChannel(), results.get(0).split("!;;!")[1], messageReceivedEvent.getAuthor().getLongID(), messageReceivedEvent.getGuild());
                            } catch (Exception e) {
                                messageReceivedEvent.getChannel().sendMessage("No results");
                            }
                        }


                    }


        }
    }
}
