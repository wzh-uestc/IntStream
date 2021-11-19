package driver.high_speed_driver;

public class HighSpeedRule {
    private String sip;
    private String sipMask;
    private String dip;
    private String dipMask;
    private String sport;
    private String sportMask;
    private String dport;
    private String dportMask;
    private String protocal;
    private String protocalMask;
    private String action;

    public HighSpeedRule(String sip, String sipMask, String dip, String dipMask, String action) {
        this.sip = sip;
        this.sipMask = sipMask;
        this.dip = dip;
        this.dport = dipMask;
        this.action = action;
    }

    public HighSpeedRule(String sip, String sipMask, String dip, String dipMask, String sport,
                         String sportMask, String dport, String dportMask, String protocol,
                         String protocolMask, String action) {
        this.sip = sip;
        this.sipMask = sipMask;
        this.dip = dip;
        this.dport = dipMask;
        this.sport = sport;
        this.sportMask = sportMask;
        this.dport = dport;
        this.dportMask = dportMask;
        this.protocal = protocol;
        this.protocalMask = protocolMask;
        this.action = action;
    }

    public String getCommand(){
        return "addacl " + this.sip + " " + this.sipMask + " "
                + this.dip + " " + this.dipMask + " " + this.action;
    }
}
