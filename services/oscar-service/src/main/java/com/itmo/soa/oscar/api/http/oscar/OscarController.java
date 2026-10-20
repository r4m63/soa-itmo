package com.itmo.soa.oscar.api.http.oscar;

import com.itmo.soa.contract.oscar.BulkOperationResult;
import com.itmo.soa.contract.oscar.RedistributionResult;
import com.itmo.soa.oscar.domain.oscar.service.OscarService;
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
    public BulkOperationResult rewardRRatedMovies() {
        return oscarService.rewardRRatedMovies().execute();
    }

    @PostMapping("/genres/redistribute-rewards/{fromGenre}/{toGenre}")
    public RedistributionResult redistributeOscars(@PathVariable String fromGenre, @PathVariable String toGenre) {
        return oscarService.redistributeOscars().execute(fromGenre, toGenre);
    }
}
