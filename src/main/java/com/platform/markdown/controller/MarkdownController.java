package com.platform.markdown.controller;

import com.platform.markdown.config.MarkdownHtmlConfig;
import com.platform.markdown.config.MarkdownSourceConfig;
import com.platform.markdown.config.MarkdownViewerProperties;
import com.platform.markdown.dto.MarkdownPageDto;
import com.platform.markdown.dto.MarkdownSourceDto;
import com.platform.markdown.dto.MarkdownTreeNodeDto;
import com.platform.markdown.service.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/markdown")
public class MarkdownController {

    private static final Map<String, MediaType> MEDIA_TYPES = Map.of(
            ".png", MediaType.IMAGE_PNG,
            ".jpg", MediaType.IMAGE_JPEG,
            ".jpeg", MediaType.IMAGE_JPEG,
            ".gif", MediaType.IMAGE_GIF,
            ".svg", MediaType.valueOf("image/svg+xml"),
            ".css", MediaType.valueOf("text/css"),
            ".webp", MediaType.valueOf("image/webp")
    );

    private final MarkdownViewerProperties properties;
    private final MarkdownSourceService sourceService;
    private final MarkdownPathSecurityService securityService;
    private final MarkdownRenderService renderService;
    private final MarkdownLinkRewriteService linkRewriteService;
    private final MarkdownTableWrapService tableWrapService;
    private final MarkdownFileTreeService fileTreeService;

    public MarkdownController(MarkdownViewerProperties properties,
                               MarkdownSourceService sourceService,
                               MarkdownPathSecurityService securityService,
                               MarkdownRenderService renderService,
                               MarkdownLinkRewriteService linkRewriteService,
                               MarkdownTableWrapService tableWrapService,
                               MarkdownFileTreeService fileTreeService) {
        this.properties = properties;
        this.sourceService = sourceService;
        this.securityService = securityService;
        this.renderService = renderService;
        this.linkRewriteService = linkRewriteService;
        this.tableWrapService = tableWrapService;
        this.fileTreeService = fileTreeService;
    }

    @GetMapping
    public String home(Model model) {
        List<MarkdownSourceDto> sources = sourceService.findAll().stream()
                .map(s -> new MarkdownSourceDto(s.getId(), s.getName(),
                        "/markdown/source/" + s.getId()))
                .toList();
        model.addAttribute("sources", sources);
        model.addAttribute("htmlConfig", properties.getHtml());
        return "markdown/home";
    }

    @GetMapping("/source/{sourceId}")
    public String sourceIndex(@PathVariable String sourceId, Model model) throws IOException {
        MarkdownSourceConfig source = findSourceOrThrow(sourceId);
        String indexFile = Optional.ofNullable(source.getIndexFile()).orElse("README.md");
        return renderMarkdownPage(source, indexFile, model);
    }

    @GetMapping("/source/{sourceId}/view/{*relativePath}")
    public String view(@PathVariable String sourceId,
                       @PathVariable String relativePath,
                       Model model) throws IOException {
        MarkdownSourceConfig source = findSourceOrThrow(sourceId);
        return renderMarkdownPage(source, stripLeadingSlash(relativePath), model);
    }

    @GetMapping("/source/{sourceId}/raw/{*relativePath}")
    public ResponseEntity<Resource> raw(@PathVariable String sourceId,
                                        @PathVariable String relativePath) {
        MarkdownSourceConfig source = findSourceOrThrow(sourceId);
        Path root = Path.of(source.getRoot());
        Path filePath = resolveSafely(root, stripLeadingSlash(relativePath));
        MediaType mediaType = detectMediaType(filePath.getFileName().toString());
        return ResponseEntity.ok().contentType(mediaType).body(new FileSystemResource(filePath));
    }

    private String renderMarkdownPage(MarkdownSourceConfig source, String relativePath, Model model) throws IOException {
        Path root = Path.of(source.getRoot());
        Path filePath = resolveSafely(root, relativePath);
        if (Files.isDirectory(filePath)) {
            filePath = resolveDirectoryIndex(filePath);
        }
        String currentPath = root.relativize(filePath).toString();
        String markdown = Files.readString(filePath);
        String rawHtml = renderService.render(markdown);
        String html = linkRewriteService.rewriteLinks(rawHtml, source.getId(), relativePath);
        html = tableWrapService.wrapTables(html);
        List<MarkdownTreeNodeDto> tree = fileTreeService.buildTree(root, source.getId());
        MarkdownHtmlConfig htmlConfig = properties.getHtml();
        MarkdownPageDto page = new MarkdownPageDto(
                source.getId(), source.getName(), extractTitle(markdown),
                html, tree, currentPath, htmlConfig.getTitle(), normalizeCssPaths(htmlConfig.getCss()),
                readFile(htmlConfig.getInjectHead()), readFile(htmlConfig.getInjectBodyEnd()));
        model.addAttribute("page", page);
        return "markdown/view";
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid path");
    }

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<String> handleNotFound(NoSuchFileException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + e.getFile());
    }

    private Path resolveDirectoryIndex(Path dir) {
        for (String candidate : List.of("README.md", "INDEX.md", "index.md")) {
            Path index = dir.resolve(candidate);
            if (Files.exists(index)) return index;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No index file found in directory: " + dir.getFileName());
    }

    private MarkdownSourceConfig findSourceOrThrow(String sourceId) {
        return sourceService.findById(sourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Source not found: " + sourceId));
    }

    private Path resolveSafely(Path root, String relativePath) {
        try {
            return securityService.resolveSafePath(root, relativePath);
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path");
        }
    }

    private String extractTitle(String markdown) {
        return markdown.lines()
                .filter(l -> l.startsWith("# "))
                .findFirst()
                .map(l -> l.substring(2).trim())
                .orElse("Untitled");
    }

    private String readFile(String path) {
        if (path == null) return null;
        try { return Files.readString(Path.of(path)); }
        catch (IOException e) { return null; }
    }

    private List<String> normalizeCssPaths(List<String> cssPaths) {
        if (cssPaths == null) return List.of();
        return cssPaths.stream()
                .map(p -> p.startsWith("./") ? "/" + p.substring(2) : p)
                .toList();
    }

    private MediaType detectMediaType(String filename) {
        return MEDIA_TYPES.entrySet().stream()
                .filter(e -> filename.endsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    private String stripLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }
}
