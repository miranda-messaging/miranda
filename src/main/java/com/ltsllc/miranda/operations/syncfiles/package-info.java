package com.ltsllc.miranda.operations.syncfiles;

/**
 * Classes pertaining to synchronizing the files on the node with all the nodes
 * in the cluster.  The operation consists of figuring out which node has the most
 * up-to-date version then downloading that version.
 *
 * SyncFiles is the operation that starts it up
 * SyncFilesStartState is the operation start state
 * SyncFilesWaitingState is the state SyncFiles goes into while waiting for replies to the GetVersions message
 * terminates after a predefined period of time
 * SyncFilesGetFiles is the state that SyncFiles goes into to download the files
 * SyncFilesComplete is the stop for the operation
 */