/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import javax.swing.filechooser.*;

/**
 *
 * @author Bill
 */
public class FileExtension extends FileFilter {
    private String imageFormat = "gif";
    private String imageFormat1 = "jpg";
    private String imageFormat2 = "jpeg";
    private String imageFormat3 = "png";
    public boolean accept(File f){
        if(f.isDirectory()){
            return true;
        }
        if(extension(f).equalsIgnoreCase(imageFormat) || extension(f).equalsIgnoreCase(imageFormat1) || extension(f).equalsIgnoreCase(imageFormat2) || extension(f).equalsIgnoreCase(imageFormat3)) {
            return true;
        }else{
            return false;
        }
    }
    public String getDescription(){
        return "Jpg, Jpeg, gif, png only";
    }

    public String extension(File f){
        String fileName = f.getName();
        int indexFile = fileName.lastIndexOf(".");
        if(indexFile > 0 && indexFile < fileName.length() - 1){
            return fileName.substring(indexFile+1);
            
        }else{
            return "";
        }
    }
    
}
