package com.platform.markdown.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarkdownPathSecurityServiceTest {

    private MarkdownPathSecurityService service;
    private Path root;

    @BeforeEach
    void setUp() {
        service = new MarkdownPathSecurityService();
        root = Path.of("/tmp/markdown-root");
    }

    @Test
    void resolveSafePath_resolvesValidRelativePath() {
        Path result = service.resolveSafePath(root, "grammar/tenses.md");

        assertThat(result).isEqualTo(root.toAbsolutePath().normalize().resolve("grammar/tenses.md"));
    }

    @Test
    void resolveSafePath_resolvesFileInRoot() {
        Path result = service.resolveSafePath(root, "README.md");

        assertThat(result).isEqualTo(root.toAbsolutePath().normalize().resolve("README.md"));
    }

    @Test
    void resolveSafePath_throwsOnParentTraversal() {
        assertThatThrownBy(() -> service.resolveSafePath(root, "../etc/passwd"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Path traversal");
    }

    @Test
    void resolveSafePath_throwsOnAbsolutePathEscape() {
        assertThatThrownBy(() -> service.resolveSafePath(root, "/etc/passwd"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void resolveSafePath_throwsOnEncodedTraversal() {
        assertThatThrownBy(() -> service.resolveSafePath(root, "grammar/../../etc/passwd"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void isSafe_returnsTrueForValidPath() {
        assertThat(service.isSafe(root, "docs/notes.md")).isTrue();
    }

    @Test
    void isSafe_returnsFalseForTraversalPath() {
        assertThat(service.isSafe(root, "../outside.md")).isFalse();
    }
}
