/* Implement this class. */

import java.time.ZonedDateTime;
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(10, (o1, o2) -> {
                if(o1.getPriority() == o2.getPriority()) {
                    if(o1.getStart() == o2.getStart()) {
                        return o1.getId() - o2.getId();
                    } else {
                        return o1.getStart() - o2.getStart();
                    }
                } else {
                    return o2.getPriority() - o1.getPriority();
                }
            });
    Task task = null;
    boolean isRunning = true;
    long lostTime = 0;
    @Override
    public void run() {
        while(isRunning) { // always run

            // Pick which task to run
            if(!queue.isEmpty()) {
                task = queue.poll();
            }

            // Run the task
            if(task != null) {
                long start = ZonedDateTime.now().toInstant().toEpochMilli();
                while (task.getLeft() > 0) {
                    if (task.isPreemptible() && queue.peek() != null && queue.peek().getPriority() > task.getPriority()) {
                        queue.add(task);
                        task = null;
                        lostTime += (ZonedDateTime.now().toInstant().toEpochMilli() - start);
                        break;
                    }
                    long current = ZonedDateTime.now().toInstant().toEpochMilli();
                    task.setLeft(task.getLeft() - (current - start));
//                    lostTime = 0;
                    start = current;
                }
                if(task != null) {
                    System.out.println("Task " + task.getId() + " finished at " + Timer.getTimeDouble() + " with priority " + task.getPriority());
                    task.finish();
                    task = null;
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        queue.add(task);
    }

    @Override
    public int getQueueSize() {
        return (queue.size() + (task != null ? 1 : 0));
    }

    // TODO: Clauclez getWorkLeft cu Math.round
    @Override
    public long getWorkLeft() {
        long workLeft = 0;
        for(Task task : queue) {
            workLeft += task.getLeft();
        }
        return (workLeft + (task != null ? task.getLeft() : 0) + lostTime);
    }

    @Override
    public void shutdown() {
        queue.clear();
        isRunning = false;
    }
}
