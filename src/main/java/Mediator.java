import java.util.concurrent.BlockingDeque;

public interface Mediator {
    void notifyDeque(BlockingDeque<String> deque);
    void addSubscribe(MediatorsSubscriber subscriber);
}
