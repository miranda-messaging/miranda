package com.ltsllc.miranda.servlet.objects;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.util.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Clark on 4/7/2017.
 */
public class ResultObject {
    private Results result;
    private String additionalInfo;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public String getAddionalInfo() {
        return  additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setAdditionalInfo (Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        Utils.closeIgnoreExceptions(stringWriter);

        this.additionalInfo = stringWriter.toString();
    }
}
