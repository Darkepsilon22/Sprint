    package mg.prom16;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import Annotations.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
// import com.thoughtworks.paranamer.AdaptiveParanamer;
// import com.thoughtworks.paranamer.Paranamer;

public class FrontController extends HttpServlet {

    protected List<Class<?>> list_controller = new ArrayList<>();
    protected Map<String, Mapping> urlMappings = new HashMap<>();

   


    // Method to get the list of controllers
    protected void getControllerList(String package_name) throws ServletException, ClassNotFoundException {
        String bin_path = "WEB-INF/classes/" + package_name.replace(".", "/");
        bin_path = getServletContext().getRealPath(bin_path);
        File b = new File(bin_path);
        list_controller.clear();
        
        for (File onefile : b.listFiles()) {
            if (onefile.isFile() && onefile.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(package_name + "." + onefile.getName().split(".class")[0]);
                if (clazz.isAnnotationPresent(Controller.class))
                    list_controller.add(clazz);

                for (Method method : clazz.getMethods()) {
                    List<String> verbActions = new ArrayList<>(); // List to hold verb actions

                    // Handle @Get and @Post, and set default to @Get if none is present
                    if (method.isAnnotationPresent(Get.class)) {
                        verbActions.add("GET");

                        Mapping mapping = new Mapping(clazz.getName(), method, verbActions);
                    String key = method.getAnnotation(Get.class).value(); 
                        if (urlMappings.containsKey(key)) {
                            throw new ServletException("La méthode '" + urlMappings.get(key).getMethod().getName() +
                                    "' possède déjà l'URL '" + key + "' comme annotation, donc elle ne peut pas être assignée à la méthode '" +
                                    mapping.getMethod().getName() + "'");
                        } else {
                            urlMappings.put(key, mapping);
                        }
                    } if (method.isAnnotationPresent(Post.class)) {
                        verbActions.add("POST");

                        Mapping mapping = new Mapping(clazz.getName(), method, verbActions);
                        String key = method.getAnnotation(Post.class).value();
                        if (urlMappings.containsKey(key)) {
                            throw new ServletException("La méthode '" + urlMappings.get(key).getMethod().getName() +
                                    "' possède déjà l'URL '" + key + "' comme annotation, donc elle ne peut pas être assignée à la méthode '" +
                                    mapping.getMethod().getName() + "'");
                        } else {
                            urlMappings.put(key, mapping);
                        }
                    } if (verbActions.isEmpty()) {
                        verbActions.add("GET"); // Default to GET
                        Mapping mapping = new Mapping(clazz.getName(), method, verbActions);
                        String key = "/" + clazz.getSimpleName() + "/" + method.getName();  // Construct a default URL
                        if (!urlMappings.containsKey(key)) {
                            urlMappings.put(key, mapping); // Map the URL to the method
                        }
                    }
                }
            }
        }
    }

    protected Object invoke_Method(HttpServletRequest request, String className, Method method) throws IOException, NoSuchMethodException {
        Object returnValue = null;
        try {
            Class<?> clazz = Class.forName(className);
            method.setAccessible(true);
    
            Parameter[] methodParams = method.getParameters();
            Object[] args = new Object[methodParams.length];
    
            Enumeration<String> params = request.getParameterNames();
            Map<String, String> paramMap = new HashMap<>();
    
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                paramMap.put(paramName, request.getParameter(paramName));
            }
    
            for (int i = 0; i < methodParams.length; i++) {
                if (methodParams[i].getType().equals(MySession.class)) {
                    HttpSession session = request.getSession();
                    MySession mySession = new MySession(session);
                    args[i] = mySession;
                } else if (methodParams[i].isAnnotationPresent(RequestBody.class)) {
                    Class<?> paramType = methodParams[i].getType();
                    Object paramObject = paramType.getDeclaredConstructor().newInstance();
                    for (Field field : paramType.getDeclaredFields()) {
                        String paramName = field.isAnnotationPresent(FormParam.class) ? field.getAnnotation(FormParam.class).value() : field.getName();
                        if (paramMap.containsKey(paramName)) {
                            field.setAccessible(true);
                            field.set(paramObject, paramMap.get(paramName));
                        }
                    }
                    args[i] = paramObject;
                } else if (methodParams[i].isAnnotationPresent(Param.class)) {
                    String paramName = methodParams[i].getAnnotation(Param.class).name();
                    String paramValue = paramMap.get(paramName);
                    args[i] = paramValue;
                } else {
                    if (paramMap.containsKey(methodParams[i].getName())) {
                        args[i] = paramMap.get(methodParams[i].getName());
                    } else {
                        args[i] = null;
                    }
                }
            }
    
            Object instance = clazz.getDeclaredConstructor().newInstance();
            returnValue = method.invoke(instance, args);
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            String package_name = "controllerPackage"; 
            String pack = getServletContext().getInitParameter(package_name);
            if (pack == null) {
                throw new ServletException("Le package \"" + package_name + "\" n'est pas reconnu.");
            } else {
                getControllerList(pack);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    
        String url = request.getRequestURI().substring(request.getContextPath().length());
        Mapping mapping = urlMappings.get(url);
    
        if (mapping != null) {
            String requestMethod = request.getMethod(); // Récupérer la méthode HTTP utilisée
    
            // Vérifier si la méthode HTTP est autorisée pour cette URL
            if (!mapping.getVerbActions().contains(requestMethod)) {
                // Rediriger vers une page d'erreur ou utiliser la méthode `handleUnsupportedMethod()`
                ModelView mv = handleUnsupportedMethod(requestMethod, url); // Passez les paramètres nécessaires
                String viewUrl = mv.getUrl();
                HashMap<String, Object> data = mv.getData();
            
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
            
                RequestDispatcher dispatcher = request.getRequestDispatcher(viewUrl);
                dispatcher.forward(request, response);
                return;
            }
            
    
            try {
                Object returnValue = invoke_Method(request, mapping.getClassName(), mapping.getMethod());
    
                // Vérifier si l'annotation Restapi est présente pour une réponse JSON
                if (mapping.getMethod().isAnnotationPresent(Restapi.class)) {
                    Gson gson = new Gson();
                    String jsonResponse;
    
                    if (returnValue instanceof ModelView) {
                        ModelView modelView = (ModelView) returnValue;
                        jsonResponse = gson.toJson(modelView.getData());
                    } else {
                        jsonResponse = gson.toJson(returnValue);
                    }
    
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
    
                    try (PrintWriter out = response.getWriter()) {
                        out.print(jsonResponse);
                        out.flush();
                    }
                } else if (returnValue instanceof String) {
                    if (((String) returnValue).startsWith("redirect")) {
                        String redirectUrl = ((String) returnValue).split(":")[1];
                        response.sendRedirect(redirectUrl);
                    } else {
                        try (PrintWriter out = response.getWriter()) {
                            out.println("<p>Contenu de la méthode <strong>" + mapping.getMethod() + "</strong> : " + returnValue + "</p>");
                        }
                    }
                } else if (returnValue instanceof ModelView) {
                    ModelView modelView = (ModelView) returnValue;
                    String viewUrl = modelView.getUrl();
                    HashMap<String, Object> data = modelView.getData();
    
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
    
                    RequestDispatcher dispatcher = request.getRequestDispatcher(viewUrl);
                    dispatcher.forward(request, response);
                } else if (returnValue == null) {
                    throw new ServletException("La méthode \"" + mapping.getMethod() + "\" retourne une valeur NULL");
                } else {
                    throw new ServletException("Le type de retour de l'objet \"" + returnValue.getClass().getName() + "\" n'est pas pris en charge par le Framework");
                }
            } catch (NoSuchMethodException | IOException e) {
                throw new ServletException("Erreur lors de l'invocation de la méthode \"" + mapping.getMethod() + "\"", e);
            }
        } else {
            // Afficher une page d'erreur pour l'URL non associée
            request.setAttribute("message", "Pas de méthode associée à l'URL: \"" + url + "\"");
            request.setAttribute("code", "404 ERROR NOT FOUND");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/ErrorPage.jsp");
            dispatcher.forward(request, response);
        }
        
    }

    public ModelView handleUnsupportedMethod(String requestMethod, String url) {
        ModelView mv = new ModelView();
        mv.setUrl("/views/TestVerb.jsp"); // Redirige vers la page de gestion des méthodes non supportées
        mv.addObject("message", "La méthode HTTP \"" + requestMethod + "\" n'est pas autorisée pour cette URL: \"" + url + "\".");
        return mv;
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
