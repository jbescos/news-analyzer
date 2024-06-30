package com.github.jbescos.newsanalyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import com.github.jbescos.newsanalyzer.NewsExtractor.Info;
import com.opencsv.CSVWriter;

public class NewsCollectorProcess {

    private static final String FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    private final NewsExtractor extractor;
    private final File file;
    private final int fromPage;
    
    public NewsCollectorProcess(NewsExtractor extractor, File file, int fromPage) {
        this.extractor = extractor;
        this.file = file;
        this.fromPage = fromPage;
    }

    public void populate(Predicate<Date> predicate) {
        Date lastProcessed = null;
        int page = fromPage;
        try (FileOutputStream fos = new FileOutputStream(file); 
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                CSVWriter writer = new CSVWriter(osw);){
            do {
                List<Info> info = extractor.collect(page);
                for (Info i : info) {
                    lastProcessed = i.getDate();
                    writer.writeNext(new String[] {new SimpleDateFormat(FORMAT_SECOND).format(i.getDate()), i.getAuthor(), i.getTitle(), i.getContent(), i.getUrl()});
                }
                page++;
            } while (predicate.test(lastProcessed));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot persist in " + file, e);
        }
    }

    public static class TillDateDesc implements Predicate<Date> {

        private final Date tillDate;
        
        public TillDateDesc(Date tillDate) {
            this.tillDate = tillDate;
        }
        
        @Override
        public boolean test(Date date) {
            return date == null || date.getTime() > tillDate.getTime();
        }
        
    }

    public static class TillDateAsc implements Predicate<Date> {

        private final Date tillDate;
        
        public TillDateAsc(Date tillDate) {
            this.tillDate = tillDate;
        }
        
        @Override
        public boolean test(Date date) {
            return date == null || date.getTime() < tillDate.getTime();
        }
        
    }
}
