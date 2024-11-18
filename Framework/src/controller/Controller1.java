package controller;

import java.io.IOException;
import java.util.Vector;

import Annotations.*;
import mg.prom16.ModelView;
import mg.prom16.MySession;
import models.Employe;

@Controller
public class Controller1 {

    @Get
    @Url(value = "/message")
    public String get_message(String message) {
        return message;
    }

    @Get
    @Url(value = "/pageNotFound")
    public ModelView pageNotFound() { 
        ModelView modelView = new ModelView();
        modelView.setUrl("/views/ErrorPage.jsp");
        modelView.addObject("message", "Page Not Found");
        modelView.addObject("code", 404);
        return modelView;
    }

    @Get
    @Url(value = "/date")
    public java.util.Date get_Date() {
        return new java.util.Date();
    }

    // @Get(value = "/employe")
    // public ModelView get_employe(@Param(name = "id")String id, @Param(name = "nom")String nom, @Param(name = "prenom")String prenom){
    //     ModelView mv = new ModelView();
    //     mv.setUrl("/views/Employe.jsp");
    //     mv.addObject("id", id);
    //     mv.addObject("nom", nom);
    //     mv.addObject("prenom", prenom);
    //     return mv;
    // }


    @Get
    @Url(value = "/employe")
    public ModelView get_employe(@RequestBody Employe employe) {
        ModelView mv = new ModelView();
        mv.setUrl("/views/Employe.jsp");
        mv.addObject("employe", employe);
        return mv;
    }

    @Restapi
    @Get
    @Url(value = "/listemploye")
    public ModelView list_employe() {
        ModelView mv = new ModelView();
        // mv.setUrl("/views/ListEmploye.jsp");

        Employe employe1 = new Employe();
        employe1.setId("1");
        employe1.setNom("RAKOTOARISON");
        employe1.setPrenom("Yohan");

        Employe employe2 = new Employe();
        employe2.setId("2");
        employe2.setNom("RAKOTOARISON");
        employe2.setPrenom("Carine");

   
        
        Vector<Employe> listEmploye = new Vector<>();
        listEmploye.add(employe1);
        listEmploye.add(employe2);

        mv.addObject("listemploye", listEmploye);
        return mv;
    }

    @Restapi
    @Url(value = "/yohan")
    public Employe get_Employe(){
        Employe employe2 = new Employe();
        employe2.setId("2");
        employe2.setNom("RAKOTOARISON");
        employe2.setPrenom("Yohan");

        return employe2;
    }

    @Post
    @Url("/uploadImage")
    public ModelView uploadImage(MySession mySession, @FileParam("imageFile") String imageFilePath) throws IOException {

        ModelView modelView = new ModelView();
        modelView.setUrl("/views/uploadSuccess.jsp");
        modelView.addObject("fileName", imageFilePath);
        return modelView;
    }
}