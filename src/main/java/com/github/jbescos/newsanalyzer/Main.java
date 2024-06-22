package com.github.jbescos.newsanalyzer;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class Main {

    public static void main(String[] args) throws ParseException {
        try (Client client = ClientBuilder.newClient()) {
            NewsCollectorProcess collector = new NewsCollectorProcess(new NewsMeneame(client), new File("C:\\Users\\jorge\\Downloads\\meneame.csv"));
            collector.populate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-01-01 00:00:00"));
        }
    }

}
