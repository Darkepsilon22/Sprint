package mg.itu.prom16;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.annotation.AnnotationController;
import mg.itu.annotation.GET;

public class FrontController extends HttpServlet {

    private String path;
    private ArrayList<Class<?>> list;
    private Map<String, Mapping> urlMappings;

    public void init() throws ServletException {
        super.init();
        urlMappings = new HashMap<>();
        scanner();
    }
    
    private void scanner(){
        String pack = getServletConfig().getInitParameter("ControllerPackage");
        try {
            list = getClassesInSpecificPackage(pack);
            for (Class<?> controller : list) {
                Method[] methods = controller.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GET.class)) {
                        GET getAnnotation = method.getAnnotation(GET.class);
                        String url = getAnnotation.value();
                        Mapping mapping = new Mapping(controller.getName(), method.getName());
                        urlMappings.put(url, mapping);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = new ArrayList<>();
        }
    }
    
    private ArrayList<Class<?>> getClassesInSpecificPackage(String packageName) throws IOException, ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(AnnotationController.class)) {
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (urlMappings.containsKey(pathInfo)) {
            Mapping mapping = urlMappings.get(pathInfo);
            try {
                Class<?> controllerClass = Class.forName(mapping.getClassName());
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                Method method = controllerClass.getMethod(mapping.getMethodName(), HttpServletRequest.class, HttpServletResponse.class);
                method.invoke(controllerInstance, request, response);
            } catch (Exception e) {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("Erreur: " + e.getMessage());
                    e.printStackTrace(out);
                }
            }
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("No method associated with the URL: " + pathInfo);
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
