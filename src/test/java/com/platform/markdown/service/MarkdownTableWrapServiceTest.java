package com.platform.markdown.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownTableWrapServiceTest {

    private MarkdownTableWrapService service;

    @BeforeEach
    void setUp() {
        service = new MarkdownTableWrapService();
    }

    @Test
    void wrapTables_wrapsTableInScrollableDiv() {
        String html = "<h2>Listado</h2><table><tr><td>A</td></tr></table>";
        String result = service.wrapTables(html);

        assertThat(result).contains("<div class=\"table-wrapper\"><table>");
    }

    @Test
    void wrapTables_wrapsEachTableIndependently() {
        String html = "<table><tr><td>A</td></tr></table><p>text</p><table><tr><td>B</td></tr></table>";
        String result = service.wrapTables(html);

        assertThat(result).containsOnlyOnce("<p>text</p>");
        assertThat(result.split("table-wrapper", -1)).hasSize(3);
    }

    @Test
    void wrapTables_leavesHtmlWithoutTablesUnchanged() {
        String html = "<p>No tables here</p>";
        String result = service.wrapTables(html);

        assertThat(result).isEqualTo(html);
    }
}
