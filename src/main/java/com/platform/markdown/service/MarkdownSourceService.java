package com.platform.markdown.service;

import com.platform.markdown.config.MarkdownSourceConfig;
import com.platform.markdown.config.MarkdownViewerProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MarkdownSourceService {

    private final MarkdownViewerProperties properties;

    public MarkdownSourceService(MarkdownViewerProperties properties) {
        this.properties = properties;
    }

    public Optional<MarkdownSourceConfig> findById(String id) {
        return properties.getSources().stream()
                .filter(s -> Objects.equals(id, s.getId()))
                .findFirst();
    }

    public List<MarkdownSourceConfig> findAll() {
        return properties.getSources();
    }
}
