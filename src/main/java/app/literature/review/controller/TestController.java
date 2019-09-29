package app.literature.review.controller;

import java.io.IOException;
import java.util.List;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONException;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import app.documents.MongoQueries;
import app.literature.review.model.DblpDocument;
import app.references.Mendeley;
import app.references.Referencia;


@Controller
@RequestMapping("/app")
public class TestController {

	public static void main(String[] args) throws Exception {
//		Mendeley m = new Mendeley();
//		m.obtainToken();
//  String a = "https://doi.org/10.1007/BF00289416";
//  String[] b=a.split("/");
//
//  String c ="";
//	 if(b[0].contains("http") && b[2].contains("doi.org"))
//	 {
//		 for(int i=3;i<b.length;i++)
//		 {
//			 c+=b[i]+"/";
//		 }
//		 c = c.substring(0,c.length()-1);
//	 }
//	 
//	 System.out.println(c);
	
		Mendeley mendeley = new Mendeley();
		Referencia r = mendeley.mendeleyConsulta("10.1007/BF01257084");
		System.out.println(r.getAbstract());
		
		
//		MongoQueries queries = new MongoQueries();
//		List<DblpDocument> q = queries.findByMendeleyState("", 10);
//		
//		for(DblpDocument d:q)
//		{
//			System.out.println(d.getTitle()+"=>"+d.getType());
//		}
	}

}
