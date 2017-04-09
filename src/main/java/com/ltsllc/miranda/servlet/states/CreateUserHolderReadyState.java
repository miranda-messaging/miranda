package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.CreateUserHolder;
import com.ltsllc.miranda.servlet.holder.ServletHolder;
import com.ltsllc.miranda.user.messages.DuplicateUserMessage;
import com.ltsllc.miranda.user.messages.UserCreatedMessage;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateUserHolderReadyState extends State {
    private static CreateUserHolderReadyState ourInstance;
    
    public static synchronized void setInstance (CreateUserHolderReadyState createUserHolder) {
        if (null == ourInstance) {
            ourInstance = createUserHolder;
        }
    }

    public CreateUserHolder getCreateUserHolder () {
        return (CreateUserHolder) getContainer();
    }

    public CreateUserHolderReadyState(CreateUserHolder createUserHolder) {
        super(createUserHolder);
    }
    
    public State processMessage (Message message) {
        State nextState = getCreateUserHolder().getCurrentState();
        
        switch (message.getSubject()) {
            case UserCreated: {
                UserCreatedMessage userCreatedMessage = (UserCreatedMessage) message;
                nextState = processUserCreatedMessage(userCreatedMessage);
                break;
            }
            
            case DuplicateUser: {
                DuplicateUserMessage duplicateUserMessage = (DuplicateUserMessage) message;
                nextState = processDuplicateUserMessage(duplicateUserMessage);
                break;
            }
            
            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        
        return nextState;
    }

    public State processUserCreatedMessage (UserCreatedMessage userCreatedMessage) {
        getCreateUserHolder().setUserCreatedAndWakeup(true);

        return getCreateUserHolder().getCurrentState();
    }

    public State processDuplicateUserMessage(DuplicateUserMessage duplicateUserMessage) {
        getCreateUserHolder().setUserCreatedAndWakeup(false);

        return getCreateUserHolder().getCurrentState();
    }
}
