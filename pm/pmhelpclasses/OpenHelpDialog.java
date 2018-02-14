import javax.help.*;
import java.net.URL;
    
public class OpenHelpDialog {
    
    public static HelpSet hs; 
    public static HelpBroker hb;
    public static  String helpHS;
    public static URL hsURL;
    
    public OpenHelpDialog(){
	helpHS = "../pmhelpset/pmhelp.hs";

	ClassLoader cl = OpenHelpDialog.class.getClassLoader();
    
	try {
	    hsURL = HelpSet.findHelpSet(cl, helpHS);
	    hs = new HelpSet(null, hsURL);
	} catch (Exception ee) {
	    // Say what the exception really is
	    System.out.println( "HelpSet " + ee.getMessage());
	    System.out.println("HelpSet "+ helpHS +" not found"); 
	}  	
	hb = hs.createHelpBroker();
	hb.setHelpSet(hs);
	hb.setCurrentID("assaytype");
	hb.setDisplayed(true);
    }
    
    public static void main(String [ ] args){
	new OpenHelpDialog();	
    }
}    

