package org.example;

import java.nio.file.*;


public class FileWatcher {
    public static void main(String[] args) throws Exception {
        // Получаем объект WatchService
        WatchService watcher = FileSystems.getDefault().newWatchService();

        boolean isProcessingEvent = false;

        ComtradeParser comParser = new ComtradeParser();

        // Регистрируем изменения в директории
        Path dir = Paths.get("E:\\DZ\\11sem\\AI_Enregy\\KP\\pythonProject\\PSCAD_files\\testGrid.gf42\\Rank_00001\\Run_00001");
        dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

        // Бесконечный цикл для мониторинга событий
        while (true) {
            WatchKey key;
            try {
                // Ожидаем получение событий
                key = watcher.take();
//                Thread.sleep( 50 );
            } catch (InterruptedException ex) {
                return;
            }

//            Thread.sleep( 50 );
            // Обрабатываем события
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                Thread.sleep( 150 );
                // Если произошло изменение файла
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    // Проверяем, что изменение произошло именно в нужном файле
                    if (filename.toString().equals("ABC_W1.dat")) {
                        isProcessingEvent = true;
                        System.out.println("File was modified: " + filename);
                        comParser.CreateCSV();
                        isProcessingEvent = false;


                    }
                }
            }

            // Сбрасываем ключ, чтобы можно было продолжать получать события
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}