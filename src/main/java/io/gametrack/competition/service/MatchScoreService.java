package io.gametrack.competition.service;

import io.gametrack.competition.domain.entity.Game;
import io.gametrack.competition.domain.entity.Match;
import io.gametrack.core.EventType;
import io.gametrack.player.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import javax.annotation.PostConstruct;

import static reactor.bus.selector.Selectors.$;

/**
 * @author Kevin Sutton
 */
@Service
public class MatchScoreService implements Consumer<Event<Game>>   {

    private EventBus bus;

    private static final Logger logger = LoggerFactory.getLogger(MatchScoreService.class);

    @Autowired
    public MatchScoreService(EventBus bus) {
        this.bus = bus;
    }

    @PostConstruct
    public void registerListeners() {
        bus.on($(EventType.GAME_SCORE_CHANGE), this);
    }

    @Override
    public void accept(Event<Game> ev) {
        Game game = ev.getData();
        recalculateMatchScore(game.getMatch());
    }

    public void recalculateMatchScore(Match match) {
        logger.info("Recalculating match score");
        int scoreOne = 0;
        int scoreTwo = 0;
        for (Game game : match.getGames()) {
            Side winner = game.getWinner().get();
            if (winner.equals(match.getScores().get(0).getSide())) {
                scoreOne++;
            }
            else {
                scoreTwo++;
            }
        }
        match.setScores(scoreOne, scoreTwo);
    }

}
