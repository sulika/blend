package com.platform.markdown.dto;

import java.util.List;

public record MarkdownPageDto(
        String sourceId,
        String sourceName,
        String pageTitle,
        String htmlContent,
        List<MarkdownTreeNodeDto> fileTree,
        String currentPath,
        String htmlTitle,
        List<String> cssFiles,
        String injectHead,
        String injectBodyEnd) {}
