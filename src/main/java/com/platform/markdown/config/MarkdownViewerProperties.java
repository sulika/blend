package com.platform.markdown.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.markdown-viewer")
public class MarkdownViewerProperties {

    private boolean enabled = true;
    private MarkdownHtmlConfig html = new MarkdownHtmlConfig();
    private List<MarkdownSourceConfig> sources = new ArrayList<>();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public MarkdownHtmlConfig getHtml() { return html; }
    public void setHtml(MarkdownHtmlConfig html) { this.html = html; }

    public List<MarkdownSourceConfig> getSources() { return sources; }
    public void setSources(List<MarkdownSourceConfig> sources) { this.sources = sources; }
}
