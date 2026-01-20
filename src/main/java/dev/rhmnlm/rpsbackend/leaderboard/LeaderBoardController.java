package dev.rhmnlm.rpsbackend.leaderboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderBoardController {

    private static final Logger log = LoggerFactory.getLogger(LeaderBoardController.class);

    @GetMapping("/hell")
    public String test(){
        log.info("leaderboard is running");
        return "Leaderboard service is running";
    }
}