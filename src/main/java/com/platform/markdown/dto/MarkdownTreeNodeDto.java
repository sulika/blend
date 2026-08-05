package com.platform.markdown.dto;

import java.util.List;

public record MarkdownTreeNodeDto(
        String name,
        String path,
        String url,
        boolean directory,
        List<MarkdownTreeNodeDto> children) {}
