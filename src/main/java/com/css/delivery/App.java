package com.css.delivery;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.SneakyThrows;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    @Getter
    private static final ExecutorService executor = Executors.newCachedThreadPool(Thread::new);
    @Getter
    private static final Queue<Delivery> deliveries = new ConcurrentLinkedQueue<>();

    @SneakyThrows
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("Delivery Service").build().defaultHelp(true)
                .description("Simulate order deliveries and print statistics");
        parser.addArgument("-s", "--strategy").choices("MATCHED", "FIFO").required(true)
                .help("Dispatching strategy to use. Possible values are MATCHED(a courier may only pick up a specific order) and FIFO(a courier picks up the next available order upon arrival)");
        parser.addArgument("-r", "--rate").type(Integer.class).setDefault(2)
                .help("Number of orders dispatched per second");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            parser.printHelp();
            System.exit(1);
        }

        Order[] orders;
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("dispatch_orders.json")) {
            if (inputStream == null) {
                logger.fatal("Cannot load order data! Exiting...");
                System.exit(1);
            }
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String json = scanner.next();
            orders = new Gson().fromJson(json, Order[].class);
            logger.info("{} orders were loaded", orders.length);
        }

        String strategy = ns.getString("strategy").toUpperCase();
        int rate = ns.getInt("rate");
        Coordinator coordinator = Strategy.valueOf(strategy).createCoordinator();
        logger.info("Start dispatching orders at {}/second", rate);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < orders.length; i++) {
            if (i % rate == 0) {
                Thread.sleep(1000);
            }
            futures.add(new Dispatcher(orders[i], coordinator).start());
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long totalOrderWaitTime = 0, totalCourierWaitTime = 0;
        for (Delivery delivery : deliveries) {
            totalOrderWaitTime += delivery.getOrderWaitTime();
            totalCourierWaitTime += delivery.getCourierWaitTime();
        }

        logger.info("Average food wait time is {} milliseconds between order ready and pickup",
                totalOrderWaitTime / deliveries.size());
        logger.info("Average courier wait time is {} milliseconds between arrival and order pickup",
                totalCourierWaitTime / deliveries.size());

        executor.shutdown();
    }
}
