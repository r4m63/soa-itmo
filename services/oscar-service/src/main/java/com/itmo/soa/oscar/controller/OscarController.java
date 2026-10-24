package com.itmo.soa.oscar.controller;

import com.itmo.soa.contract.response.oscar.RedistributeOscarsResponse;
import com.itmo.soa.contract.response.oscar.RewardRRatedMoviesResponse;
import com.itmo.soa.oscar.service.OscarService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/oscar", produces = MediaType.APPLICATION_JSON_VALUE)
public class OscarController {

    private final OscarService oscarService;

    public OscarController(OscarService oscarService) {
        this.oscarService = oscarService;
    }

    @PostMapping("/movies/reward-r")
    public RewardRRatedMoviesResponse rewardRRatedMovies() {
        return oscarService.rewardRRatedMovies();
    }

    @PostMapping("/genres/redistribute-rewards/{from-genre}/{to-genre}")
    public RedistributeOscarsResponse redistributeOscars(@PathVariable("from-genre") String fromGenre,
                                                         @PathVariable("to-genre") String toGenre) {
        return oscarService.redistributeOscars(fromGenre, toGenre);
    }
}
