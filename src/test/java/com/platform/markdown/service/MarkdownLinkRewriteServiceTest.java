package com.platform.markdown.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownLinkRewriteServiceTest {

    private MarkdownLinkRewriteService service;

    @BeforeEach
    void setUp() {
        service = new MarkdownLinkRewriteService();
    }

    @Test
    void rewriteLinks_rewritesMdLinkInSameDirectory() {
        String html = "<a href=\"./past-simple.md\">Past Simple</a>";
        String result = service.rewriteLinks(html, "english-teaching", "grammar/tenses.md");

        assertThat(result).contains("/markdown/source/english-teaching/view/grammar/past-simple.md");
        assertThat(result).contains(">Past Simple<");
    }

    @Test
    void rewriteLinks_rewritesMdLinkToParentDirectory() {
        String html = "<a href=\"../README.md\">Home</a>";
        String result = service.rewriteLinks(html, "english-teaching", "grammar/tenses.md");

        assertThat(result).contains("/markdown/source/english-teaching/view/README.md");
    }

    @Test
    void rewriteLinks_preservesFragment() {
        String html = "<a href=\"./past-simple.md#examples\">Examples</a>";
        String result = service.rewriteLinks(html, "english-teaching", "grammar/tenses.md");

        assertThat(result).contains("/markdown/source/english-teaching/view/grammar/past-simple.md#examples");
    }

    @Test
    void rewriteLinks_rewritesImageSrcToRawEndpoint() {
        String html = "<img src=\"./images/diagram.png\" alt=\"Diagram\">";
        String result = service.rewriteLinks(html, "english-teaching", "grammar/tenses.md");

        assertThat(result).contains("/markdown/source/english-teaching/raw/grammar/images/diagram.png");
    }

    @Test
    void rewriteLinks_doesNotRewriteExternalHttpLinks() {
        String html = "<a href=\"https://example.com\">External</a>";
        String result = service.rewriteLinks(html, "english-teaching", "grammar/tenses.md");

        assertThat(result).contains("https://example.com");
    }

    @Test
    void rewriteLinks_doesNotRewriteExternalImages() {
        String html = "<img src=\"https://example.com/image.png\">";
        String result = service.rewriteLinks(html, "english-teaching", "grammar/tenses.md");

        assertThat(result).contains("https://example.com/image.png");
    }

    @Test
    void rewriteLinks_doesNotRewritePureFragmentLinks() {
        String html = "<a href=\"#section\">Jump</a>";
        String result = service.rewriteLinks(html, "english-teaching", "README.md");

        assertThat(result).contains("href=\"#section\"");
    }

    @Test
    void rewriteLinks_rewritesLinkFromRootFile() {
        String html = "<a href=\"./grammar/tenses.md\">Tenses</a>";
        String result = service.rewriteLinks(html, "english-teaching", "README.md");

        assertThat(result).contains("/markdown/source/english-teaching/view/grammar/tenses.md");
    }

    @Test
    void resolveRelative_normalizesDoubleDotSegments() {
        String result = service.resolveRelative("grammar/advanced", "../../README.md");

        assertThat(result).isEqualTo("README.md");
    }

    @Test
    void resolveRelative_resolvesRelativeFromEmptyDir() {
        String result = service.resolveRelative("", "./notes.md");

        assertThat(result).isEqualTo("notes.md");
    }
}
