package com.github.jbescos.newsanalyzer;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

public class ForoCoches implements NewsExtractor {

    private static final String URL = "https://forocoches.com/foro/showthread.php";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.of("es", "ES"));
    private static final String COOKIE = "XXXXX";
    private final Client client;

    public ForoCoches(Client client) {
        this.client = client;
    }

    @Override
    public List<Info> collect(int page) {
        Response response = client.target(URL).queryParam("t", page).request().header("Cookie", COOKIE).get();
        String content = response.readEntity(String.class);
        if (response.getStatus() == 200) {
            String url = URL + "?t=" + page;
            System.out.println(url);
            try {
                String encodedUrl = URLEncoder.encode(url);
                List<Info> info = parse(content, page, encodedUrl);
                return info;
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot parse " + url + "\n" + content, e);
            }
        } else {
            throw new IllegalStateException("Cannot fetch page " + page + ". Response code is " + response.getStatus()
                    + " and content:\n" + content);
        }
    }

    private List<Info> parse(String html, int page, String url) {
        Document document = Jsoup.parse(html, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.html);
        document.outputSettings().escapeMode(EscapeMode.xhtml);
        
        String title = document.getElementsByTag("h1").iterator().next().text();
        Elements firstPosts = document.select("[id^=post]");
        for (Element firstPost : firstPosts) {
            if (firstPost.id().matches("^post\\d+$")) {
                String author = firstPost.child(1).child(0).child(1).child(0).child(0).text();
                String date = firstPost.getElementsByAttributeValue("class", "postdate old").iterator().next().text().replace("\u00A0", " ").trim();
                String content = firstPost.child(3).child(0).child(0).child(0).child(0).text();
                LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_FORMATTER);
                Date dateD = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                Info info = new Info(author, dateD, title, content, url);
                return List.of(info);
            }
        }
        return List.of();
    }

}
