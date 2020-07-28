package spa.ds;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Launcher {
	
	static DateFormat sdf=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	public static void main(String[] args) throws MalformedURLException, IOException, ParseException {		
        Document doc = Jsoup.connect("http://localhost:8150/deploy").get();
        System.out.println(doc.title());
        String dateStr="";
        for(Element elem:doc.getElementsByTag("tr")) {
        	if(elem.text().contains("JNLPTest.jar")) {
        		System.out.println(elem.child(0));
        		System.out.println(elem.child(2).child(0).text());
        		dateStr=elem.child(2).child(0).text();
        	}
        }
        
        File launcher=new File("Launcher.jar");
        
        Date jarTimeStamp= sdf.parse(dateStr);
        System.out.println(jarTimeStamp);
        File jnlpJar=new File("JNLPTest.jar");
        if(!jnlpJar.exists()) {
        	InputStream inputStream = new URL("http://localhost:8150/deploy/JNLPTest.jar").openStream();
        	Files.copy(inputStream, jnlpJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        try {
		URLClassLoader child = URLClassLoader.newInstance(new URL[] {jnlpJar.toURL()});
        Class classToLoad = Class.forName ("com.prateek.TestJnlp", true, child);
        
		
        Method method = null;
        for(Method m: classToLoad.getDeclaredMethods()) {
			if(m.toString().contains("main")) {
				method= m;
				System.out.println(m);
			}
		}
        String[] args1= {};
        Object instance = classToLoad.newInstance ();
        Object result = method.invoke (instance, new Object[]{args1});
        System.out.println(result);
        }catch (Exception e) {
        	e.printStackTrace();
		}
	}
}
