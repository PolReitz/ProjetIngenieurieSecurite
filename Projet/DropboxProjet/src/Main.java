import java.io.BufferedReader;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.activation.MimeType;
import javax.annotation.PostConstruct;

import org.omg.CORBA_2_3.portable.OutputStream;
import org.xml.sax.InputSource;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Copy;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


import org.apache.commons.io.IOUtils;

public class Main {
	private static DropBox dp;
	private static GoogleDrive gd;
	/**
	 * @param args
	 */	
	    public static void main(String[] args) throws Exception {
	    	
	    	//dp = new DropBox();
	    	//dp.upload("music.mp3","/Dossier/");
	    	//dp.listerFichierNom("/");
	    	
	    	gd = new GoogleDrive();
	    	gd.upload("test2.mp3");
	    	//compare("/","/");
	        
	    }
	    public static String [] compare(String pathdp, String pathgd) throws DropboxException, IOException{
	    	ArrayList<String> nomdp = dp.listerFichierNom(pathdp);
	    	String[] nomgd = gd.listerFichierNom();
	    	String[] idgd = gd.listerFichierID();
	    	String [] nomdptrie = new String [nomdp.size()];
	    	String [] nomgdtrie;
	    	java.io.File filedp;
	    	File filegd;
			FileOutputStream op;
	    	for(String s : nomdp) {
	    		if (!dp.getApi().metadata(s, 0, null, false, null).isDir){
	    			filedp = new java.io.File(s);
		    		op = new FileOutputStream(filedp);
		    		System.out.println(dp.getApi().getFile(s, null, op, null).getMetadata().clientMtime);
	    		}else{
	    			compare(pathdp+ dp.getApi().metadata(s, 0, null, false, null).fileName(), pathgd);
	    		}
		    }
	    	for (int i=0;i<idgd.length;i++){
	    		System.out.println(gd.getService().files().get(idgd[i]).execute().getModifiedDate());
	    	}
	    	return nomdptrie;
	    }
	    
}
