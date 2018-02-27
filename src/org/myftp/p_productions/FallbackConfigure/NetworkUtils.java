package org.myftp.p_productions.FallbackConfigure;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NetworkUtils {
	private static Logger logger = null;
	
	private static int ruleNo = 13;
	private static final String pwd = "ROUTER_PASSWORD";
	private static final String proto = "TCP";
	private static final boolean active = true;
	private static final String desc = "MinecraftDynmap";
	private static final String port = "8080";
	private static final String localIpServer = getIpForHost("HOST_OF_SERVER");
	private static final String localIpFallback = getIpForHost("HOST_OF_FALLBACKSERVER");
	private static final String localPort = "8123";
	
	
	// TODO: Maybe Async ?
	public static String toServer() {
		log(Level.INFO,"Configuring port forward to Server");
		return setPortProperty(ruleNo, pwd, proto, active, desc, port, localIpServer, localPort);
		
	}

	// TODO: Maybe Async ?
	public static String toFallback() {
		log(Level.INFO,"Configuring port forward to Fallback");
		return setPortProperty(ruleNo, pwd, proto, active, desc, port, localIpFallback, localPort);
		
	}
	
	private static String getIpForHost(String string) {
		try {
			return InetAddress.getByName(string).getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	private static void log(Level level, String msg){
		if(logger!=null) logger.log(level, msg);
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger loggr) {
		logger = loggr;
	}
	
	public static String getWebContent(String url){
        String content =  "";
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
            for ( String line; (line = br.readLine()) != null; )
        {
	                content+=line+"\n";
            }
        }catch (Exception e){
            content=null;
        }
        return content;
    }
	
	public static String getContentWithPost(String urls, Map<String, String> pv){
        if(pv.isEmpty())return "";
        OutputStreamWriter writer=null;
        BufferedReader reader=null;
        try{
            String body="";
            for(Map.Entry<String, String> entry: pv.entrySet()){
                
                body += entry.getKey() +"="+ URLEncoder.encode( entry.getValue(), "UTF-8" ) + "&";
            
            }
            //System.out.println(body);
            URL url = new URL( urls );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoInput( true );
            connection.setDoOutput( true );
            connection.setUseCaches( false );
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
            connection.setRequestProperty( "Content-Length", String.valueOf(body.length()) );

            writer = new OutputStreamWriter( connection.getOutputStream() );
            writer.write( body );
            writer.flush();


            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
            String content="";
            for ( String line; (line = reader.readLine()) != null; )
            {
                content+=line+"\n";
            }

            return content;
        }catch(UnsupportedEncodingException uee){
             uee.printStackTrace(System.out);
        }catch(MalformedURLException mue){
            mue.printStackTrace(System.out);
        }catch(ProtocolException pe){
            pe.printStackTrace(System.out);
        }catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }finally{
            if(writer!=null) try{writer.close();}catch(IOException ioe){};
            if(reader!=null) try{reader.close();}catch(IOException ioe){};
        }
        return "";
    }
	
	public static String getSessionId(String pwd) throws IOException, SAXException, ParserConfigurationException {
		
		String sessionInfo = getWebContent("http://fritz.box/login_sid.lua");
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder;
		
		builder = factory.newDocumentBuilder();
	    
	    Document document;
	    
		document = builder.parse(new ByteArrayInputStream(sessionInfo.getBytes()));
	    
	    NodeList challengeNode = document.getElementsByTagName("Challenge");
	    if(challengeNode.getLength()!=1)
	    	throw new IOException("Challenge-Tag count not 1: "+challengeNode.getLength());
	    
		String challenge = challengeNode.item(0).getTextContent();
	    
		Map<String, String> mapLogin = new HashMap<>();
		String response;
		// mapLogin.put("username", "Modac");	// If user account login is activated in the Fritz!Box
		try {
			response = challenge + "-" + OtherUtils.getMD5(new String(new String(challenge + "-" + pwd).getBytes("UTF-16LE")));
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getCause());
		}
		mapLogin.put("response", response);
		
		String resp = getContentWithPost("http://fritz.box/login_sid.lua", mapLogin);
		
		document = builder.parse(new ByteArrayInputStream(resp.getBytes()));
		
		NodeList SIDNode = document.getElementsByTagName("SID");
		if(SIDNode.getLength()!=1)
			throw new IOException("SID-Tag count not 1: " + SIDNode.getLength());
		
		String sid = SIDNode.item(0).getTextContent();
		if(sid.equals("0000000000000000"))
			throw new IOException("Error retrieving Session Id\n"+resp);
		
		return sid;
	}
	
	public static String setPortProperty(int ruleNo, String pwd, String proto, boolean active, String desc, String port, String localIp, String localPort) {
		try {
			Map<String, String> map = new HashMap<>();
			
			String rule= "rule"+ruleNo;
			
			map.put("sid", getSessionId(pwd));
			map.put("current_rule", rule);
			map.put("is_new_rule", "false");
			map.put("was_exposed_host", "false");
			map.put("forwardrules_"+rule+"_protocol", proto);
			if(active)
				map.put("forwardrules_"+rule+"_activated", "1");
			map.put("forwardrules_"+rule+"_description", desc);
			map.put("forwardrules_"+rule+"_port", port);
			map.put("forwardrules_"+rule+"_endport", port);
			//map.put("selected_lan_device", "192.168.2.85#");
			map.put("forwardrules_"+rule+"_fwip", localIp);
			map.put("forwardrules_"+rule+"_fwport", localPort);
			map.put("rule", rule);
			map.put("apply", "");
			
			return getContentWithPost("http://fritz.box/internet/port_fw_edit.lua", map);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return "Error";
		
	}

	public static void updateRuleNo(int ruleno) {
		if(ruleno>=0){
			ruleNo=ruleno;
			OtherUtils.notify(FallbackConfigure.instance, "Rule number updated to "+ruleNo);
		}
	}
	
	
}
