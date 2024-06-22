package com.github.jbescos.newsanalyzer;

import java.util.Date;
import java.util.List;

public interface NewsExtractor {

    public List<Info> collect(int page);
    
    public static class Info {

        private final String author;
        private final Date date;
        private final String title;

        public Info(String author, Date date, String title) {
            this.author = author;
            this.date = date;
            this.title = title;
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

    }
    
}
