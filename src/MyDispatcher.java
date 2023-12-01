/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        if(algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            hosts.get(task.getId()%hosts.size()).addTask(task);
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
            int min = Integer.MAX_VALUE;
            int index = 0;
            for(int i = 0; i < hosts.size(); i++) {
                if(hosts.get(i).getWorkLeft() < min) {
                    min = (int) hosts.get(i).getWorkLeft();
                    index = i;
                }
            }
            hosts.get(index).addTask(task);
        }
    }
}
