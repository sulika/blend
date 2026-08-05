package com.platform.markdown.service;

import com.platform.markdown.dto.MarkdownTreeNodeDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MarkdownFileTreeService {

    public List<MarkdownTreeNodeDto> buildTree(Path root, String sourceId) throws IOException {
        return buildNodes(root.toAbsolutePath().normalize(), root.toAbsolutePath().normalize(), sourceId);
    }

    private List<MarkdownTreeNodeDto> buildNodes(Path root, Path dir, String sourceId) throws IOException {
        List<MarkdownTreeNodeDto> nodes = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> entries = stream
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
            for (Path entry : entries) {
                nodes.add(buildNode(root, entry, sourceId));
            }
        }
        return nodes;
    }

    private MarkdownTreeNodeDto buildNode(Path root, Path entry, String sourceId) throws IOException {
        String name = entry.getFileName().toString();
        String relativePath = root.relativize(entry).toString();
        boolean isDir = Files.isDirectory(entry);

        if (isDir) {
            List<MarkdownTreeNodeDto> children = buildNodes(root, entry, sourceId);
            return new MarkdownTreeNodeDto(name, relativePath, null, true, children);
        }

        String url = name.endsWith(".md")
                ? "/markdown/source/" + sourceId + "/view/" + relativePath
                : null;
        return new MarkdownTreeNodeDto(name, relativePath, url, false, List.of());
    }
}
