package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static Thread textGen;

    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("calculation");

         textGen = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);

                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        textGen.start();


        new Thread(threadGroup, () -> {
            search(queueA, "a");
        }).start();

        new Thread(threadGroup, () -> {
            search(queueB, "b");
        }).start();

        new Thread(threadGroup, () -> {
            search(queueC, "c");

        }).start();

        while (!queueA.isEmpty() || !queueB.isEmpty() || !queueC.isEmpty() || textGen.isAlive()){

        }

        threadGroup.interrupt();


    }

    public static int letterCounter(String letter, String text) {
        byte bLet = letter.getBytes()[0];
        int counter = 0;
        for (byte s : text.getBytes()) {
            if (s == bLet) {
                counter++;
            }
        }
        return counter;
    }

    public static void search(ArrayBlockingQueue<String> queue, String letter){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int max = 0;
        String text = "";
        while (true) {
            try {
                String textV = queue.take();

                int var = letterCounter(letter, textV);
                if (var > max) {
                    max = var;
                    text = textV;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.printf("Наибольшее количество '%s' (%d) в: %s\n\n", letter, max, text);
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}