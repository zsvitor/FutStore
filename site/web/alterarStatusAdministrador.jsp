<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.SQLException"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Alteração de Status</title>
    </head>
    <body>
        <%
            String id = request.getParameter("id");
            String novoStatus = request.getParameter("novoStatus"); 
            if (id != null && novoStatus != null) {
                try {
                    Connection conecta;
                    PreparedStatement st;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conecta = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco", "root", "1234");    
                    String query = "UPDATE administradores SET status = ? WHERE id = ?";
                    st = conecta.prepareStatement(query);
                    st.setString(1, novoStatus);
                    st.setInt(2, Integer.parseInt(id));
                    st.executeUpdate();                    
                    conecta.close();
                    response.sendRedirect("listarAdministradores.jsp");
                } catch (ClassNotFoundException | SQLException e) {
                    out.print("Erro ao atualizar o status: " + e.getMessage());
                }
            } else {
                out.print("Parâmetros inválidos.");
            }
        %>
    </body>
</html>
