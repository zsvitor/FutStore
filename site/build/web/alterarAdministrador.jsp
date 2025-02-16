<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Alterar Administrador</title>
    <link rel="stylesheet" href="./css/alterarAdministrador.css">
</head>
<body>
    <div class="form-container">
        <div class="form-box">
            <h2>Alterar Dados</h2>
            <%
                int id = Integer.parseInt(request.getParameter("id"));
                Connection conecta = null;
                PreparedStatement st = null;
                ResultSet rs = null;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conecta = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/banco", "root", "1234");
                    String query = "SELECT * FROM administradores WHERE id = ?";
                    st = conecta.prepareStatement(query);
                    st.setInt(1, id);
                    rs = st.executeQuery();
                    if (rs.next()) {
                        String nome = rs.getString("nome");
                        String email = rs.getString("gmail");
                        String cpf = rs.getString("cpf");
                        String grupo = rs.getString("grupo");
                        String status = rs.getString("status");
                        String senhaAntiga = rs.getString("senha"); // Recuperando a senha encriptada
            %>
            <form id="alterarForm" action="salvarAlteracaoAdministrador.jsp" method="POST" onsubmit="return validarFormulario()">
                <input type="hidden" name="id" value="<%= id %>">
                <input type="hidden" name="email" value="<%= email %>">
                
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" size="100" maxlength="100" value="<%= nome %>" required>

                <label for="cpf">CPF</label>
                <input type="text" id="cpf" name="cpf" size="14" maxlength="14" value="<%= cpf %>" required oninput="mascaraCPF(this)">

                <label for="senha">Nova Senha</label>
                <input type="password" id="senha" name="senha" size="255" maxlength="255" placeholder="Digite a nova senha (deixe em branco se não quiser alterar)">

                <label for="confirmarSenha">Confirmar Nova Senha</label>
                <input type="password" id="confirmarSenha" name="confirmarSenha" size="255" maxlength="255" placeholder="Digite novamente a nova senha">

                <input type="hidden" name="senhaAntiga" value="<%= senhaAntiga %>"> <!-- Armazenando a senha antiga -->

                <label for="grupo">Grupo</label>
                <select id="grupo" name="grupo" required>
                    <option value="Administrador" <%= "Administrador".equals(grupo) ? "selected" : "" %>>Administrador</option>
                    <option value="Estoquista" <%= "Estoquista".equals(grupo) ? "selected" : "" %>>Estoquista</option>
                </select>
                
                <button type="submit">Alterar</button>
            </form>
            <%  
                    }
                } catch (Exception e) {
                    out.print("Erro: " + e.getMessage());
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (st != null) st.close();
                        if (conecta != null) conecta.close();
                    } catch (Exception e) {
                        out.print("Erro ao fechar conexão: " + e.getMessage());
                    }
                }
            %>
        </div>
    </div>
    <script>
        function mascaraCPF(campo) {
            var cpf = campo.value.replace(/\D/g, '');
            if (cpf.length <= 11) {
                campo.value = cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
            }
        }
        function validarFormulario() {
            const senha = document.getElementById('senha').value;
            const confirmarSenha = document.getElementById('confirmarSenha').value;
            document.getElementById('senha').style.borderColor = '';
            document.getElementById('confirmarSenha').style.borderColor = '';
            
            if (senha !== '' && senha !== confirmarSenha) {
                alert("As senhas não coincidem!");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>
