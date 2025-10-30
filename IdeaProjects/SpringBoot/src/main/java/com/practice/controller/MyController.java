package com.practice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/test")
public class MyController {

    // GET: http://localhost:8080/test/hello?name=Chelsea
    // GET: http://localhost:8080/test/hello   => "Hello, World!" by default
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    // POST: http://localhost:8080/test/echo
    // Body -> raw (JSON) -> { "msg": "Hello World." }
    @PostMapping("/echo")
    public Msg echo(@RequestBody Msg input) {
        return input;
    }

    // Wrap the JSON string into an object, can work without it (using String).
    public static class Msg {
        public String msg;
        public Msg() {}
        public Msg(String msg) {
            this.msg = msg;
        }
    }

    // test postgreSQL
    @Autowired
    private DataSource dataSource;

    @GetMapping("/psql")
    public String testDbConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "[DB OK] Connected to " + conn.getMetaData().getDatabaseProductName();
        } catch (Exception e) {
            return "[DB ERROR] " + e.getMessage();
        }
    }
}