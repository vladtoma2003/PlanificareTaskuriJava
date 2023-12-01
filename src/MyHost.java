/* Implement this class. */

import java.util.Comparator;
import java.util.PriorityQueue;

public class MyHost extends Host {
    PriorityQueue<Task> queue = new PriorityQueue<>(Comparator.comparing(Task::getPriority).thenComparing(Task::getStart));
    @Override
    public void run() {
    }

    @Override
    public void addTask(Task task) {
        queue.add(task);
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public long getWorkLeft() {
        long workLeft = 0;
        for(Task task : queue) {
            workLeft += task.getLeft();
        }
        return workLeft;
    }

    @Override
    public void shutdown() {
    }
}
