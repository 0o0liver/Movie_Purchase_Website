package edu.uci.ics.binghal.service.api_gateway.threadpool;

import java.util.Arrays;

public class ThreadPool {
    private int numWorkers;
    private Worker[] workers;
    private ClientRequestQueue queue;

    public ThreadPool(int numWorkers) {
        this.numWorkers = numWorkers;
        this.queue = new ClientRequestQueue();
        this.workers = new Worker[numWorkers];
        for (int i = 0; i < numWorkers; ++i){
            workers[i] = Worker.CreateWorker(i, this);
        }
    }

    public void startWorkers(){
        for (int i = 0; i < this.numWorkers; ++i){
            this.workers[i].start();
        }
    }

    public void add(ClientRequest clientRequest) {
        queue.enqueue(clientRequest);
    }

    public ClientRequest remove() {
        return queue.dequeue();
    }

    public ClientRequestQueue getQueue() {
        return queue;
    }

}
