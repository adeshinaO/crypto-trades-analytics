package io.github.abdulwahabo.cryptoanalytics.dashboard.web;

import io.github.abdulwahabo.cryptoanalytics.dashboard.model.DashboardDataset;
import io.github.abdulwahabo.cryptoanalytics.dashboard.service.DashboardService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    @Value("${host}")
    private String host;

    private DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard(ModelMap modelMap) {
        modelMap.addAttribute("data_api_url", host.concat("/data"));
        return new ModelAndView("dashboard");
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<DashboardDataset> data() {
        Optional<DashboardDataset> dataOptional = dashboardService.buildDataset();
        return dataOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
