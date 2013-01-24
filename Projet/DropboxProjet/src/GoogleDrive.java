import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


public class GoogleDrive implements Serializable {
	public Drive service;
	
	public GoogleDrive() throws ClassNotFoundException, IOException{
		connexion();
	}
	public void connexion() throws IOException, ClassNotFoundException{
		HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
       
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, "136137944234.apps.googleusercontent.com", "xG9fk357VVUkFYYwniGc_aN4", Arrays.asList(DriveScopes.DRIVE))
            .setAccessType("online")
            .setApprovalPrompt("auto").build();
        
        System.out.println("Voulez vous creer une authentification ?(tapez 1) ou vous reauthentifier (taper 2)");
        Scanner input = new Scanner(System.in);
		String rep =input.next() ;
		if(rep.equals("1")){
			String url = flow.newAuthorizationUrl().setRedirectUri("urn:ietf:wg:oauth:2.0:oob").build();
	        System.out.println("Please open the following URL in your browser then type the authorization code:");
	        System.out.println("  " + url);
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String code = br.readLine();
	        
	        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri("urn:ietf:wg:oauth:2.0:oob").execute();
	
	        PrintWriter tokenWriter = new PrintWriter("googletoken");
	    	tokenWriter.println(response.getAccessToken());
	    	tokenWriter.close();

	        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
	        
	        //Create a new authorized API client
	        service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
		}else if(rep.equals("2")){

			Scanner tokenScanner = new Scanner("googletoken"); // Initiate Scanner to read tokens from TOKEN file
			String token = tokenScanner.next(); // Read key
			GoogleTokenResponse response = new GoogleTokenResponse();
			response.setAccessToken(token);
			GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
			service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
			System.out.println("Reauthentication Sucessful!");
		}else{
			System.out.println("Reponse non accepte.");
		}
	}
	
	public void upload (String nomfichier) throws IOException{
		File test = new File();
        test.setTitle(nomfichier);      
        java.io.File fileContent = new java.io.File(nomfichier);
        FileContent mediaContent = new FileContent(null, fileContent);
        service.files().insert(test,mediaContent).execute();
	}
	
	public void download (String nomfichier) throws IOException{
		String[] nomlist = listerFichierNom();
		String[] idlist = listerFichierID();
		String id="";
		for (int i=0;i<nomlist.length;i++){
			if (nomlist[i].equals(nomfichier)){
				id = idlist[i];
				break;
			}
		}		
		File file = service.files().get(id).execute();
		HttpResponse resp =service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();	       
		InputStream input = resp.getContent();
		FileOutputStream output = new FileOutputStream(nomfichier);
		IOUtils.copy(input, output);
	}
	
	public void createPath(String path) throws IOException{
		File test = new File();
        test.setTitle(path);
        test.setMimeType("application/vnd.google-apps.folder"); 
		service.files().insert(test).execute();

	}
	
	public void deleteFile(String nomfichier) throws IOException{
		service.files().delete(nomfichier).execute();
	}
	public void deletePath(String path) throws IOException{
		File test = new File();
        test.setTitle(path);
        test.setMimeType("application/vnd.google-apps.folder"); 
		service.files().delete(path).execute();
	}
	public String [] listerFichierNom() throws IOException{
		 FileList listfich = service.files().list().execute();
		 String[] listfichier = new String[listfich.getItems().size()];
		    for(int i=0;i<listfich.getItems().size();i++) {
		    	listfichier[i] = listfich.getItems().get(i).getTitle();
		    }
		return listfichier;
	}
	public String [] listerFichierID() throws IOException{
		 FileList listfich = service.files().list().execute();
		 String[] listfichier = new String[listfich.getItems().size()];
		    for(int i=0;i<listfich.getItems().size();i++) {
		    	listfichier[i] = listfich.getItems().get(i).getId();
		    }
		return listfichier;
	}
	public Drive getService() {
		return service;
	}
	
}
