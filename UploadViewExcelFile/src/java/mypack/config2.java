package mypack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class config2
{
    public static String ctiIp = "";
    public static String confip = "";
    public static String databaseip = "";//192.168.1.50
    public static String databaseusername = "";
    public static String databasepassword = "";
    public static String databasename = "";
    public static String loglevel = "";
    public static String logFilepath = "";
    
    public static Connection getconnection()
    {
        Connection conn = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            conn = DriverManager.getConnection("jdbc:mysql://"+databaseip+":3306/"+databasename+"",databaseusername,databasepassword);
            System.out.println("correct ip and port");
//          conn = DriverManager.getConnection("jdbc:mysql://192.168.1.223:3306/test_1","root", "s@mp@rk@123");
        }
        catch(Exception ee)
        {
            System.out.println("check ip/port/extension etc");
            System.out.println(ee);
            ee.printStackTrace();
        }
        return conn;
    }
    static public void CreateAppLogs(String message)
    {
       try
        {
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat formatter= new SimpleDateFormat("dd-MMM-yyyy_HH");
            String dateNow = formatter.format(currentDate.getTime());
            String filename = logFilepath+"Adesktop-Logs_"+dateNow+".txt";
            FileWriter fstream = new FileWriter(filename,true);
            BufferedWriter out = new BufferedWriter(fstream);
            formatter = new SimpleDateFormat("dd-MMM-yyyy/HH:mm:ss");
            dateNow = formatter.format(currentDate.getTime());
            out.write(dateNow+"\t: "+message+"\n");
            out.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}