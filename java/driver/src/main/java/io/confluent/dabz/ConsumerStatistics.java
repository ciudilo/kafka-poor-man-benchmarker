package io.confluent.dabz;

import java.util.concurrent.atomic.AtomicLong;

public class ConsumerStatistics implements Runnable {
    private static ConsumerStatistics shared = new ConsumerStatistics();
    private AtomicLong totalNumberOfMessagesConsumed = new AtomicLong(0);
    private AtomicLong totalSizeOfMessagesConsumed = new AtomicLong(0);
    private AtomicLong totalTimePartionHasBeenReset = new AtomicLong(0);
    private Boolean minimalist = false;

    public static ConsumerStatistics getShared() {
        return shared;
    }

    public AtomicLong getTotalNumberOfMessagesConsumed() {
        return totalNumberOfMessagesConsumed;
    }

    public AtomicLong getTotalSizeOfMessagesConsumed() {
        return totalSizeOfMessagesConsumed;
    }

    public AtomicLong getTotalTimePartionHasBeenReset() {
        return totalTimePartionHasBeenReset;
    }

    public Boolean getMinimalist() {
        return minimalist;
    }

    public void setMinimalist(Boolean minimalist) {
        this.minimalist = minimalist;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        long previousNumberOfMessageProduced = shared.totalNumberOfMessagesConsumed.get();
        long previousSizeOfMessagesProduced = shared.totalSizeOfMessagesConsumed.get();

        try {
            while (true) {
                Thread.sleep(1000);
                long deltaNumberOfMessages = shared.totalNumberOfMessagesConsumed.get() - previousNumberOfMessageProduced;
                long deltaSizeOfmessages = shared.totalSizeOfMessagesConsumed.get() - previousSizeOfMessagesProduced;
                previousNumberOfMessageProduced = shared.totalNumberOfMessagesConsumed.get();
                previousSizeOfMessagesProduced = shared.totalSizeOfMessagesConsumed.get();

                if (minimalist) {
                    printMachineReadable(deltaSizeOfmessages);
                } else {
                    printHumanReadable(deltaNumberOfMessages, deltaSizeOfmessages);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printHumanReadable(long deltaNumberOfMessages, long deltaSizeOfmessages) {
        System.out.println(String.format("Message received %d (%s)", deltaNumberOfMessages, humanReadableByteCount(deltaSizeOfmessages, false)));
    }

    public void printMachineReadable(long deltaSizeOfmessages) {
        System.out.println(String.valueOf(deltaSizeOfmessages));
    }

    public String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
