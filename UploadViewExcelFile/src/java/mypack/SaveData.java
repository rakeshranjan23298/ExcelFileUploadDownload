package mypack;

import java.io.BufferedReader;
import java.io.FileReader;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;

@MultipartConfig(fileSizeThreshold = 10485760, maxFileSize = 52428800L, maxRequestSize = 104857600L)
public class SaveData extends HttpServlet {

    private static final long serialVersionUID = 205242440643911308L;
    private static final String UPLOAD_DIR = "uploads";
    private static final char DEFAULT_SEPARATOR = ',';
    private static String downloadDir = "";
    private static int linesInBuffer = 1000;
    private static int counter = 0;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
        String msg = "";
        try {
            String type = request.getParameter("type");
            System.out.println("welcome on SaveData Servlet type=" + type);
            if (type == null) {
                type = "upload";
            }
            if (type.equalsIgnoreCase("upload")) {
                PrintWriter out = response.getWriter();
                final String applicationPath = request.getServletContext().getRealPath("");
                final String uploadFilePath = applicationPath + "uploads";

                File fileSaveDir = new File(uploadFilePath);
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdirs();
                }
                String fileName = "";
                String filePath = "";

                for (final Part part : request.getParts()) {
                    System.out.println("part=" + part);
                    fileName = this.getFileName(part);
                    if (fileName != "") {
                        part.getName();
                        filePath = uploadFilePath + fileName;
                        break;
                    }
                    part.getName();
                    filePath = uploadFilePath + fileName;
//                    break;
                }
                Part part2 = request.getPart("fileName");

                part2.write(fileName);
                String file_location = filePath;
//              String file_location=fileSaveDir+"\\"+fileName;
//              file_location = file_location.replace("\\", "\\\\");

                final String path = filePath;
                final InputStream is = part2.getInputStream();
                final boolean succs = this.uploadFile(is, path);
                System.out.println("UPLOAD -- " + succs);
                String errorr = "";

//------------------------  if folder creaded and file uploaded sucess ----------------------------------------------------------                
                if (succs) {
                    System.out.println(" upload file into folder adreess:- " + path);
                    try {
                        final Connection con = config2.getconnection();
                        if (con == null) {
                            System.out.println("Error : Could not connect to database");
                            return;
                        }
                        PreparedStatement pstmt = null;
                        con.setAutoCommit(false);
                        final BufferedReader lineReader = new BufferedReader(new FileReader(filePath));
                        final String lineText = null;
                        int count = 0;
                        final String sql = " LOAD DATA local INFILE '" + file_location + "' \nINTO TABLE tbl_allocated_data \nFIELDS TERMINATED BY '\t' \nENCLOSED BY '\"'\nLINES TERMINATED BY '\\r\\n'\nIGNORE 1 LINES \n(mobile_no,alternate_no,landline_no,agreement_no,loan_no,loan_amount,loan_type,name,father_name,spouse_name,home_address,office_address,pan_no,aadhar_no,vehical_no) \nSET insert_date = now(),file_name = '" + fileName + "';";
                        pstmt = con.prepareStatement(sql);
                        try {
                            count = pstmt.executeUpdate();
                        } catch (SQLException sqe) {
                            sqe.printStackTrace();
                            errorr += "  ||  error get ";
                        } catch (Exception e) {
                            errorr += "  ||error get   ";
                            e.printStackTrace();
                        }
                        if (count > 0) {
                            msg = "sucess";
                            System.out.println("uploaded Sucessfully");
                        }
                        if (errorr != "") {
                        }
                        lineReader.close();
                        con.commit();
                        if (pstmt != null) {
                            try {
                                pstmt.close();
                            } catch (Exception ex) {
                            }
                        }
                        if (con != null) {
                            try {
                                con.close();
                            } catch (Exception ex2) {
                            }
                        }
                        msg = "sucessFully Uploaded ";
                        response.sendRedirect("index.jsp?msg=" + msg);
                    } catch (ArrayIndexOutOfBoundsException ab) {
                        System.out.println("array Index Out of bound Exception" + ab);
                        msg = "Column Mismatch ";
                        response.sendRedirect("index.jsp?msg=" + msg);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    System.out.println("Folder Not created to upload");
                }
            } else if (type.equals("downloading")) {
//                PrintWriter out = response.getWriter();
                try {
                    Connection conn = config2.getconnection();
                    if (conn == null) {
                        System.out.println("Error : Could not connect to database");
                        return;
                    }

                    String csvFile = request.getRealPath("/") + "Report.txt";
                    FileWriter writer = new FileWriter(csvFile);
                    String fname = "Report";

                    PreparedStatement prest = null;
                    ResultSet rs = null;

                    String sql0 = "";
                    sql0 = "SELECT * FROM tbl_allocated_data";

                    prest = conn.prepareStatement(sql0);
                    rs = prest.executeQuery();
                    System.out.println("Query to get Service =" + sql0);

//                  -------------------------  fetcing data from c2c_dp_master to export as tab delimited   ---------------------------------
                    int count = 0;
                    StringBuilder sb = new StringBuilder();
                    String headerArr = "NAME \t MOBILE NO \t ALTERNATE NO \t LANDLINE NO \t AGREEMENT NO \t LOAN NO \t LOAN AMOUNT \t LOAN TYPE \t FATHER NAME \t SPOUSE NAME \t HOME ADDRESS \t OFFICE ADDRESS \t PAN NO \t  AADHAR NO \t VEHICAL NO";

                    sb.append(headerArr);
                    sb.append("\n");
                    writer.append(sb.toString());

                    while (rs.next()) {
                        sb = null;
                        sb = new StringBuilder();
                        String arr[] = new String[15];
//                        arr[0] = rs.getString("id"); //testing
                        arr[0] = rs.getString("name");
                        arr[1] = rs.getString("mobile_no");
                        arr[2] = rs.getString("alternate_no");
                        arr[3] = rs.getString("landline_no");
                        arr[4] = rs.getString("agreement_no");
                        arr[5] = rs.getString("loan_no");
                        arr[6] = rs.getString("loan_amount");
                        arr[7] = rs.getString("loan_type");
                        arr[8] = rs.getString("father_name");
                        arr[9] = rs.getString("spouse_name");
                        arr[10] = rs.getString("home_address");
                        arr[11] = rs.getString("office_address");
                        arr[12] = rs.getString("pan_no");
                        arr[13] = rs.getString("aadhar_no");
                        arr[14] = rs.getString("vehical_no");
//                        arr[15] = rs.getString("file_name");
//                        arr[16] = rs.getString("insert_date");

                        int cnt = 0;
                        for (String value : arr) {
                            if (cnt == 0) {
                                sb.append(value);
                            } else {
                                sb.append("\t" + value);
                            }
                            cnt++;
                        }
                        sb.append("\n");
                        writer.append(sb.toString());
                    }

                    writer.flush();
                    writer.close();

                    download(request, response, csvFile, fname);
                    rs.close();

                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Exception e) {
                        }
                    }
                    if (prest != null) {
                        try {
                            prest.close();
                        } catch (Exception e) {
                        }
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (Exception e) {
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception \n\n\n\n\n\n\\n||" + e + "\n\n\n\n\n\n");
                }
            } else if (type.equalsIgnoreCase("viewData")) {
                PrintWriter out = response.getWriter();
                try {
                    Connection conn = config2.getconnection();
                    if (conn == null) {
                        out.println("Error : Could not connect to database");
                        return;
                    }
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("select * from tbl_allocated_data");
                    String data = "";
                    out.println("<table id=\"example\" class=\"table table-striped table-bordered\" style=\"width:100%\">");
//                    out.println("<thead><tr><th scope=\"col\">id</th><th scope=\"col\">Mobile</th><th scope=\"col\">Alternate No</th><th scope=\"col\">Landline No</th><th scope=\"col\">Agreement_no</th>"
//                            + "<th scope=\"col\">Loan No</th><th scope=\"col\">Loan Amount</th><th scope=\"col\">Loan Type </th><th scope=\"col\">Name </th><th scope=\"col\">Father name</th>"
//                            + "<th scope=\"col\">Spose Name</th><th scope=\"col\">Home Add</th><th scope=\"col\">Office Add</th><th scope=\"col\">Pan</th>"
//                            + "<th scope=\"col\">Aadhar</th><th scope=\"col\">Vehical</th><th scope=\"col\">File Name</th><th scope=\"col\">Insert Date</th>"
//                            + "</tr></thead><tbody>");

                    out.println("<thead><tr><th scope=\"col\">Id </th><th scope=\"col\">Name </th><th scope=\"col\">Mobile</th><th scope=\"col\">Alternate No</th><th scope=\"col\">Landline No</th><th scope=\"col\">Agreement_no</th>"
                            + "<th scope=\"col\">Loan No</th><th scope=\"col\">Loan Amount</th><th scope=\"col\">Loan Type </th><th scope=\"col\">Father name</th>"
                            + "<th scope=\"col\">Spose Name</th><th scope=\"col\">Home Add</th><th scope=\"col\">Office Add</th><th scope=\"col\">Pan</th>"
                            + "<th scope=\"col\">Aadhar</th><th scope=\"col\">Vehical</th><th scope=\"col\">File Name</th><th scope=\"col\">Insert Date</th>"
                            + "</tr></thead><tbody>");
                    int countt = 3;
                    while (rs.next()) {
                        data = "";
                        String id = rs.getString("id"); //testing
                        String mobile_no = rs.getString("mobile_no");
                        String alternate_no = rs.getString("alternate_no");
                        String landline_no = rs.getString("landline_no");
                        String agreement_no = rs.getString("agreement_no");
                        String loan_no = rs.getString("loan_no");
                        String loan_amount = rs.getString("loan_amount");
                        String loan_type = rs.getString("loan_type");
                        String name = rs.getString("name");
                        String father_name = rs.getString("father_name");
                        String spouse_name = rs.getString("spouse_name");
                        String home_address = rs.getString("home_address");
                        String office_address = rs.getString("office_address");
                        String pan_no = rs.getString("pan_no");
                        String aadhar_no = rs.getString("aadhar_no");
                        String vehical_no = rs.getString("vehical_no");
                        String file_name = rs.getString("file_name");
                        String insert_date = rs.getString("insert_date");

//                        data= id+"||"+mobile_no+"||"+alternate_no+"||"+landline_no+"||"+agreement_no+"||"+loan_no+"||"+loan_amount+"||"+loan_type+"||"+name+"||"+father_name+"||"+spouse_name+"||"
//                                +home_address+"||"+office_address+"||"+pan_no+"||"+aadhar_no+"||"+vehical_no+"||"+file_name+"||"+insert_date+"||";
                        out.println("<tr>"
                                + "<td>" + id + "</td>"
                                + "<td>" + name + "</td>"
                                + "<td>" + mobile_no + "</td>"
                                + "<td>" + alternate_no + "</td>"
                                + "<td>" + landline_no + "</td>"
                                + "<td>" + agreement_no + "</td>"
                                + "<td>" + loan_no + "</td>"
                                + "<td>" + loan_amount + "</td>"
                                + "<td>" + loan_type + "</td>"
                                + "<td>" + father_name + "</td>"
                                + "<td>" + spouse_name + "</td>"
                                + "<td>" + home_address + "</td>"
                                + "<td>" + office_address + "</td>"
                                + "<td>" + pan_no + "</td>"
                                + "<td>" + aadhar_no + "</td>"
                                + "<td>" + vehical_no + "</td>"
                                + "<td>" + file_name + "</td>"
                                + "<td>" + insert_date + "</td>"
                                + "</tr>");
                        countt++;

                    }
                    out.println("</tbody></table>");
                    conn.close();
                    st.close();
                    rs.close();
                } catch (Exception e) {
                    out.println("EXCEPTION caught inn views " + e);
                }
            }
        } catch (FileNotFoundException fe) {
            msg = "File Not Found " + fe;
            response.sendRedirect("index.jsp?msg=" + msg);
            System.out.println("File not Found Exception = " + fe);
        } catch (Exception e) {
            // out.println(e);
            msg = "Exception" + e;
            response.sendRedirect("index.jsp?msg=" + msg);
            System.out.println(" Exception in SaveData Servlet =" + e);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    public boolean uploadFile(final InputStream is, final String path) {
        boolean test = false;
        try {
            final byte[] byt = new byte[is.available()];
            int rr = 0;
            final FileOutputStream fops = new FileOutputStream(path);

            while ((rr = is.read(byt)) != -1) {
                fops.write(byt, 0, rr);
                System.out.println(fops);
            }
            fops.flush();
            fops.close();
            test = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Exception Caught while File Uploading=" + e);
        }
        return test;
    }

    private String getFileName(final Part part) {
        final String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= " + contentDisp);
        final String[] arr$;
        final String[] tokens = arr$ = contentDisp.split(";");
        for (final String token : arr$) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    private void download(HttpServletRequest request, HttpServletResponse response, String path, String fileName) {
        String dataDirectory = request.getServletContext().getRealPath("/");
        Path file = Paths.get(dataDirectory, "Report.txt");

        response.setContentType("application/csv");
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".txt");
        try {
            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Exception \n\n\nn---" + ex);
        }
    }
}
