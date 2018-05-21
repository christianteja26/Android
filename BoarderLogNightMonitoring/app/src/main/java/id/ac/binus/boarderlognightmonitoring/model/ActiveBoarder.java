package id.ac.binus.boarderlognightmonitoring.model;

/**
 * Created by CT on 26-Apr-17.
 */

public class ActiveBoarder {
    private String RegistrationID;
    private String BinusianID;
    private String BoarderName;

    public String getRegistrationID(){ return RegistrationID; }
    public void setRegistrationID(String RegistrationID){this.RegistrationID = RegistrationID;}

    public String getBinusianID(){ return BinusianID; }
    public void setBinusianID(String BinusianID){this.BinusianID = BinusianID;}

    public String getBoarderName(){return BoarderName;}
    public void  setBoarderName(String BoarderName){this.BoarderName = BoarderName;}
}
