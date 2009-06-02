package fr.umlv.unitex.console;

public class ConsoleEntry {

    String content;
    String error;
    
    /* 0=no error button, 1=error down button, 2=error up button, 3=nothing */ 
    int status;
    
    public ConsoleEntry(String command,boolean isRealCommand) {
        this.content=command;
        this.status=isRealCommand?0:3;
    }
    
    public String getContent() {
        return content;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void addErrorMessage(String s) {
        if (error==null) {
            error=s;
            status=1;
        } else {
            if (!error.endsWith("\r\n") && !error.endsWith("\n")) {
                error=error+"\n";
            }
            error=error+s;
        }
    }
    
    public String getErrorMessage() {
        return error;
    }
}
