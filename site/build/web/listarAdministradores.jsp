<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <link rel="stylesheet" href="./css/listarAdministradores.css">
        <title>Listagem de Usuários</title>
        <script>
            function confirmarAlteracao(id, statusAtual) {
                let novoStatus = statusAtual === 'Ativo' ? 'Inativo' : 'Ativo';
                if (confirm('Deseja realmente alterar o status para ' + novoStatus + '?')) {
                    window.location.href = 'alterarStatusAdministrador.jsp?id=' + id + '&novoStatus=' + novoStatus;
                }
            }
        </script>
    </head>
    <body>
        <h2>Lista de Usuários</h2>
        <form method="GET">
            <label for="filtroNome">Filtrar por nome:</label>
            <input type="text" id="filtroNome" name="filtroNome" value="<%= request.getParameter("filtroNome") != null ? request.getParameter("filtroNome") : "" %>">
            <input type="submit" value="Filtrar">
            <a href="listarAdministradores.jsp">Limpar</a>
        </form>
        <table border="1">
            <tr>
                <th>Nome</th>
                <th>Email</th>
                <th>Status</th>
                <th>Grupo</th>
                <th>Ações</th>
            </tr>           
            <%
                try {
                    Connection conecta;
                    PreparedStatement st;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conecta = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco", "root", "1234");              
                    String filtroNome = request.getParameter("filtroNome");
                    String query = "SELECT * FROM administradores";
                    if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                        query += " WHERE nome LIKE ?";
                    }
                    st = conecta.prepareStatement(query);
                    if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                        st.setString(1, "%" + filtroNome + "%");
                    }                   
                    ResultSet rs = st.executeQuery();
                    while (rs.next()) {
            %>
            <tr>
                <td><%= rs.getString("nome") %></td>
                <td><%= rs.getString("gmail") %></td>
                <td><%= rs.getString("status") %></td>
                <td><%= rs.getString("grupo") %></td>
                <td>
                    <a href="alterarAdministrador.jsp?id=<%= rs.getInt("id") %>">Alterar</a> |
                    <a href="#" onclick="confirmarAlteracao(<%= rs.getInt("id") %>, '<%= rs.getString("status") %>')">
                        <%= rs.getString("status").equals("Ativo") ? "Inativar" : "Reativar" %>
                    </a>
                </td>
            </tr>
            <%
                    }
                    conecta.close();
                } catch (Exception x) {
                    out.print("Erro: " + x.getMessage());
                }
            %>
        </table>      
        <br>
        <a href="cadastrarAdministrador.html">+ Adicionar Novo Usuário</a>
    </body>
</html>
