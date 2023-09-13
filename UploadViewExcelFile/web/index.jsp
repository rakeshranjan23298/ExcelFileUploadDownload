
<%@page import="java.sql.Connection"%>
<%@page import="mypack.config2"%>
<%@page import="java.io.File"%>
<%@page import="mypack.parser"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%ResultSet resultset = null;%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        
       <script src="jquery-3.5.1.js" type="text/javascript"></script>
       <script src="jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="dataTables.bootstrap4.min.js" type="text/javascript"></script>
        <script>
         var msg="<%=request.getParameter("msg")%>";
         if( msg != 'null')
         {
            console.log("message not null");
//            document.getElementById("loader").style.display="block"
//            document.getElementById("upload").disabled = false;
            alert(msg);
            var url =    window.location.href;
            var new_url=url.split("?");

            msg = null;
            console.log("msg="+msg);
            window.open(new_url[0],"_self");
         }            
        </script>
        
        <title>Data Allocation</title>
    </head>
    <body onload="viewData();">
        <%
            try {
                String configFilepath = "";
                session.setAttribute("cfgpath", configFilepath);
                try {
                    File f = new File(configFilepath);
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

                String msg = "";
                String callerid = "";
                String callnumber = "";
                String lrefid = "";
                String lsid = "";
                String leads = "";
                String calltype = "";

                if (request.getParameterMap().containsKey("lrefid")) {
                    lrefid = request.getParameter("lrefid");
                    out.println("<input type='text' style='display:none' id='lrefid' value='" + lrefid + "'>");
                }
                if (request.getParameterMap().containsKey("transinfo")) {
                    String val = request.getParameter("transinfo");
                    if (!val.equals("")) {
                        out.println("This is the transferred call from " + val);
                    }
                }
                if (request.getParameterMap().containsKey("callerid")) {
                    callerid = request.getParameter("callerid");
                    out.println("<input type='text' style='display:none' id='leadcid' value='" + callerid + "'>");
                }
                if (request.getParameterMap().containsKey("callnumber")) {
                    callnumber = request.getParameter("callnumber");
                    out.println("<input type='text' style='display:none' id='callnumber' value='" + callnumber + "'>");
                }
                if (request.getParameterMap().containsKey("lsid")) {
                    lsid = request.getParameter("lsid");
                    out.println("<input type='text' style='display:none' id='leadsid' value='" + lsid + "'>");
                }
                if (request.getParameterMap().containsKey("leads")) {
                    leads = request.getParameter("leads");
                    out.println("<input type='text' style='display:none' id='leads' value='" + leads + "'>");
                }
                if (request.getParameterMap().containsKey("ctype")) {
                    calltype = request.getParameter("ctype");
                    out.println("<input type='text' style='display:none' id='calltype' value='" + calltype + "'>");
                }
                out.println("<input type='text' style='display:none' id='batch_name' >");
                out.println("<input type='text' style='display:none' id='batch_id' >");

                Connection conn = config2.getconnection();
                if (conn == null) {
                    out.println("Error : Could not connect to database");
                    return;
                }
                PreparedStatement prest = null;
                ResultSet rs = null;
                String output11 = "";
        %>            
        <div class="container" style="max-width: 1518px;">
            <div class="row">    
                <div class="col-lg-12 text-center " style="background-color: lightblue;color:black;margin-top: 3px;padding: 3px; margin-bottom: 25px;"> Data Allocation Window </div>
               
                <div class="col-lg-3"></div>
                <div class="col-lg-6">
                    <form id="myform" method="post" enctype="multipart/form-data">

                    <div class="form-group">
                        <h3 style="font-size: 1.25rem">Select tab-delimited text File  :</h3> 
                        <input type="file" class="form-control" id="fileName" name="fileName" accept=".txt" >
                    </div>                   

                        <button  type="button" style="margin-top: 1px; " id="upload" onclick="fncUpload();" class="btn btn-primary float-right" >Upload</button>
                        <a class='btn btn-primary float-left' style='text-decoration: none;margin-top: 1px; margin-right: 30px;' href="SaveData?type=downloading" > Download </a>                    
                    </form>
                </div>
                <div class="col-lg-3"></div>
                <!--------------------------------  --------------------------------------------------          Table               --------------------------------------------------------------------->                

            </div>
        </div>
            <div class="col-lg-12" style="margin-top: 20px;padding: 5px; " id="newid"></div>        
        <script>
            function fncUpload()  // to check file choosen or not
            {
                console.log("upload clicked");
                var d=document.getElementById("fileName").files.length;
                console.log(d);
                if(d!=0)
                {
                    document.getElementById("upload").disabled = true;
//                  document.getElementById("loader").style.display="block"
                    document.getElementById('myform').action = "SaveData";
                    document.getElementById("myform").submit();
                }else 
                    alert("please Select ");
            }            
          
          function fncDownload()
          {
              console.log(" download clicked")
              
          }

            function viewData()
            {
                console.log("Viewing data");
                var xmlhttp = null;
                if (window.XMLHttpRequest)
                {
                    xmlhttp = new XMLHttpRequest();
                } else
                {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                }
                xmlhttp.onreadystatechange = function ()
                {
                    if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
                    {
                        var response = xmlhttp.responseText;
//                        console.log("response =" + response);
                        test();
                        $("#newid").html(response);
                        $(document).ready(function (){
                            $("#example_filter").css("float","right");
                        });
                        
                    }
                }
                xmlhttp.open("GET", "SaveData?type=viewData", true);
                xmlhttp.send();
            }
         
            function test()
            {
                $(document).ready(function () {
                    $('#example').DataTable();
                });
            }


        </script>
    </body>
</html>
<%  if (resultset != null) {try {resultset.close();} catch (Exception e) {}}
        if (prest != null) {try {prest.close();} catch (Exception e) {}}
        if (conn != null) {try {conn.close();} catch (Exception e) {}}
    } catch (Exception e) {out.println(e);}
%>
