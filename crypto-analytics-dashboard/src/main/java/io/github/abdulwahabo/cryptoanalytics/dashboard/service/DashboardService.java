package io.github.abdulwahabo.cryptoanalytics.dashboard.service;

import io.github.abdulwahabo.cryptoanalytics.dashboard.model.DashboardDataset;
import java.util.Optional;

public interface DashboardService {
    Optional<DashboardDataset> buildDataset();
}
