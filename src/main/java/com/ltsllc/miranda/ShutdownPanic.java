package com.ltsllc.miranda;

/**
 * A panic that occurs during a shutdown.
 *
 * <h3>Fields</h3>
 * <table border="1" summary=:"Class Field">
 *     <tr>
 *         <td><b>Name</b></td>
 *         <td><b>Description</b></td>
 *     </tr>
 *     <tr>
 *         <td>shutdownReason</td>
 *         <td>Why the panic was raised</td>
 *     </tr>
 * </table>
 *
 */
public class ShutdownPanic extends Panic {
    public enum ShutdownReasons {
        Exception
    }

    public ShutdownReasons shutdownReason;

    public ShutdownReasons getShutdownReason() {
        return shutdownReason;
    }

    public void setShutdownReason(ShutdownReasons shutdownReason) {
        this.shutdownReason = shutdownReason;
    }

    public ShutdownPanic (ShutdownReasons reason, Throwable cause) {
        super(cause, Reasons.Shutdown);

        shutdownReason = reason;
    }

}
