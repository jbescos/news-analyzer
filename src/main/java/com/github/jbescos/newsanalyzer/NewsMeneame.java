package com.github.jbescos.newsanalyzer;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

public class NewsMeneame implements NewsExtractor {

    private static final String URL = "https://old.meneame.net";
    private final Client client;

    public NewsMeneame(Client client) {
        this.client = client;
    }

    @Override
    public List<Info> collect(int page) {
        Response response = client.target(URL).queryParam("page", page).request().get();
        String content = response.readEntity(String.class);
        if (response.getStatus() == 200) {
            List<Info> info = parse(content);
            return info;
        } else {
            throw new IllegalStateException("Cannot fetch page " + page + ". Response code is " + response.getStatus()
                    + " and content:\n" + content);
        }
    }

    private List<Info> parse(String html) {
        Document document = Jsoup.parse(html, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        document.outputSettings().escapeMode(EscapeMode.xhtml);
        Elements news = document.getElementsByAttributeValue("class", "news-body");
        List<Info> infos = new ArrayList<>();
        for (Element elementNews : news) {
            Element content = getCenterContent(elementNews);
            try {
                Element title = content.child(0).child(0);
                String titleStr = title.text();
                String url = URLEncoder.encode(title.attr("href"), "UTF-8");
                Element metadata = content.child(1);
                String author = metadata.child(1).text();
                Long ts = Long.parseLong(metadata.child(3).attr("data-ts")) * 1000;
                Info info = new Info(author, new Date(ts), titleStr, url);
                infos.add(info);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot parse html\n" + elementNews + "\nContent\n:" + content, e);
            }
        }
        return infos;
    }

    private Element getCenterContent(Element elementNews) {
        Elements childs = elementNews.children();
        for (Element child : childs) {
            if ("div".equals(child.tagName())) {
                String attr = child.attr("class");
                if (attr.startsWith("center-content")) {
                    return child;
                }
            }
        }
        throw new IllegalArgumentException("center-content was not found in " + elementNews);
    }
    
}
