
/**
 * 
 * Purpose: Takes a window screenshot.
 *
 * @author Kiran N Rao
 * @version 1.0 5/25/15
 */
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;  
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;


public class KeyHook {
    private static HHOOK hhk;
    private static LowLevelKeyboardProc keyboardHook;
    static JFileChooser fileChooser = new JFileChooser();
    
   
    public static void main(String[] args) {
    
        final User32 lib = User32.INSTANCE;
        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        keyboardHook = new LowLevelKeyboardProc() 
        {
        	public LRESULT callback(int nCode, WPARAM wParam, final KBDLLHOOKSTRUCT info) 
        	{
        		if (nCode >= 0) 
        		{
        			switch(wParam.intValue()) 
        			{
        				case WinUser.WM_KEYDOWN:
        				System.out.println(info.vkCode);
        				Executor executor = Executors.newSingleThreadExecutor();
                    	executor.execute(new Runnable() 
                    	{
                    		public void run() 
                    		{
                    			if (info.vkCode == 44) 
                    			{ 
                    			try 
                    			{
                    				
                        		Robot robot = new Robot();
                        		BufferedImage bi=robot.createScreenCapture(new Rectangle(0,25,1366,744));
                        		JFrame frame = new JFrame();
                        		frame.toFront();
                        		frame.requestFocus();
                        		frame.setAlwaysOnTop(true);
                        		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        		String name = JOptionPane.showInputDialog(frame, "Enter file name");
                        		frame.dispose();
                        		String fileName= dovalidateFile(name);
                        		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", ".png");
                        		fileChooser.setFileFilter(filter);
                        		fileChooser.setSelectedFile(new File (fileName));
                        		int returnVal = fileChooser.showSaveDialog(null);
                        		if ( returnVal == JFileChooser.APPROVE_OPTION )
                        		{
                        			File file = fileChooser.getSelectedFile();
                        			final File validateFile = validateFile(file);
                        			ImageIO.write(bi, "png", validateFile);
                        		}
                        	}
                        	catch (NullPointerException e1)  {e1.printStackTrace(); }
                        	catch (AWTException e1) {e1.printStackTrace(); }
                        	catch (IOException e1) {e1.printStackTrace();}
                        	}
                        	}
                        });
        			}
        		}
        		return lib.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
        	}

    private File validateFile(File file) {
    	DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss.ddMMMMMyyyy");
		Calendar cal = Calendar.getInstance();
		String filePath = file.getAbsolutePath();
		if (filePath.indexOf(".png") == -1) 
		{
			filePath += "." + dateFormat.format(cal.getTime()) + ".png";
		}
		file = new File(filePath);
		if (file.exists()) 
		{
			file.delete();
		}
		try 
		{
			file.createNewFile();
		} 
		catch (Exception e) {e.printStackTrace();}
		return file; 
	}

	private String dovalidateFile(String name) {
				
		String input = name.replace("https://www.","");  
		input = input.replaceAll("http://www.","");   
		input = input.replaceAll("https://","");    
		input = input.replace("http://","");
		input = input.replace("/?",".");
		input = input.replace("/",".");
		input = input.replace("|",".") ;
		input = input.replace("%",".");
		input = input.replace("<",".");
		input = input.replace(">",".");
		input = input.replaceAll("\\?",".");
		input = input.replaceAll("\\*",".");
		input = input.replace(":",".");
		input = input.replace("\\",".");
		input = Character.toUpperCase(input.charAt(0)) + input.substring(1);
		return input;
	} 
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);
        if(!SystemTray.isSupported())
        {
            return ;
        }
        SystemTray systemTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(KeyHook.class.getResource("/images/icon.png"));
        PopupMenu trayPopupMenu = new PopupMenu();
        MenuItem close = new MenuItem("Exit");
        close.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
            	System.err.println("unhook and exit");
                lib.UnhookWindowsHookEx(hhk);
                System.exit(0);
              
            }
        });
        trayPopupMenu.add(close);
        TrayIcon trayIcon = new TrayIcon(image, "captur", trayPopupMenu);
        trayIcon.setImageAutoSize(true);
        try
        {
            systemTray.add(trayIcon);
        }
        catch(AWTException awtException){awtException.printStackTrace();}
     
        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) 
        {
            if (result == -1) 
            {
                System.err.println("error in get message");
                break;
            }
            else 
            {
                System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
       lib.UnhookWindowsHookEx(hhk);
        
    }
    
}