package team037.DataStructures;

import team037.Messages.Communication;

public class MessageBuffer {
    public Communication[] buffer;
    private int index, oldest;

    public MessageBuffer() {
        index = 0;
        oldest = 0;
        buffer = new Communication[20];
    }

    public void addToBuffer(Communication communication) {
        buffer[index] = communication;
        index++;
        index %= 20;
        if (index == oldest) {
            oldest++;
            oldest %= 20;
        }
    }

    // Return the entire message array and reinitialize it
    public Communication[] dumpBuffer() {
        Communication[] dump = buffer;
        buffer = new Communication[20];
        index = 0;
        oldest = 0;
        return dump;
    }

    // Return the oldest communication in the array
    public Communication getOldest() {
        Communication out = buffer[oldest];
        buffer[oldest] = null;
        oldest++;
        oldest %= 20;
        if (buffer[oldest] == null) {
            oldest = 0;
            index = 0;
        }
        return out;
    }

    // Return whether this array contains anything
    public boolean isEmpty() {
        return buffer[oldest] == null;
    }

    // Return the value of the
    public int newestIndex() {
        return index - 1;
    }
}
