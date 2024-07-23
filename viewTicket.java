import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/viewTickets")
public class viewTickets extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String searchQuery = request.getParameter("searchQuery");
        String url = "jdbc:mysql://localhost:3306/ticket_db?serverTimezone=UTC";
        String username = "root";
        String password = ""; 

        try (PrintWriter out = response.getWriter()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);

                String query = "SELECT * FROM tickets";
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    query += " WHERE branche LIKE ? OR produit LIKE ?";
                }
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    String searchPattern = "%" + searchQuery + "%";
                    preparedStatement.setString(1, searchPattern);
                    preparedStatement.setString(2, searchPattern);
                }
                
                ResultSet resultSet = preparedStatement.executeQuery();

                out.println("<html>");
                out.println("<head><title>Liste des Tickets</title></head>");
                out.println("<body>");
                out.println("<h1>Liste des Tickets</h1>");
                
                out.println("<table border='1'>");
                out.println("<tr><th>ID</th><th>Branche</th><th>Produit</th><th>Description</th></tr>");

                while (resultSet.next()) {
                    out.println("<tr>");
                    out.println("<td>" + resultSet.getInt("id") + "</td>");
                    out.println("<td>" + resultSet.getString("branche") + "</td>");
                    out.println("<td>" + resultSet.getString("produit") + "</td>");
                    out.println("<td>" + resultSet.getString("description") + "</td>");
                    out.println("</tr>");
                }

                out.println("</table><br>");
                out.println("<form method='get' action='viewTickets'>");
                out.println("<h2>Rechercher par branche ou produit</h2>: <input type='text' name='searchQuery'/>");
                out.println("<input type='submit' value='Rechercher'/>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                out.println("Erreur lors de la récupération des données.");
                out.println("<br/>");
                out.println("Message d'erreur: " + e.getMessage());
            }
        }
    }

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
}
