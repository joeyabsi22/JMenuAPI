package me.jmang.japi.jmenuapi;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class Animation<FrameType> {

    private static final int defaultDelay = 0;
    private static final int defaultPeriod = 20;

    private final BukkitRunnable animation = new BukkitRunnable() {
        @Override
        public void run() {
            if (isPaused) return;
            onFrame();
            nextFrame();
        }
    };

    private final ArrayList<FrameType> frames = new ArrayList<>();
    private volatile FrameType currentFrame;

    private volatile boolean isPaused = false;
    private int period = defaultPeriod;

    public abstract void onFrame();

    public @NotNull ArrayList<FrameType> getFrames() {
        return (ArrayList<FrameType>) frames.clone();
    }

    private FrameType getCurrentFrame() {
        return currentFrame;
    }

    private void nextFrame() {
        try {
            currentFrame = frames.get(frames.indexOf(currentFrame) + 1);
        } catch (IndexOutOfBoundsException e) {
            currentFrame = frames.get(0);
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void start() {
        update();
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    private void update() {
        try {
            animation.cancel();
        } catch (IllegalStateException ignored) {}
        // here might be the prob
        animation.runTaskTimerAsynchronously(JMenuAPI.getInstance(), defaultDelay, period);
    }
}
