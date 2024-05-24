package mg.itu.controller;

import mg.itu.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@AnnotationController
public class TestController {

    @GET("/test")
    public void testMethod(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Test Method</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Test Method Executed</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
