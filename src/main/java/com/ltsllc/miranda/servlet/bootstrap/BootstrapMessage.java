package com.ltsllc.miranda.servlet.bootstrap;

import com.ltsllc.clcl.DistinguishedName;
import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A message asking the system to bootstrap itself
 */
public class BootstrapMessage extends Message {
    private DistinguishedName caDistinguishedName;
    private String caPassword;
    private DistinguishedName nodeDistinguishedName;
    private String nodePassword;
    private DistinguishedName adminDistinguishedName;
    private String adminPassword;

    public DistinguishedName getAdminDistinguishedName() {
        return adminDistinguishedName;
    }

    public void setAdminDistinguishedName(DistinguishedName adminDistinguishedName) {
        this.adminDistinguishedName = adminDistinguishedName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getCaPassword() {
        return caPassword;
    }

    public void setCaPassword(String caPassword) {
        this.caPassword = caPassword;
    }

    public String getNodePassword() {
        return nodePassword;
    }

    public void setNodePassword(String nodePassword) {
        this.nodePassword = nodePassword;
    }

    public DistinguishedName getCaDistinguishedName() {
        return caDistinguishedName;
    }

    public void setCaDistinguishedName(DistinguishedName caDistinguishedName) {
        this.caDistinguishedName = caDistinguishedName;
    }

    public DistinguishedName getNodeDistinguishedName() {
        return nodeDistinguishedName;
    }

    public void setNodeDistinguishedName(DistinguishedName nodeDistinguishedName) {
        this.nodeDistinguishedName = nodeDistinguishedName;
    }

    public BootstrapMessage(BlockingQueue<Message> senderQueue, Object senderObject, DistinguishedName caDistinguishedName,
                            String caPassword, DistinguishedName nodeDistinguishedName, String nodePassword,
                            DistinguishedName adminDistinguishedName, String adminPassword) {
        super(Subjects.Bootstrap, senderQueue, senderObject);
        setCaDistinguishedName(caDistinguishedName);
        setCaPassword(caPassword);
        setNodeDistinguishedName(nodeDistinguishedName);
        setNodePassword(nodePassword);
        setAdminDistinguishedName(adminDistinguishedName);
        setAdminPassword(adminPassword);
    }

    public BootstrapMessage(BlockingQueue<Message> senderQueue, Object senderObject,
                            DistinguishedName adminDistinguishedName, String adminPassword) {
        super(Subjects.Bootstrap, senderQueue, senderObject);
        setAdminDistinguishedName(adminDistinguishedName);
        setAdminPassword(adminPassword);
    }
}
