package com.platform.markdown.service;

import com.platform.markdown.config.MarkdownSourceConfig;
import com.platform.markdown.config.MarkdownViewerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownSourceServiceTest {

    private MarkdownSourceService service;

    private MarkdownSourceConfig english;
    private MarkdownSourceConfig architecture;

    @BeforeEach
    void setUp() {
        english = new MarkdownSourceConfig();
        english.setId("english-teaching");
        english.setName("English Teaching");
        english.setRoot("/docs/english");
        english.setIndexFile("README.md");

        architecture = new MarkdownSourceConfig();
        architecture.setId("architecture-notes");
        architecture.setName("Architecture Notes");
        architecture.setRoot("/docs/architecture");
        architecture.setIndexFile("index.md");

        MarkdownViewerProperties properties = new MarkdownViewerProperties();
        properties.setSources(List.of(english, architecture));
        service = new MarkdownSourceService(properties);
    }

    @Test
    void findById_returnsMatchingSource() {
        Optional<MarkdownSourceConfig> result = service.findById("english-teaching");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("english-teaching");
        assertThat(result.get().getName()).isEqualTo("English Teaching");
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        Optional<MarkdownSourceConfig> result = service.findById("unknown-source");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsEmptyForNullId() {
        Optional<MarkdownSourceConfig> result = service.findById(null);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllSources() {
        List<MarkdownSourceConfig> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(english, architecture);
    }
}
