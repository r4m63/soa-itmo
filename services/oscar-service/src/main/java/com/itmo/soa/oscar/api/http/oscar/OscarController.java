package com.itmo.soa.oscar.api.http.oscar;

import com.itmo.soa.contract.request.oscar.RedistributeOscarsPathParams;
import com.itmo.soa.contract.response.oscar.RedistributeOscarsResponse;
import com.itmo.soa.contract.response.oscar.RewardRRatedMoviesResponse;
import com.itmo.soa.oscar.domain.oscar.service.OscarService;
import org.springframework.http.MediaType;
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
        return oscarService.rewardRRatedMovies().execute();
    }

    @PostMapping("/genres/redistribute-rewards/{fromGenre}/{toGenre}")
    public RedistributeOscarsResponse redistributeOscars(RedistributeOscarsPathParams params) {
        return oscarService.redistributeOscars().execute(params.getFromGenre(), params.getToGenre());
    }
}
