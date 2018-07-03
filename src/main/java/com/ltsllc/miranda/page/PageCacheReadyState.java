package com.ltsllc.miranda.page;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.event.messages.EvictMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.writer.WriteResponseMessage;

public class PageCacheReadyState extends State {
    public PageCacheReadyState (Consumer container) {
        super(container);
    }

    public PageCache getPageCache () {
        return (PageCache) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getPageCache().getCurrentState();

        switch (message.getSubject()) {
            case WriteResponse: {
                WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                nextState = processWriteResponseMessage (writeResponseMessage);
                break;
            }

            case Evict: {
                EvictMessage evictMessage = (EvictMessage) message;
                nextState = processEvictMessage(evictMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processWriteResponseMessage (WriteResponseMessage writeResponseMessage) {
        getPageCache().writeCompleted(writeResponseMessage.getFilename());

        return getPageCache().getCurrentState();
    }

    public State processEvictMessage (EvictMessage evictMessage) {
        for (Page page : getPageCache().getPages())
            if (page.isDirty() && !page.isBeingWritten()) {
                getPageCache().writePage(page);
            }

        return getPageCache().getCurrentState();
    }
}
