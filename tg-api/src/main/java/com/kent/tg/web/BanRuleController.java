package com.kent.tg.web;

import com.kent.tg.domain.BanRule;
import com.kent.tg.service.BanRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "BanRule")
@RestController
public class BanRuleController {

    @Autowired
    private BanRuleService banRuleService;

    @Operation(summary = "list ban rules", description = "list ban rules")
    @GetMapping("/banRules")
    public List<Map<String, Object>> listAll() {
        return banRuleService.listMaps();
    }


    @Operation(summary = "add ban rule", description = "add ban rule")
    @PostMapping("/banRules")
    public void addBanRule(@RequestBody Map<String, Object> body) {
        String property = (String) body.getOrDefault("property", "");
        String rule = (String) body.getOrDefault("rule", "");
        if (StringUtils.isNotBlank(rule)) {
            BanRule banRule = new BanRule();
            banRule.setProperty(property);
            banRule.setRule(rule);
            banRuleService.save(banRule);
        }
    }

}
