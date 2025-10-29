package com.practice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class MyController {

    // GET: http://localhost:8080/api/hello?name=Chelsea
    // GET: http://localhost:8080/api/hello   => "Hello, World!" by default
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    // POST: http://localhost:8080/api/echo
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
}