
package minierlang; 
import java.io.*;

public class Main {
    static public void main(String argv[]) {    
        try {
            /* Scanner instantiation */
            scanner l = new scanner(new FileReader(argv[0]));
            /* Parser instantiation */
            @SuppressWarnings("deprecation")
			parser p = new parser(l);
            /* Run the parser */
            p.parse();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


