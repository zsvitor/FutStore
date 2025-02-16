<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Salvar Administrador</title>
    </head>
    <body>
        <%
            String nome, email, senha, cpf, grupo;
            nome = request.getParameter("nome");
            email = request.getParameter("email");
            senha = request.getParameter("senha");
            cpf = request.getParameter("cpf");
            grupo = request.getParameter("grupo");
            try {
                Connection conecta;
                PreparedStatement st;
                Class.forName("com.mysql.cj.jdbc.Driver");
                conecta = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco", "root", "1234");
                st = conecta.prepareStatement("INSERT INTO administradores(nome, gmail, senha, cpf, grupo) VALUES(?,?,?,?,?)");
                st.setString(1, nome);
                st.setString(2, email);
                st.setString(3, senha);
                st.setString(4, cpf);
                st.setString(5, grupo);
                st.executeUpdate();
                response.sendRedirect("listarAdministradores.jsp"); 
            } catch (Exception x) {
                out.print("Erro ao cadastrar administrador: " + x.getMessage());
            }
        %>
    </body>
</html>
