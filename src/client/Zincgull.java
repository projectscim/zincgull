package client;

import java.applet.*;
import java.awt.*;

public class Zincgull extends Applet {
	private static final long serialVersionUID = 1692879245497912173L;
	
	int width, height;

   public void init() {
      width = getSize().width;
      height = getSize().height;
      setBackground( Color.black );
   }

   public void paint( Graphics g ) {
      g.setColor( Color.green );
      for ( int i = 0; i < 10; ++i ) {
         g.drawLine( width, height, i * width / 10, 0 );
      }
   }
}
