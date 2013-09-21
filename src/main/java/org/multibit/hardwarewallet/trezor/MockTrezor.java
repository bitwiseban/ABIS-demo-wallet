package org.multibit.hardwarewallet.trezor;

import com.google.common.base.Preconditions;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.bsol.trezorj.core.Trezor;
import uk.co.bsol.trezorj.core.TrezorEvent;
import uk.co.bsol.trezorj.core.TrezorEventType;
import uk.co.bsol.trezorj.core.TrezorListener;
import uk.co.bsol.trezorj.core.events.TrezorEvents;
import uk.co.bsol.trezorj.core.trezors.AbstractTrezor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A mock Trezor device that you can use to loop back events to the MultiBit UI.
 */
public class MockTrezor extends AbstractTrezor implements Trezor {

    private static final Integer DEFAULT_USB_VENDOR_ID = 0x10c4;
    private static final Integer DEFAULT_USB_PRODUCT_ID = 0xea80;
    private static final String DEFAULT_SERIAL_ID = "trezor1234";

    private static final Logger log = LoggerFactory.getLogger(MockTrezor.class);

    private Integer vendorId;
    private Integer productId;
    private String serialNumber;

    /**
     * The DataOutputStream that is writing TO the Trezor emulator.
     */
    private DataOutputStream out = null;

    /**
     * The DataInputStream that is being read FROM the Trezor emulator.
     */
    private DataInputStream in = null;

    /**
     * The device that in a non-mock object would be the physical Trezor.
     */
    private TrezorEmulator device = null;

    /**
     * <p>Create a new instance of a Mock Trezor device</p>
     *
     */
    public MockTrezor() {
        this.vendorId = DEFAULT_USB_VENDOR_ID;
        this.productId = DEFAULT_USB_PRODUCT_ID;
        this.serialNumber= DEFAULT_SERIAL_ID;
    }

    @Override
    public synchronized void connect() {
        Preconditions.checkState(device == null, "Device is already connected");

        device = new TrezorEmulator();

        // Add unbuffered data streams for easy data manipulation.
        out = new DataOutputStream(device.getOutputStream());
        in = new DataInputStream(device.getInputStream());

        // Monitor the input stream.
        //monitorDataInputStream(in);

        // We loopback that the device has connected ok
        // i.e. we always say we connect successfully.
        log.debug("TESTCODE : Saying that we always connect successfully") ;

        for (TrezorListener listener : listeners) {
            try {
                listener.getTrezorEventQueue().put(TrezorEvents.newSystemEvent(TrezorEventType.DEVICE_CONNECTED));
            } catch (InterruptedException e) {
                log.error(e.getMessage().getClass() + " " + e.getMessage());
            }
        }
    }

    @Override
    public synchronized void internalClose() {
        Preconditions.checkNotNull(device, "Device is not connected");

        // We loopback that the device has disconnected ok
        // i.e. we always disconnect successfully.
        log.debug("TESTCODE : Saying that we always disconnect successfully") ;
        TrezorEvent disconnectEvent = TrezorEvents.newSystemEvent(TrezorEventType.DEVICE_DISCONNECTED);

        for (TrezorListener listener : listeners) {
            try {
                listener.getTrezorEventQueue().put(disconnectEvent);
            } catch (InterruptedException e) {
                log.error(e.getMessage().getClass() + " " + e.getMessage());
            }
        }

        // Close and throw away the Trezor.
        device.close();
        device = null;
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        Preconditions.checkNotNull(message, "Message must be present");
        Preconditions.checkNotNull(device, "Device is not connected");

        // Log the message being sent to the TrezorEmulator.
        log.debug("Sending to TrezorEmulator the message '" + message.toString() + "'");

        // Apply the message to the TrezorEmulator.
        writeMessage(message, out);

        // Depending on the message sent, loopback a message as if coming back from the Trezor.
        loopBack(message);
    }

    /**
     * According to the message sent to the Trezor, loopback a reply.
     * This is so that you can mock replies at the object level rather than the wire level.
     *
     * @param message
     */
    private void loopBack(Message message) {

        try {
            Thread.sleep(100);

            // Work out what the reply to the message is.
            final TrezorEvent trezorEvent = workOutReply(message);

            if (trezorEvent != null) {
                log.debug("Firing event: {} ", trezorEvent.eventType().name());
                for (TrezorListener listener : listeners) {
                    listener.getTrezorEventQueue().put(trezorEvent);
                }
            } else {
                log.debug("No loopback reply in MockTrezor to the sent message '" + message.toString() + "'");
            }

            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Work out what the reply to a message to the Trezor is.
     * @param message
     * @return reply
     */
    private TrezorEvent workOutReply(Message message) {
        log.debug("Working out reply for message '" + message.toString() + "'");

        return null;
    }
}