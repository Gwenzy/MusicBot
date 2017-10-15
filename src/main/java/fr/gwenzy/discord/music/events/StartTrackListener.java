package fr.gwenzy.discord.music.events;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent;
import fr.gwenzy.discord.music.Main;

public class StartTrackListener implements AudioEventListener {


    @Override
    public void onEvent(AudioEvent audioEvent) {
        Main.startingTimestamp = System.currentTimeMillis();
        Main.authors.remove(0);



    }
}