import java.io.Closeable;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public interface Sort extends Closeable {
    void sortData(List<BlockingDeque<String>> deques);
}
