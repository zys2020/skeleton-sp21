package gh2;

import deque.Deque;
import deque.LinkedListDeque;

public class GuitarString {
    /**
     * Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday.
     */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /**
     * Buffer for storing sound data.
     */
    private Deque<Double> buffer;

    /**
     * Create a guitar string of the given frequency.
     * Create a buffer with capacity = SR / frequency.
     * Your should initially fill your buffer array with zeros.
     */
    public GuitarString(double frequency) {
        buffer = new LinkedListDeque<>();
        for (int i = 0; i < Math.round(SR / frequency); i++) {
            buffer.addFirst(0.);
        }
    }

    /**
     * Pluck the guitar string by replacing the buffer with white noise.
     * Dequeue everything in buffer, and replace with random numbers
     * between -0.5 and 0.5. You can get such a number by using:
     * double r = Math.random() - 0.5;
     */
    public void pluck() {
        for (int i = 0; i < buffer.size(); i++) {
            buffer.removeFirst();
            double r = Math.random() - 0.5;
            buffer.addLast(r);
        }
    }

    /**
     * Advance the simulation one time step by performing one iteration of the Karplus-Strong algorithm.
     * Dequeue the front sample and enqueue a new sample that is the average of the two multiplied by the DECAY factor.
     */
    public void tic() {
        double newSample = sample();
        buffer.removeFirst();
        buffer.addLast(newSample);
    }

    /**
     * Return the double at the front of the buffer.
     */
    public double sample() {
        return (buffer.get(0) + buffer.get(1)) * 0.5 * DECAY;
    }
}
