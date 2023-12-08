/* Implement this class. */

import java.util.List;
import java.util.concurrent.Semaphore;

public class MyDispatcher extends Dispatcher {
    int taskNo = 0;
    Semaphore mutex = new Semaphore(1);
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            hosts.get(taskNo%hosts.size()).addTask(task);
            ++taskNo;
        } else if(algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            int min = Integer.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < hosts.size(); i++) {
                if (hosts.get(i).getQueueSize() < min) {
                    min = hosts.get(i).getQueueSize();
                    index = i;
                }
            }
            hosts.get(index).addTask(task);
        } else if(algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            if(task.getType() == TaskType.SHORT) {
                hosts.get(0).addTask(task);
            } else if(task.getType() == TaskType.MEDIUM) {
                hosts.get(1).addTask(task);
            } else if(task.getType() == TaskType.LONG) {
                hosts.get(2).addTask(task);
            }
        }  else if(algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            long min = Long.MAX_VALUE;
            int index = 0;
            for(int i = 0; i < hosts.size(); i++) {
                if(min - hosts.get(i).getWorkLeft() > 950) { // at least one second difference
                    min = hosts.get(i).getWorkLeft() ; // convert to seconds
                    index = i;
                }
            }
            hosts.get(index).addTask(task);
        }
        mutex.release();
    }
}
