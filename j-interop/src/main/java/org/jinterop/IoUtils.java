/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jinterop;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class IoUtils {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");

    /**
     * Close given socket and shutdown inner input / output.
     * If any error occur, it is logged and ignored.
     *
     * @param <T> the {@code Socket} type, used to ease return assignment
     * @param socket the socket to close
     * @param name name used on error message
     * @return {@code null}
     */
    public static final <T extends Socket> T closeSilent(T socket, String name) {
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "Failed to shutwown socket " + name + " inner streams.", ex);
            }
            try {
                socket.close();
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "Failed to close " + name + ".", ex);
            }
        }
        return null;
    }

    /**
     * Close given closable.
     * If any error occur, it is logged and ignored.
     *
     * @param <T> the {@code Closeable} type, used to ease return assignment
     * @param toCLose the object to close
     * @param name name used on error message
     * @return {@code null}
     */
    public static final <T extends AutoCloseable> T closeSilent(T toClose, String name) {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "Failed to close " + name + ".", ex);
            }
        }
        return null;
    }
}
