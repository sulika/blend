package com.platform.markdown.service;

import com.platform.markdown.dto.MarkdownTreeNodeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownFileTreeServiceTest {

    @TempDir
    Path tempDir;

    private MarkdownFileTreeService service;

    @BeforeEach
    void setUp() {
        service = new MarkdownFileTreeService();
    }

    @Test
    void buildTree_includesMdFilesWithUrl() throws IOException {
        Files.createFile(tempDir.resolve("README.md"));

        List<MarkdownTreeNodeDto> tree = service.buildTree(tempDir, "test-source");

        MarkdownTreeNodeDto node = tree.stream()
                .filter(n -> n.name().equals("README.md"))
                .findFirst()
                .orElseThrow();
        assertThat(node.url()).isEqualTo("/markdown/source/test-source/view/README.md");
        assertThat(node.directory()).isFalse();
    }

    @Test
    void buildTree_includesNonMdFilesWithoutUrl() throws IOException {
        Files.createFile(tempDir.resolve("diagram.png"));

        List<MarkdownTreeNodeDto> tree = service.buildTree(tempDir, "test-source");

        MarkdownTreeNodeDto node = tree.stream()
                .filter(n -> n.name().equals("diagram.png"))
                .findFirst()
                .orElseThrow();
        assertThat(node.url()).isNull();
        assertThat(node.directory()).isFalse();
    }

    @Test
    void buildTree_buildsNestedDirectoryStructure() throws IOException {
        Path grammar = Files.createDirectory(tempDir.resolve("grammar"));
        Files.createFile(grammar.resolve("tenses.md"));

        List<MarkdownTreeNodeDto> tree = service.buildTree(tempDir, "test-source");

        MarkdownTreeNodeDto dir = tree.stream()
                .filter(MarkdownTreeNodeDto::directory)
                .findFirst()
                .orElseThrow();
        assertThat(dir.name()).isEqualTo("grammar");
        assertThat(dir.url()).isNull();
        assertThat(dir.children()).hasSize(1);
        assertThat(dir.children().get(0).name()).isEqualTo("tenses.md");
        assertThat(dir.children().get(0).url())
                .isEqualTo("/markdown/source/test-source/view/grammar/tenses.md");
    }

    @Test
    void buildTree_excludesHiddenFiles() throws IOException {
        Files.createFile(tempDir.resolve(".DS_Store"));
        Files.createFile(tempDir.resolve("visible.md"));

        List<MarkdownTreeNodeDto> tree = service.buildTree(tempDir, "test-source");

        assertThat(tree).noneMatch(n -> n.name().startsWith("."));
        assertThat(tree).anyMatch(n -> n.name().equals("visible.md"));
    }

    @Test
    void buildTree_sortsEntriesAlphabetically() throws IOException {
        Files.createFile(tempDir.resolve("z-last.md"));
        Files.createFile(tempDir.resolve("a-first.md"));
        Files.createFile(tempDir.resolve("m-middle.md"));

        List<MarkdownTreeNodeDto> tree = service.buildTree(tempDir, "test-source");

        assertThat(tree).extracting(MarkdownTreeNodeDto::name)
                .containsExactly("a-first.md", "m-middle.md", "z-last.md");
    }
}
