package studijosKTU;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.JFrame;
/**
 * @author Mutis
 */
public class TestScreenKTU {
    // išbandome skirtingo dydžio simbolius, taip pat galimus fontus 0,1,2,3
    static void demoSkirtingiCeliųDydžiaiIrFontai(){
        for(int dydis=16; dydis<=32; dydis+=4)
            for(ScreenKTU.Fonts fnt: ScreenKTU.Fonts.values()){
                ScreenKTU sc = new ScreenKTU(dydis,dydis, 4, 10, fnt);
                sc.print(1, 1, "DYDIS=" + dydis);
                sc.print(2, 1, fnt.toString());
                sc.refresh();
            }
    }
    
    static void exploreFontsProperties(){
        JFrame fr = new JFrame("KTU IF"); 
        for(int k=8; k<=60; k++){
             FontMetrics fm = fr.getFontMetrics(new Font("Serif", Font.PLAIN, k));
             int a = fm.getAscent();
             int b = fm.getDescent();
             int c = fm.getLeading();
             System.out.printf("%2d %2d %2d %2d\n", k, a, b, c);
        }
    }
    
    static char[] chrs = "MINTIS╳╳↖↗↘↙ąęgqČŠ\u263a\u263b*+@\u2600\u2602".toCharArray();
    static void drawBoard(ScreenKTU sc){
        sc.setFontColor(Color.red);
        int k=0;
        for(int i = 0; i < sc.screenH; i++) 
            for(int j = 0; j < sc.screenW; j++){ 
                sc.setBackColor((i+j)%2==0?Color.green:Color.gray);
                sc.print(i,j, chrs[k++]);
//                Toolkit.getDefaultToolkit().beep(); 
            }
            sc.refresh(10);
    }
    static void demo() {
//        exploreFontsProperties();
        for(ScreenKTU.Fonts fnt: ScreenKTU.Fonts.values())
            for(int cs=20; cs<36; cs+=2){
//            for(int cs=8; cs<26; cs+=2){
                ScreenKTU sc = new ScreenKTU(cs, cs, 4, 6, fnt, 
                                   ScreenKTU.Grid.OFF);
                sc.setTitle("" + cs);
                drawBoard(sc);
            }
    }  

    public static void main(String[] args) {
        demoSkirtingiCeliųDydžiaiIrFontai();
    }

}