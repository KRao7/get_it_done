# get_it_done
A screenshot application (.jar) using Java and JNA (KeyHook). Created to take screenshots of webpages and save with URL name.

Steps to use:

1) Click on the .jar file and an icon will appear on the system tray which indicates the application is running.
2) Press PrtScn button, screenshot will be taken and a dialog will appear wherein the file name has to be inputted (typically a full URL).
3) Press enter and a File Save Dialog will open, choose for the desired location where the file is to be saved and click Enter.
4) Screenshot Saved.
5) If you need to exit the application, right click on the icon on your system tray and click on EXIT. 

Merits:
The file name is automatically edited. All not allowed characters like \ / : * ? " < > | are replaced with a Dot,
"http://" and "www" are removed and converts the first letter into Uppercase. Takes time stamp as prefix and saves the image file.
