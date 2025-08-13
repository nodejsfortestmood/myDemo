package com.strategy.controller;

import com.strategy.model.NavigationLink;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        // 添加导航链接
        List<NavigationLink> navLinks = new ArrayList<>();
        navLinks.add(new NavigationLink("趋势数据-mix", "/stock/trends"));
        navLinks.add(new NavigationLink("趋势数据-daily", "/stock/trends2"));
        navLinks.add(new NavigationLink("趋势数据-month", "/stock/trends3"));
        navLinks.add(new NavigationLink("选股播放", "/stock/play"));
        navLinks.add(new NavigationLink("分析工具", "/stock/analysis-tools"));

        model.addAttribute("navLinks", navLinks);
        return "home";
    }
}
