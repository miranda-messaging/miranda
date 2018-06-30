package com.ltsllc.miranda.node;

import com.ltsllc.miranda.message.Message;

/**
 * A decorator that adds a key to a message.
 * <p>
 * <p>
 * This class marks a message as being part of a {@link Conversation} by
 * adding a key to the message.  Interested parties send a {@link com.ltsllc.miranda.node.messages.StartConversationMessage}
 * to the node and whenever a message comes through with a matching key,
 * the message is forwarded on to the recipient.
 * </p>
 * <p>
 * <h3>PROPERTIES</h3>
 * <p>
 * <p>
 * <table border="1">
 * <th>
 * <td>Name</td>
 * <td>Type</td>
 * <td>Description</td>
 * </th>
 * <tr>
 * <td>key</td>
 * <td>String</td>
 * <td>The key (conversation) that the message is associated with.</td>
 * </tr>
 * <tr>
 * <td>message</td>
 * <td>Message</td>
 * <td>The message that is part of the conversation.</td>
 * </tr>
 * </table>
 * </p>
 */
public class ConversationMessage {
    private String key;
    private Message message;

    public ConversationMessage(String key, Message message) {
        this.key = key;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getKey() {
        return key;
    }
}
