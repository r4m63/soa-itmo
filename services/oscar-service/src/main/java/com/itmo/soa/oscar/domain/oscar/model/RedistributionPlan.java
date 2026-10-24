package com.itmo.soa.oscar.domain.oscar.model;

import com.itmo.soa.contract.error.exception.InvalidOperationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record RedistributionPlan(List<OscarsChange> changes, int sourceMovies, int targetMovies,
                                 long transferredOscars) {

    public static RedistributionPlan calculate(String targetGenre, List<AwardedMovie> sources,
                                               List<AwardedMovie> targets) {
        if (targets.isEmpty()) {
            throw new InvalidOperationException("В жанре " + targetGenre + " нет фильмов для получения наград");
        }

        List<OscarsChange> changes = new ArrayList<>();
        long transferred = 0;
        for (AwardedMovie source : sources) {
            if (source.oscarsCount() > 1) {
                transferred += source.oscarsCount() - 1L;
                changes.add(new OscarsChange(source.id(), source.oscarsCount(), 1));
            }
        }

        List<AwardedMovie> byId = targets.stream().sorted(Comparator.comparingInt(AwardedMovie::id)).toList();
        long share = transferred / byId.size();
        long remainder = transferred % byId.size();
        for (int i = 0; i < byId.size(); i++) {
            AwardedMovie target = byId.get(i);
            long added = share + (i < remainder ? 1 : 0);
            if (added > 0) {
                changes.add(new OscarsChange(target.id(), target.oscarsCount(),
                        Math.toIntExact(target.oscarsCount() + added)));
            }
        }
        return new RedistributionPlan(List.copyOf(changes), sources.size(), targets.size(), transferred);
    }
}
