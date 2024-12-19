package com.mars.NangPaGo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/nangpago")
    public String nangPaGo() {
        return "냉파고의 시작";
    }
}
