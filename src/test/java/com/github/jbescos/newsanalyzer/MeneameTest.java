package com.github.jbescos.newsanalyzer;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.github.jbescos.newsanalyzer.NewsExtractor.Info;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class MeneameTest {

    @Test
    @Ignore
    public void test() {
        try (Client client = ClientBuilder.newClient()) {
            NewsMeneame meneame = new NewsMeneame(client);
            List<Info> info = meneame.collect(0);
            System.out.println(info);
        }
    }
}
