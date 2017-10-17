package fr.gwenzy.discord.music.events;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent;
import fr.gwenzy.discord.music.Main;

import java.util.List;

public class StartTrackListener implements AudioEventListener {

    private long guildID;
    public StartTrackListener(long guildID){
        this.guildID = guildID;
    }

    @Override
    public void onEvent(AudioEvent audioEvent) {
        Main.startingTimestamp = System.currentTimeMillis();
        List<Long> author = Main.authors.get(this.guildID);
        author.remove(0);
        Main.authors.put(this.guildID, author);



    }
}
