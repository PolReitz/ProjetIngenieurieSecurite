import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DeltaEntry;
import com.dropbox.client2.DropboxAPI.DeltaPage;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;


public class DropBox {
	DropboxAPI<WebAuthSession> api;
	public DropBox() throws FileNotFoundException, DropboxException{
		connexion();
	}
	public void connexion() throws DropboxException, FileNotFoundException{
		AppKeyPair appKeys = new AppKeyPair("bgir0e66ehmeepr", "eu27swmjb52dze0");
		WebAuthSession session = new WebAuthSession(appKeys, Session.AccessType.DROPBOX);
		api = new DropboxAPI<WebAuthSession>(session);	
		File tokensFile = new File("TOKENS");
		System.out.println("Voulez vous creer une authentification ?(tapez 1) ou vous reauthentifier (taper 2)");
		Scanner input = new Scanner(System.in);
		String rep =input.next() ;
		if(rep.equals("1")){

	    	System.out.println("Please go to this URL and hit \"Allow\": " +api.getSession().getAuthInfo().url); // tell user to go to app allowance URL
	    	AccessTokenPair tokenPair = api.getSession().getAccessTokenPair();
	    	
	    	System.out.println("Finished allowing? Enter 'next' if so: ");
	    	if(input.next().equals("next")){
	    		RequestTokenPair tokens = new RequestTokenPair(tokenPair.key, tokenPair.secret);
		    	api.getSession().retrieveWebAccessToken(tokens);
		    	
		    	PrintWriter tokenWriter = new PrintWriter(tokensFile);
		    	tokenWriter.println(session.getAccessTokenPair().key);
		    	tokenWriter.println(session.getAccessTokenPair().secret);
		    	tokenWriter.close();
		    	System.out.println("Authentication Successful!");
	    	}	
		}else if(rep.equals("2")){

			Scanner tokenScanner = new Scanner(tokensFile); // Initiate Scanner to read tokens from TOKEN file
			String ACCESS_TOKEN_KEY = tokenScanner.nextLine(); // Read key
			String ACCESS_TOKEN_SECRET = tokenScanner.nextLine(); // Read secret
			tokenScanner.close(); //Close Scanner

			//Reauth
			AccessTokenPair reAuthTokens = new AccessTokenPair(ACCESS_TOKEN_KEY, ACCESS_TOKEN_SECRET);
			api.getSession().setAccessTokenPair(reAuthTokens);
			System.out.println("Reauthentication Sucessful!");
		
		}else{
			System.out.println("Reponse non accepte.");
		}

	}
		
	public void upload(String nomfichier, String pathvoulu ) throws FileNotFoundException, DropboxException{
		File file = new File(nomfichier);
		FileInputStream ip = new FileInputStream(file);
		api.putFile(pathvoulu+nomfichier, ip, file.length(), null, null);
	}
	
	public void download(String nomfichier) throws FileNotFoundException, DropboxException{
		File file = new File(nomfichier);
		FileOutputStream op = new FileOutputStream(file);
		api.getFile(nomfichier, null, op, null);
	}
	
	public void createPath(String path) throws DropboxException{
		api.createFolder(path);
	}
	
	public void delete(String path) throws DropboxException{
		api.delete(path);
	}
	
	public ArrayList<String> listerFichierNom(String path) throws DropboxException {
		Entry e = api.metadata(path, 0, null, true, null);
		List<Entry> list= e.contents;
		ArrayList<String>listfichier = new ArrayList<String>();
	    for(int i=0;i<list.size();i++) {
	    	if (!list.get(i).isDir){
	    		listfichier.add(list.get(i).fileName());
	    		System.out.println(list.get(i).fileName());
	    	}
	    }
		return listfichier;
	}
	
	public DropboxAPI<WebAuthSession> getApi() {
		return api;
	}
	
	 
}
