package com.platform.markdown.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class MarkdownPathSecurityService {

    public Path resolveSafePath(Path root, String relativePath) {
        Path normalizedRoot = root.toAbsolutePath().normalize();
        Path resolved = normalizedRoot.resolve(relativePath).normalize();
        if (!resolved.startsWith(normalizedRoot)) {
            throw new SecurityException("Path traversal attempt detected: " + relativePath);
        }
        return resolved;
    }

    public boolean isSafe(Path root, String relativePath) {
        try {
            resolveSafePath(root, relativePath);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
