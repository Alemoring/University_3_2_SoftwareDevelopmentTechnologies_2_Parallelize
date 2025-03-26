import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int N = 10;
    private static final int NUMBERS_COUNT = 100;

    public static void main(String[] args) {
        // Первый буфер для чисел от 100000 до 1000 с шагом 1000
        BlockingQueue<Integer> firstBuffer = new LinkedBlockingQueue<>(N);
        // Второй буфер для результатов x/1000
        BlockingQueue<Integer> secondBuffer = new LinkedBlockingQueue<>(N);

        // Поток 1: Генерация чисел и помещение в первый буфер
        Thread firstThread = new Thread(() -> {
            for (int i = 0; i < NUMBERS_COUNT; i++) {
                int number = 100000 - i * 1000;
                try {
                    firstBuffer.put(number);
                    synchronized (System.out) {
                        System.out.println("Первый поток добавил к первому буферу: " + number);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Первый поток поток завершился с ошибкой");
                    return;
                }
            }
            System.out.println("Первый поток завершил генерацию чисел.");
        });

        // Поток 2: Извлечение чисел из первого буфера, вычисление x/1000 и помещение во второй буфер
        Thread secondTread = new Thread(() -> {
            for (int i = 0; i < NUMBERS_COUNT; i++) {
                try {
                    int x = firstBuffer.take();
                    int result = x / 1000;
                    synchronized (System.out) {
                        System.out.println("Второй поток получил: " + x + " -> " + result);
                    }
                    secondBuffer.put(result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Второй поток завершил работу с ошибкой");
                    return;
                }
            }
            System.out.println("Второй поток окончил работу с числами.");
        });

        // Поток 3: Извлечение чисел из второго буфера, сортировка и вывод в порядке убывания
        Thread thirdThread = new Thread(() -> {
            for (int i = 0; i < NUMBERS_COUNT; i++) {
                try {
                    int max = Integer.MIN_VALUE;
                    int number = secondBuffer.take();
                    if (number > max) {
                        max = number;
                        synchronized (System.out) {
                            System.out.println("Наибольшее число в буфере: " + max);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Третий поток завершился с ошибкой");
                    return;
                }
            }
            System.out.println("Третий поток завершил работу");
        });

        // Запуск всех потоков
        firstThread.start();
        secondTread.start();
        thirdThread.start();

        // Ожидание завершения всех потоков
        try {
            firstThread.join();
            secondTread.join();
            thirdThread.join();
        } catch (InterruptedException e) {
            System.err.println("Главный поток завершился с ошибкой, пока ждал остальные потоки");
        }

        System.out.println("Все потоки завершили работу.");
    }
}