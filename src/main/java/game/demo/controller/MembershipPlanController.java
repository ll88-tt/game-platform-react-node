package game.demo.controller;

import game.demo.service.MembershipPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membership-plans")
public class MembershipPlanController {

    @Autowired
    private MembershipPlanService membershipPlanService;

    @GetMapping
    public ResponseEntity<?> getEnabledPlans() {
        return ResponseEntity.ok(membershipPlanService.getEnabledPlanViews());
    }
}
