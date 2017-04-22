package com.ltsllc.miranda.node;

import com.google.gson.Gson;
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.Updateable;
import com.ltsllc.miranda.util.ImprovedRandom;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Clark on 1/21/2017.
 */
public class NodeElement implements Perishable, Updateable<NodeElement>, Matchable<NodeElement> {
    private static Gson ourGson = new Gson();
    private static SimpleDateFormat ourSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd@HH:mm:ss.SSS");

    private String dns;
    private String ip;
    private int port;
    private String description;
    private long lastConnected;

    public long getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(long lastConnected) {
        this.lastConnected = lastConnected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public NodeElement (Node node) {
        this.dns = node.getDns();
        this.ip = node.getIp();
        this.port = node.getPort();
        this.description = node.getDescription();
        this.lastConnected = -1;
    }

    public NodeElement (String dns, String ip, int port, String description) {
        this.dns = dns;
        this.ip = ip;
        this.port = port;
        this.description = description;
        this.lastConnected = -1;
    }

    public boolean equals(NodeElement element) {
        return dns.equals(element.getDns()) && ip.equals(element.getIp()) && port == element.getPort();
    }

    public boolean hasTimedout (long timeout) {
        Date date = new Date();
        long now = date.getTime();
        long timeSinceLastConnect = now - getLastConnected();
        return timeSinceLastConnect >= timeout;
    }

    /**
     * false.
     *
     * Objects of this class do not expire.  Instead, during a health check,
     * an element may have not connected in an acceptable time frame and
     * therefore get dropped.
     *
     * @param time
     * @return
     */
    public boolean expired (long time) {
        return false;
    }

    public void update (NodeElement newValue) {
        this.dns = newValue.dns;
        this.ip = newValue.ip;
        this.port = newValue.port;
        this.description = newValue.description;
    }


    public String toJson() {
        return ourGson.toJson(this);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("NodeElement {");
        stringBuilder.append("dns: ");
        stringBuilder.append(getDns());
        stringBuilder.append(", ip: ");
        stringBuilder.append(getIp());
        stringBuilder.append(", port: ");
        stringBuilder.append(getPort());
        stringBuilder.append(", last connect: ");
        Date date = new Date(getLastConnected());
        stringBuilder.append(0 == getLastConnected() ? "never" : ourSimpleDateFormat.format(date));
        stringBuilder.append(", description: ");
        stringBuilder.append(getDescription());
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private static final String[] FISRT_DOMAINS = {
            "foo",
            "bar",
            "whatever",
            "nerts",
            "liwipi",
            "fred",
            "node",
            "junk",
            "curley",
            "amy"
    };

    private static final String[] MIDDLE_DOMIANS = {
            "krabnebula",
            "ever",
            "intermediate",
            "google",
            "amazon",
            "bell",
            "uswaste",
            "meetup",
            "experts",
            "beginners"
    };

    private static final String[] LAST_DOMAINS = {
            "com",
            "net",
            "org"
    };

    public static String randomDnsName (ImprovedRandom random) {
        int numberOfDomains = random.nextInt(2, 6);
        StringBuffer sb = new StringBuffer();
        sb.append (FISRT_DOMAINS[random.nextIndex(FISRT_DOMAINS)]);

        int i = 1;
        while (i < (numberOfDomains - 1)) {
            sb.append(".");
            sb.append(MIDDLE_DOMIANS[random.nextIndex(MIDDLE_DOMIANS)]);
            i++;
        }

        sb.append(".");
        sb.append(LAST_DOMAINS[random.nextIndex(LAST_DOMAINS)]);

        return sb.toString();
    }

    public static String randomIp (ImprovedRandom random) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < 4; i++) {
            if (i > 0 && i < 4) {
                sb.append(".");
            }

            byte b = random.nextByte();
            sb.append(Byte.toUnsignedInt(b));
        }

        return sb.toString();
    }

    public static NodeElement random (ImprovedRandom random) {
        String dns = randomDnsName(random);
        String ip = randomIp (random);
        int port = 1 + random.nextInt(65535);

        return new NodeElement(dns, ip, port, "a node");
    }

    public boolean equivalent (Object o) {
        if (null == o || !(o instanceof NodeElement))
            return false;

        NodeElement other = (NodeElement) o;
        return matches(other);
    }

    public void updateFrom (NodeElement other) {
        setIp(other.getIp());
        setLastConnected(other.getLastConnected());
        setDescription(other.getDescription());
    }

    public boolean matches (NodeElement other) {
        return getDns().equals(other.getDns()) && getPort() == other.getPort();
    }
}
