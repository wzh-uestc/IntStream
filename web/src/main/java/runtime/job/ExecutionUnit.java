package runtime.job;

public class ExecutionUnit {
    private String name;

    public ExecutionUnit(){

    }

    public ExecutionUnit(String name){
        setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
