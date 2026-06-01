package boardLogic;

import javax.swing.*;

/**
 * This class implements a turn timer that counts down the time a player has to perform a move.
 * It executes a specified action on each tick and another action when the time runs out.
 */
public class TurnTimer {
    //region Fields
    private Timer timer;
    private int timeLeft;
    private final int initialTime;
    private static final int TIME_PER_TICK = 1000;

    private final Runnable onTick;
    private final Runnable onTimeout;
    //endregion

    /**
     * Construct a new timer.
     *
     * @param initialTime The time a player has to perform a move.
     * @param onTick The event that gets executed on each tick.
     * @param onTimeout The event that gets executed when the initialTime has run out.
     */
    public TurnTimer(int initialTime, Runnable onTick, Runnable onTimeout) {
        this.initialTime = initialTime;
        this.timeLeft = initialTime;
        this.onTick = onTick;
        this.onTimeout = onTimeout;
    }

    /**
     * Starts the timer.
     * The timePerTick defines, in which intervalls the onTick event should be executed.
     * The onTimeout event gets executed, when the remaining thinking time runs out (=0).
     */
    public void startTimer() {
        timer = new Timer(TIME_PER_TICK, e -> {
            timeLeft--;

            if(onTick != null) {
                onTick.run();
            }

            if(timeLeft <= 0) {
                stopTimer();
                if(onTimeout != null) {
                    onTimeout.run();
                }
            }            
        });
        
        timer.start();
    }

    /**
     * Stops the timer, causing it to stop executing events as well.
     */
    public void stopTimer() {
        if(timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * Stops the timer, resets the remaining thinking time and starts the timer again.
     */
    public void resetTimer() {
        stopTimer();
        timeLeft = initialTime;
        startTimer();
    }

    /**
     * Getter for the remaining thinking time.
     *
     * @return The current time remaining to perform a move.
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * Sets the remaining time left to perform a move.
     *
     * @param timeLeft The remaining time that should be set.
     */
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
