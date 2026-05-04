package models;

public class WaitingLineEntry {
    private final int waitlistId;
    private final String customerSsn;
    private final int instanceId;
    private final int priorityNum;
    private final String timeEntered;
    private final String status;

    public WaitingLineEntry(
            int waitlistId,
            String customerSsn,
            int instanceId,
            int priorityNum,
            String timeEntered,
            String status
    ) {
        this.waitlistId = waitlistId;
        this.customerSsn = customerSsn;
        this.instanceId = instanceId;
        this.priorityNum = priorityNum;
        this.timeEntered = timeEntered;
        this.status = status;
    }

    public int getWaitlistId() {
        return waitlistId;
    }

    public String getCustomerSsn() {
        return customerSsn;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getPriorityNum() {
        return priorityNum;
    }

    public String getTimeEntered() {
        return timeEntered;
    }

    public String getStatus() {
        return status;
    }
}
