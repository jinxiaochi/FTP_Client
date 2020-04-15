package entrance;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import ui.InitCoponents;

/**
 * @author jinxiaochi
 * @date 2020��3��31��
 * @effect TODO
 */
public class SwingMain extends JFrame {
	private static final long serialVersionUID = 1L;

	public SwingMain(String title) {
		super(title);
		new InitCoponents(this);
	}

	/**
	 * @effect �������
	 * @param args
	 */
	public static void main(String[] args) {
		//������������
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }
		
		JFrame main = new SwingMain("FTP�ͻ���");
		BufferedImage image = null;  
		try {  
		image = ImageIO.read(main.getClass().getResource("/resource/ftp.png")); 
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		
		if(image != null) {
			main.setIconImage(image);
		}
		// frame�Ĳ���Ҫ�������; setVisible����������е����
		main.setSize(845, 500);
		main.setResizable(false);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setLocationRelativeTo(null);
		main.setVisible(true);

	}

}
