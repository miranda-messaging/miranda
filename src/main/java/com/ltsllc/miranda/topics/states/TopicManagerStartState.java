package com.ltsllc.miranda.topics.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.manager.ManagerStartState;
import com.ltsllc.miranda.topics.TopicManager;

/**
 * Created by Clark on 5/14/2017.
 */
public class TopicManagerStartState extends ManagerStartState {
    public TopicManager getTopicManager () {
        return (TopicManager) getContainer();
    }

    public TopicManagerStartState (TopicManager topicManager) {
        super(topicManager);
    }

    public State getReadyState () {
        return new TopicManagerReadyState(getTopicManager());
    }
}
