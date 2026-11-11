package tbmelee;

import java.time.LocalDateTime;

public class GameClock {
    private LocalDateTime inGameTime;

    public GameClock() {
        this.inGameTime = LocalDateTime.of(1000, 1, 1, 8, 0,0);
    }

    public void advanceTurn() {
        inGameTime = inGameTime.plusSeconds(5);
    }

    public LocalDateTime getCurrentTime() {
        return inGameTime;
    }
    
}
