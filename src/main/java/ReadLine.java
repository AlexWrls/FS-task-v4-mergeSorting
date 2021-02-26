import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class ReadLine implements Runnable, Closeable {
    Logger log = Logger.getGlobal();

    private String nameFile;
    private FileInputStream inputStream;
    private Scanner scanner;
    private BlockingDeque<String> deque;
    private ExecutorService service;
    private boolean failed;
    private Mediator mediator;

    public ReadLine(String nameFile, Mediator mediator) {
        this.nameFile = nameFile;
        this.mediator = mediator;
        try {
            inputStream = new FileInputStream(nameFile);
        } catch (FileNotFoundException e) {
            log.warning(String.format("Ошибка %s во входном файле %s",e.getMessage(),nameFile));
            failed = true;
            return;
        }
        scanner = new Scanner(inputStream);
        deque = new LinkedBlockingDeque<>(2);
        service = Executors.newSingleThreadExecutor();
    }

    public boolean isFailed() {
        return failed;
    }
    public BlockingDeque startRead(){
        if (!failed){
            service.submit(this);
            return deque;
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null) continue;
                String value = line.replaceAll("\\s+", "");
                try {
                    deque.putLast(value);
                } catch (InterruptedException e) {
                    log.warning(String.format("Ошибка %s, при чтении файла %s", e.getMessage(), nameFile));
                }
            }
            if (scanner.ioException() != null) {
                log.warning(String.format("Ошибка %s, при чтении файла %s", scanner.ioException().getMessage(), nameFile));
            }
        }finally {
            close();
        }
    }

    @Override
    public void close(){
                if (scanner != null) scanner.close();
        if (inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warning(String.format("Ошибка %s закрытия потока чтения",e.getMessage()));
            }
        }
        mediator.notifyDeque(deque);
        service.shutdownNow();
    }

}
