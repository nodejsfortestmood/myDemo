package com.strategy.model;

import lombok.Data;

@Data
public class NavigationLink {
    private String title;
    private String url;

    public NavigationLink(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
