package fr.gwenzy.discord.music.events;

import fr.gwenzy.discord.music.Main;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.util.Timer;
import java.util.TimerTask;

public class ReadyListener implements IListener<ReadyEvent> {

    public void handle(ReadyEvent readyEvent) {
        Timer t = new Timer("Updates");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Main.client.changePlayingText("music");
            }
        }, 0, 1000);
    }
}