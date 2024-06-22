package com.github.jbescos.newsanalyzer;

import java.util.Date;
import java.util.List;

public interface NewsExtractor {

    List<Info> collect(int page);
    
    public static class Info {

        private final String author;
        private final Date date;
        private final String title;
        private final String url;
        private final String content;

        public Info(String author, Date date, String title, String content, String url) {
            this.author = author;
            this.date = date;
            this.title = title;
            this.content = content;
            this.url = url;
        }

        public String getAuthor() {
            return author;
        }

        public Date getDate() {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getContent() {
            return content;
        }

    }
    
}
