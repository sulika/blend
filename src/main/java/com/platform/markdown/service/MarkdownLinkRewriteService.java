package com.platform.markdown.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class MarkdownLinkRewriteService {

    private static final String VIEW_BASE = "/markdown/source/%s/view/";
    private static final String RAW_BASE = "/markdown/source/%s/raw/";

    public String rewriteLinks(String html, String sourceId, String currentRelativePath) {
        Document doc = Jsoup.parseBodyFragment(html);
        String currentDir = parentDir(currentRelativePath);
        rewriteAnchors(doc, sourceId, currentDir);
        rewriteImages(doc, sourceId, currentDir);
        return doc.body().html();
    }

    private void rewriteAnchors(Document doc, String sourceId, String currentDir) {
        for (Element a : doc.select("a[href]")) {
            String href = a.attr("href");
            if (isLocalMdLink(href)) {
                a.attr("href", buildViewUrl(href, sourceId, currentDir));
            }
        }
    }

    private void rewriteImages(Document doc, String sourceId, String currentDir) {
        for (Element img : doc.select("img[src]")) {
            String src = img.attr("src");
            if (isLocalAssetLink(src)) {
                String resolved = resolveRelative(currentDir, src);
                img.attr("src", String.format(RAW_BASE, sourceId) + resolved);
            }
        }
    }

    private String buildViewUrl(String href, String sourceId, String currentDir) {
        String[] parts = href.split("#", 2);
        String resolved = resolveRelative(currentDir, parts[0]);
        String fragment = parts.length > 1 ? "#" + parts[1] : "";
        return String.format(VIEW_BASE, sourceId) + resolved + fragment;
    }

    String resolveRelative(String currentDir, String relativePath) {
        if (relativePath.startsWith("/")) {
            return relativePath.substring(1);
        }
        String base = currentDir.isEmpty() ? "" : currentDir + "/";
        return Path.of(base + relativePath).normalize().toString();
    }

    private boolean isLocalMdLink(String href) {
        if (href.startsWith("#") || isAbsoluteUrl(href)) return false;
        return href.split("#")[0].endsWith(".md");
    }

    private boolean isLocalAssetLink(String src) {
        return !isAbsoluteUrl(src) && !src.startsWith("data:");
    }

    private boolean isAbsoluteUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("//");
    }

    private String parentDir(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(0, lastSlash) : "";
    }
}
