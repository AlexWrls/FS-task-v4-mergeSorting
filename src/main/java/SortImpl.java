import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;


public class SortImpl implements Sort, MediatorsSubscriber {
   private final Logger log = Logger.getGlobal();
   private final Param param = Param.getParam();

   private PrintWriter writer;
   private List<BlockingDeque<String>> dequeList;

    public SortImpl() {
    }

    public SortImpl (String nameOutFile){
       dequeList = new ArrayList<>();
       try {
           this.writer = new PrintWriter(nameOutFile);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           log.warning(String.format("Ошибка %s потока записи в файл %s  ",e.getMessage(),param.getNameOutFile()));
           System.exit(9);
       }
   }

    @Override
    public void addDeque(BlockingDeque<String> deque) {
                dequeList.add(deque);
    }

    @Override
    public void sortData(List<BlockingDeque<String>> deques) {
        while (true){
            for (BlockingDeque<String> deque:deques){
                if (dequeList.contains(deque) && deque.size()==0){
                    deques.remove(deque);
                    dequeList.remove(deque);
                }
            }

             if (deques.size() == 0) break;
             BlockingDeque<String> thisDeque = null;
             try {
             thisDeque = getThisDeque(deques);
             if (failedSortOrder(thisDeque)){
                 deques.remove(thisDeque);

                 log.warning("Нарушена сортировка входного файла. Файл исключен");
                 continue;
                }
             }catch (NumberFormatException e){
                 deques.remove(thisDeque);
                 log.warning(e.getMessage());
                 continue;
             } catch (IllegalArgumentException e){
                 continue;
             }
             try{
                 if (thisDeque.size() > 0) writer.println(thisDeque.takeFirst());
             } catch (InterruptedException e) {
                 log.warning(String.format("Ошибка при получении значения очереди: %s",e.getMessage()));
             }
        }
    }
    private boolean failedSortOrder(BlockingDeque<String> deque){
        if (deque.peekFirst() == null || deque.peekLast() == null) return false;
        if (param.isStr()){
            if (param.isAscend()){
                return deque.peekFirst().compareTo(deque.peekLast()) > 0;
            } else {
                return deque.peekFirst().compareTo(deque.peekLast()) < 0;
            }
        } else {
            if (param.isAscend()){
                return stringToInteger(deque.peekFirst()) > stringToInteger(deque.peekLast());
            } else {
                return stringToInteger(deque.peekFirst()) < stringToInteger(deque.peekLast());
            }
        }
    }


    private BlockingDeque<String> getThisDeque(List<BlockingDeque<String>> deques){
        if (deques.size() == 1) return deques.get(0);
        String[] buffStr = new String[deques.size()];
        BlockingDeque<String>[] buffDeque = new BlockingDeque[deques.size()];
        int i = 0;
        for (BlockingDeque<String> deque:deques){
            if (deque.peekFirst() == null) continue;
            buffStr[i] = deque.peekFirst();
            buffDeque[i] = deque;
            i++;
        }
        if (Arrays.stream(buffStr).anyMatch(Objects::isNull)){
            throw new IllegalArgumentException("due over fast grabbing");
        }
        if (param.isStr()){
            if (param.isAscend()){
                return buffDeque[findMinStringIndex(buffStr)];
            } else {
                return buffDeque[findMaxStringIndex(buffStr)];
            }
        } else {
            if (param.isAscend()){
                return buffDeque[findMinNumberIndex(buffStr)];
            } else {
                return buffDeque[findMaxNumberIndex(buffStr)];
            }
        }
    }
    private int findMaxStringIndex(String[] strings){
        if (strings.length == 1) return 0;
        Optional<String> max = Arrays.stream(strings).max(Comparator.comparing(String::toString));
        return Arrays.stream(strings).collect(toList()).indexOf(max.get());
    }
    private int findMinStringIndex(String[] strings){
        if (strings.length == 1) return 0;
        Optional<String> min = Arrays.stream(strings).min(Comparator.comparing(String::toString));
        return Arrays.stream(strings).collect(toList()).indexOf(min.get());
    }
    private int findMaxNumberIndex(String[] numbers){
        if (numbers.length == 1) return 0;
        Optional<Integer> numMax = Arrays.stream(numbers).map(this::stringToInteger).max(Comparator.comparingInt(Integer::intValue));
        Optional<String> max = Optional.of(numMax.get().toString());
        return Arrays.stream(numbers).collect(toList()).indexOf(max.get());
    }
    private int findMinNumberIndex(String[] numbers){
        if (numbers.length == 1) return 0;
        Optional<Integer> numMin = Arrays.stream(numbers).map(this::stringToInteger).min(Comparator.comparingInt(Integer::intValue));
        Optional<String> min = Optional.of(numMin.get().toString());
        return Arrays.stream(numbers).collect(toList()).indexOf(min.get());
    }

    private Integer stringToInteger(String string){
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e){
            throw new NumberFormatException(String.format("Ошибка: %s \nСтрока: [%s] не является числом\nФайл исключен",e.getMessage(),string));
        }
    }
    @Override
    public void close() throws IOException {
        if (writer != null){
            writer.close();
        }
    }

}
