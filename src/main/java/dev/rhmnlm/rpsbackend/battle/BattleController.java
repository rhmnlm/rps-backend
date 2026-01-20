package dev.rhmnlm.rpsbackend.battle;

import dev.rhmnlm.rpsbackend.enums.RPS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.f4b6a3.uuid.UuidCreator;

@RestController
public class BattleController {

    private static final Logger log = LoggerFactory.getLogger(BattleController.class);

    @PostMapping("/startBattle")
    public String startGame(){
        String charId = "char_" + UuidCreator.getTimeOrderedEpoch();
        return "Game started. Your game id is " + charId + ". Please pass this in the Bearer token on each round.";
    }

    @PostMapping("/fight")
    public String fight(@RequestBody RPS playerMove){
        RPS cpuMove = RPS.random();

        log.info("Generated cpu movement: {}", cpuMove);

        if(playerMove.beats(cpuMove)){
            return "Congrats. You win! You chose " + playerMove + " while opponent chose " + cpuMove + ".";
        } else {
            return "Shoot, you lose. You chose " + playerMove + " while opponent chose " + cpuMove + ". You can try again tho.";
        }
    }
}