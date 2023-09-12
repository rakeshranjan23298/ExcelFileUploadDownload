<%-- 
    Document   : index
    Created on : 22 Sep, 2022, 3:21:02 PM
    Author     : toss
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="mypack.parser"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>NOS Upload</title>
    </head>
    <body style="    background-color: beige;">
    <%
            String configFilepath = "";
            session.setAttribute("cfgpath", configFilepath);
            try {
                File f = new File(configFilepath);
                System.out.println(configFilepath);
                if (f.exists()) {
                    parser k = new parser();
                    k.parse(f);
                } else {
                    String rootPath = request.getRealPath("/");
                    f = new File(rootPath + "config.xml");
                    if (f.exists()) {
                        parser k = new parser();
                        k.parse(f);
                    } else {
                        rootPath += "\\";
                        f = new File(rootPath + "config.xml");
                        if (f.exists()) {
                            parser k = new parser();
                            k.parse(f);
                        } else {
                            out.println("Config file not found");
                        }
                    }
                }
            } catch (Exception e) {
                out.println(e);
            }

            PreparedStatement prest = null;
            ResultSet rs = null;
            String output11 = "";
        %> 
        <h1 style="align-items: center;margin-left: 30px;color: red;background-color: blanchedalmond;text-align: center;"> Upload Vanity Number</h1>
        <form style="margin-left: 30px;" action="" id="myform" method="post" enctype="multipart/form-data">
            <div style="text-align: center" >
                <h3>Select File to Upload:</h3>       
                
                
                <table >
                    <tr>
                        <td>
                        </td>
                    </tr>
                    <tr>
                        <input type="file" name="fileName" id="fileName" >
                        
                        <input type="submit" value="Upload" onclick="fun()">  
                    </tr>
                </table>
            </div>
            <br>
            <!--<input type="submit" value="Upload" onclick="fun()">-->
        </form>
        <script>
            function fun()
            {
                var d=document.getElementById("fileName").files.length;
                console.log(d);
                if(d!=0)
                {
                    document.getElementById("myform").action="Upload";
                }else 
                    alert("please Select ");
            }
        </script>
    </body>
</html>