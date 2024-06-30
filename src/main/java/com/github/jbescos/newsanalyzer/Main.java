package com.github.jbescos.newsanalyzer;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.jbescos.newsanalyzer.NewsCollectorProcess.TillDateDesc;
import com.github.jbescos.newsanalyzer.NewsCollectorProcess.TillDateAsc;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class Main {

    private static final TillDateDesc TILL_DATE_DESC;
    private static final TillDateAsc TILL_DATE_ASC;
    
    static {
        try {
            Date tillDateDesc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-01-01 00:00:00");
            TILL_DATE_DESC = new TillDateDesc(tillDateDesc);
            TILL_DATE_ASC = new TillDateAsc(new Date());
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static void main(String[] args) {
        try (Client client = ClientBuilder.newClient()) {
//            meneame(client);
            forocoches(client);
        }
    }

    private static void meneame(Client client) {
        NewsCollectorProcess collector = new NewsCollectorProcess(new NewsMeneame(client), new File("C:\\Users\\jorge\\Downloads\\meneame.csv"), 0);
        collector.populate(TILL_DATE_DESC);
    }

    private static void forocoches(Client client) {
        NewsCollectorProcess collector = new NewsCollectorProcess(new ForoCoches(client), new File("C:\\Users\\jorge\\Downloads\\forocoches.csv"), 9789264);
        collector.populate(TILL_DATE_ASC);
    }
}
