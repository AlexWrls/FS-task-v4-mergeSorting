import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class WorkerSort implements Closeable {
   private final Logger log = Logger.getGlobal();
   private final Param param = Param.getParam();

    private List<BlockingDeque<String>> deques = new CopyOnWriteArrayList<>();
    private List<ReadLine> readLines = new ArrayList<>();
    private Sort sort;
    private Mediator mediator;

    public WorkerSort(Sort sort, Mediator mediator) {
        this.sort = sort;
        this.mediator = mediator;
    }

    public void doSort(){
        for (String nameFile:param.getNameInFile()){
            ReadLine reader = new ReadLine(nameFile,mediator);
            if (!reader.isFailed()) {
                readLines.add(reader);
            }
        }
        if (readLines.size() == 0){
            log.warning("Входные файлы отсутствуют.");
            System.exit(13);
        } else {
            for (ReadLine readLine:readLines){
                BlockingDeque<String> deque = readLine.startRead();
                if (deque != null){
                    deques.add(deque);
                }
            }
        }

        sort.sortData(deques);
    }

    @Override
    public void close()  {
        for (ReadLine readLine:readLines){
            readLine.close();
        }
    }

}
