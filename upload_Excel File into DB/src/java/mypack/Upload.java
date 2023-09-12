/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mypack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author toss
 */



//@WebServlet("/Upload1")
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB 
                 maxFileSize=1024*1024*50,      	// 50 MB
                 maxRequestSize=1024*1024*100)   
public class Upload extends HttpServlet 
{

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final long serialVersionUID = 205242440643911308L;
    private static final String UPLOAD_DIR = "uploads";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Upload NOS</title>");            
            out.println("</head>");
            out.println("<body>");

        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
        File fileSaveDir = new File(uploadFilePath);

        if (!fileSaveDir.exists()) 
        {
            fileSaveDir.mkdirs();
        }
        String fileName = "";
        String filePath="";
        for (Part part : request.getParts()) 
        {
            System.out.println("part="+part);
            fileName = getFileName(part);
            part.getName();
            filePath=uploadFilePath + File.separator + fileName;
        }       
            Part part = request.getPart("fileName");
            part.write(fileName);
            
            String path=filePath;
            InputStream is=part.getInputStream();
            boolean succs=uploadFile(is, path);
            if(succs)
            {
                System.out.println(" upload file into folder adreess:- "+path);
//      ------------------------------------------------------------------ Truncate table & File Upload ------------------------------------------------------
      
            try {   
                
//                Class.forName("com.mysql.jdbc.Driver");   
//                Connection con = DriverManager.getConnection("jdbc:mysql://192.168.1.223:3306/test_1", "root", "s@mp@rk@123");
              
                Connection con = config2.getconnection();
                if (con == null) {
                    out.println("Error : Could not connect to database");
                    return;
                }
                con.setAutoCommit(false);
                String q = "insert into tbl_nos(NOS,Tagging,Type,Update_date) values(?,?,?,now())";
                String q1 = "TRUNCATE table tbl_nos;";
//-------------------------------------- -----------------------------------   TRuncate    ------------------------------------------------------------------
                PreparedStatement pstmt = con.prepareStatement(q1);
                pstmt.executeUpdate();

//  ---------------------------------------------------------------------------    Upload -----------------------------------------
                pstmt = con.prepareStatement(q);
                
                BufferedReader lineReader=new BufferedReader(new FileReader(filePath));
                String lineText=null;
                int count=0;
                lineReader.readLine();
                
                String sb="";
                String en="";
                String ed="";
                while((lineText=lineReader.readLine())!=null)
                {
                    count++;
                    String [] data=lineText.split(",");
                    
                     sb = data[0];
                     en = data[1];
                     ed = data[2];

                    pstmt.setString(1, sb);
                    pstmt.setString(2, en);
                    pstmt.setString(3, ed);
                    pstmt.executeUpdate();
                }
                lineReader.close();
//                pstmt.close();
                con.commit();
//                con.close();
                count++;
                if (pstmt != null) try { pstmt.close(); } catch(Exception e) {}
                if (con != null) try { con.close(); } catch(Exception e) {}
                out.println("<h1>SucessFully Uploaded "+count+" data<h1>");
                }catch (Exception e) 
                {
                    out.println("<h1>Exception Caught while File Uploading into Database<h1>"+e);
//                  e.printStackTrace();
                }       
            
//----------------------------------------------------------    End OF File Reading From CSV and Upload To Db   --------------------------------------------------------------------            
            }else
            {
                out.println("<h1>!!!!!Failed TO Upload!!!!!</h1>"+path);
            }               
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public boolean uploadFile(InputStream is, String path)
    {
        boolean test= false;
        try
            {
            byte[] byt=new byte[is.available()];
            int rr = 0;
            FileOutputStream fops=new FileOutputStream(path);
            while((rr = is.read(byt)) != -1 )
            {
                fops.write(byt,0,rr);  
                System.out.println(fops);
            }
            fops.flush();
            fops.close();
            test = true;
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(" Exception Caught=="+e);
        }
        return test;
    }    
    
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }    
    
}


