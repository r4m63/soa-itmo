package com.itmo.soa.oscar.domain.oscar.service;

import com.itmo.soa.oscar.domain.oscar.service.actions.RedistributeOscarsAction;
import com.itmo.soa.oscar.domain.oscar.service.actions.RewardRRatedMoviesAction;
import org.springframework.stereotype.Service;

/**
 * Фасад: только собирает команды, логики нет.
 */
@Service
public class OscarService {

    private final RewardRRatedMoviesAction rewardRRatedMovies;
    private final RedistributeOscarsAction redistributeOscars;

    public OscarService(RewardRRatedMoviesAction rewardRRatedMovies, RedistributeOscarsAction redistributeOscars) {
        this.rewardRRatedMovies = rewardRRatedMovies;
        this.redistributeOscars = redistributeOscars;
    }

    public RewardRRatedMoviesAction rewardRRatedMovies() {
        return rewardRRatedMovies;
    }

    public RedistributeOscarsAction redistributeOscars() {
        return redistributeOscars;
    }
}
