package ru.mail.kievsan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int cores = Runtime.getRuntime().availableProcessors();
        int textLinesNumber = 25;

        String[] texts = new String[textLinesNumber];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        // Создаём пул потоков
        final ExecutorService threadPool = Executors.newFixedThreadPool(cores);
        List<Future<Integer>> futureList = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {
            // Создаём задачу с результатом типа String
            Callable<Integer> myCallable = getMaxSize(text);

            // Отправляем задачу на выполнение в пул потоков
            Future<Integer> task = threadPool.submit(myCallable);
            futureList.add(task);   // и сохраняем в список
        }

        int max = 0;
        for (Future<Integer> task : futureList) {
            int resultOfTask = task.get();   // Получаем результат
            System.out.print(resultOfTask + "\t");
            if (resultOfTask > max) {
                max = resultOfTask;
            }
        }
        System.out.println("\nMAX = " + max);

        // Завершаем работу пула потоков
        threadPool.shutdown();

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms" + ", Cores: " + cores);
    }

    public static Callable<Integer> getMaxSize(String text) {
        return () -> {
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            return maxSize;
        };

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
