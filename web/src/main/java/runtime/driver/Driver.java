package runtime.driver;

public class Driver {
    private String name;
    private String note;
    private String server;

    public Driver(String name, String note, String server){
        this.name = name;
        this.note = note;
        this.server = server;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNote(String note){
        this.note = note;
    }

    public void setServer(String server){
        this.server = server;
    }

    public String getName(){
        return this.name;
    }

    public String getNote(){
        return this.note;
    }

    public String getServer(){
        return this.server;
    }

    public String toString(){
        return name + " " + note + " " + server;
    }
}
