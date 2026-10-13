package com.assessmenttool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/ping")
    public String ping() {
        if (jdbcTemplate != null) {
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            } catch (Exception ignored) {
            }
        }
        return "pong";
    }
}
