package edu.uci.ics.binghal.service.api_gateway.threadpool;

import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import static edu.uci.ics.binghal.service.api_gateway.GatewayService.ANSI_RED;
import static edu.uci.ics.binghal.service.api_gateway.GatewayService.ANSI_RESET;

public class ClientRequestQueue {
    private ListNode head;
    private ListNode tail;

    public ClientRequestQueue() {
        head = tail = null;
    }

    @Override
    public String toString() {
        return "ClientRequestQueue{" +
                "head=" + head +
                ", tail=" + tail +
                '}';
    }

    public synchronized void enqueue(ClientRequest clientRequest) {
        ListNode newNode = new ListNode(clientRequest, null);
        if (isEmpty()){
            head = newNode;
            ServiceLogger.LOGGER.info("Notifying all thread to wake up.");
            this.notifyAll();
        }
        else{
            tail.setNext(newNode);
        }
        tail = newNode;
    }

    public synchronized ClientRequest dequeue() {
        while (isEmpty()){
            ServiceLogger.LOGGER.info("Request queue is empty.");
            try{
                ServiceLogger.LOGGER.info("Waiting.");
                this.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        ClientRequest returnRequest = head.getClientRequest();
        head = head.getNext();
        if (isEmpty()){
            tail = null;
        }
        return returnRequest;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public boolean isFull() {
        return false;
    }
}
