package com.platform.markdown.config;

import java.util.ArrayList;
import java.util.List;

public class MarkdownHtmlConfig {

    private String title = "Markdown Viewer";
    private String theme = "github";
    private List<String> css = new ArrayList<>();
    private String injectHead;
    private String injectBodyEnd;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public List<String> getCss() { return css; }
    public void setCss(List<String> css) { this.css = css; }

    public String getInjectHead() { return injectHead; }
    public void setInjectHead(String injectHead) { this.injectHead = injectHead; }

    public String getInjectBodyEnd() { return injectBodyEnd; }
    public void setInjectBodyEnd(String injectBodyEnd) { this.injectBodyEnd = injectBodyEnd; }
}
