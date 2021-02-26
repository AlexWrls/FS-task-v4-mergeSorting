import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class MediatorImpl implements Mediator {

    private List<MediatorsSubscriber> allReceivers = new ArrayList<>();

    @Override
    public synchronized void notifyDeque(BlockingDeque<String> deque) {
        for (MediatorsSubscriber subscriber : allReceivers){
            subscriber.addDeque(deque);
        }
    }

    public void addSubscribe(MediatorsSubscriber subscriber){
        allReceivers.add(subscriber);
    }
}
