import java.util.logging.Logger;

import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Logger log = Logger.getGlobal();

        Param param = Param.getParam(args);


        Mediator mediator = new MediatorImpl();
        try (Sort sort = new SortImpl(param.getNameOutFile());
             WorkerSort workerSort = new WorkerSort(sort,mediator);){

            mediator.addSubscribe( (MediatorsSubscriber)sort);
            workerSort.doSort();

        } catch (Exception e){
            log.warning(String.format("Ошибка %s работы программы",e.getMessage()));
        }


    }
}
