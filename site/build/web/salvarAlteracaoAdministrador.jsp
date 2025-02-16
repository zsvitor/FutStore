<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Salvar Alteração</title>
</head>
<body>
    <%
        int id = Integer.parseInt(request.getParameter("id"));
        String nome = request.getParameter("nome");
        String cpf = request.getParameter("cpf");
        String senha = request.getParameter("senha");
        String grupo = request.getParameter("grupo");
        String email = request.getParameter("email");
        Connection conecta = null;
        PreparedStatement st = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conecta = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco", "root", "1234");
            if (senha != null && !senha.isEmpty()) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(senha.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                senha = hexString.toString();
            } else {
                senha = request.getParameter("senhaAntiga");
            }
            String query = "UPDATE administradores SET nome = ?, cpf = ?, senha = ?, grupo = ? WHERE id = ?";
            st = conecta.prepareStatement(query);
            st.setString(1, nome);
            st.setString(2, cpf);
            st.setString(3, senha);
            st.setString(4, grupo);
            st.setInt(5, id);
            st.executeUpdate();
            response.sendRedirect("listarAdministradores.jsp");  // Redirecionamento para a página de listagem
        } catch (Exception e) {
            out.print("Erro ao alterar dados: " + e.getMessage());
        } finally {
            try {
                if (st != null) st.close();
                if (conecta != null) conecta.close();
            } catch (Exception e) {
                out.print("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    %>
</body>
</html>
