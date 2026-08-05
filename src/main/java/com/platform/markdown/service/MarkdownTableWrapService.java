package com.platform.markdown.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class MarkdownTableWrapService {

    public String wrapTables(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings().prettyPrint(false);
        for (Element table : doc.select("table")) {
            table.wrap("<div class=\"table-wrapper\"></div>");
        }
        return doc.body().html();
    }
}
