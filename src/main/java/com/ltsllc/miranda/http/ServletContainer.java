package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Consumer;

import java.util.List;

/**
 * A {@link Consumer} that contains servelets.
 *
 * <p>
 *     An instance of this class responds to requests from the outside world
 *     in the form of HTTP requests and passes these requests onto the servlets
 *     that it contains.  Instances of this class respond to
 * </p>
 *
 * <h3>Attributes</h3>
 * <table border="1">
 *     <tr>
 *         <th>Attribute</th>
 *         <th>Type</th>
 *         <th>Description</th>
 *     </tr>
 *
 *     <tr>
 *         <th>serlets</th>
 *         <th>List<ServeletMapping></th>
 *         <th>The sevrlets that this container contains.</th>
 *     </tr>
 * </table>
 */
abstract public class ServletContainer extends Consumer {
    abstract void startContainer ();
    abstract void stopContainer ();

    private List<ServletMapping> servlets;

    public List<ServletMapping> getServlets() {
        return servlets;
    }

    public void addServlets (List<ServletMapping> mappings) {
        for (ServletMapping servletMapping : mappings) {
            getServlets().add(servletMapping);
        }
    }
}
