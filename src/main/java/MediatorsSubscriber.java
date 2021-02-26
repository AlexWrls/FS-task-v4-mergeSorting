import java.util.concurrent.BlockingDeque;

public interface MediatorsSubscriber {
     void addDeque(BlockingDeque<String> deque);
}
