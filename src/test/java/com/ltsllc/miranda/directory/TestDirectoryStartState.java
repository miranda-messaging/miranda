package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.event.EventDirectory;
import com.ltsllc.miranda.directory.MirandaDirectory;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.test.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestDirectoryStartState extends TestCase {
    public DirectoryStartState directoryStartState;

    @Override
    public Miranda getMockMiranda() {
        return super.getMockMiranda();
    }

    MirandaDirectory mockDirectory;

    public MirandaDirectory getMockDirectory() {
        return mockDirectory;
    }

    public void setMockDirectory(MirandaDirectory mockDirectory) {
        this.mockDirectory = mockDirectory;
    }



    public DirectoryStartState getDirectoryStartState() {
        return directoryStartState;
    }

    public void setDirectoryStartState(DirectoryStartState directoryStartState) {
        this.directoryStartState = directoryStartState;

    }


    @Before
    public void setup() throws Exception {
        super.setup();


        setMockDirectory(mock(MirandaDirectory.class));
        DirectoryStartState directoryStartState = new DirectoryStartState(getMockDirectory());
        setDirectoryStartState(directoryStartState);

    }

    public List<File> loadList (String[] array) {
        List<File> list = new ArrayList<File>(array.length);

        int i;
        for (i = 0; i < array.length; i++) {
            list.add(new File(array[i]));
        }

        return list;
    }

    public String[] testFileNames = {"Hi", "There"};

    @Test
    public void testPocessScanCompleteMessage () throws MirandaException {
        List<File> files = loadList(testFileNames);

        when(getMockDirectory().getDirectory()).thenReturn(new File ("whatever"));
        ScanCompleteMessage scanCompleteMessage = new ScanCompleteMessage(null, null, files);

        getDirectoryStartState().processMessage(scanCompleteMessage);

        verify(getMockDirectory(), atLeastOnce()).setFiles(any());
    }

    @Test
    public void testProcessExceptionDuringScanMessage() throws MirandaException {
        ExceptionDuringScanMessage exceptionDuringScanMessage = new ExceptionDuringScanMessage(null,
                null, new Exception());
        setupMockMiranda();
        getDirectoryStartState().processMessage(exceptionDuringScanMessage);

        verify (getMockMiranda(), atLeastOnce()).panic(any());
    }



}
