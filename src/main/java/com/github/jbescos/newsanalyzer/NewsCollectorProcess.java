package com.github.jbescos.newsanalyzer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import com.github.jbescos.newsanalyzer.NewsExtractor.Info;
import com.opencsv.CSVWriter;

public class NewsCollectorProcess {

    private static final String FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    private static final int THREADS = 20;
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
        final AtomicInteger page = new AtomicInteger(fromPage);
        try (FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
                CSVWriter writer = new CSVWriter(osw);
                ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();){
            do {
                List<Future<List<Info>>> futures = new ArrayList<>();
                for (int i = 0; i < THREADS; i++) {
                    futures.add(executor.submit(() -> extractor.collect(page.getAndIncrement())));
                }
                List<Info> info = new ArrayList<>();
                for (Future<List<Info>> future : futures) {
                    try {
                        info.addAll(future.get());
                    } catch (InterruptedException | ExecutionException e) {}
                }
                Collections.sort(info, (a1, a2) -> a1.getDate().compareTo(a2.getDate()));
                for (Info i : info) {
                    lastProcessed = i.getDate();
                    writer.writeNext(new String[] {new SimpleDateFormat(FORMAT_SECOND).format(i.getDate()), i.getAuthor(), i.getTitle(), i.getContent(), i.getUrl()});
                }
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
