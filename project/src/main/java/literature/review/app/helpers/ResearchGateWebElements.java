package literature.review.app.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component
public class ResearchGateWebElements {
	
	private List<String> options = Arrays.asList("Overview","Contributions","Departments","Members","Member stats");
	private List<String> profile_options = Arrays.asList("Overview","Research");
	
	//verifica si dado la cabecera de opciones, la opcion department esta habilitada
	//WebElement->driver.findElements(By.cssSelector("[class='tab-bar-plain-inner'] > a"))
	public boolean isDepartmentOptionEnable(List<WebElement> elements)
	{
		boolean res = true;
		try {
			for (WebElement web : elements) {
				if( web.getAttribute("class").equalsIgnoreCase("btn btn-large btn-inactive")
						&& web.getText().trim().equalsIgnoreCase(options.get(2))) {
					res = false; break; 
				}
			}
		}catch (NoSuchElementException e) {
			System.err.println("function :isDepartmentOptionEnable()"+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	
	//verifica si la lista de Instituciones, tiene personas como miembros
	//Webelement -> driver.findElements(By.cssSelector("[class='list'] > div")
	public boolean isDepartmentHasMembers(WebElement element) throws NoSuchElementException
	{
		List<WebElement> stats = element.findElements(By.cssSelector("[class='stats'] > div"));
		//System.out.println(stats.size());
		return stats.size() > 1;
	}
	
	//dado un nombre de opcion en una lista de WebElements, retorna WebElement que concide con TEXT
	//se condiciona a que webelement tenga TEXT
	public WebElement getWebElementByName(String name, List<WebElement> elments)
	{
		WebElement res = null;
		try {
			for (WebElement web : elments) {
				if(web.getText().trim().equalsIgnoreCase(name.trim())){	
					res = web; break;
				}
			}
		}catch (NoSuchElementException e) {
			System.err.println("function : getWebElementByName(), "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	public List<Integer> splitNumber(int parts, int number)
	{
		List<Integer> res = new ArrayList<Integer>();
		if(number > parts)
		{
			int num =  (int) Math.floor(number / parts);
			for(int i=1;i<=parts-1;i++)
				res.add(num*i);
			res.add( number );
		}else
			res.add(number);
		
		return res;
	}

	public int getPartes(int position)
	{
		int res = 0;
		if(position > 0 && position < 1000)
			res = 2;
		else if(position > 1000 && position < 2000)
			res = 3;
		else if(position > 2000 && position < 3000)
			res = 4;
		else if(position > 3000 && position < 4000)
			res = 5;
		else if(position > 4000 && position < 5000)
			res = 6;
		else if(position > 5000 && position < 6000)
			res = 7;
		else if(position > 6000 && position < 7000)
			res = 8;
		else if(position > 8000 && position < 9000)
			res = 9;
		else if(position > 9000 && position < 10000)
			res = 10;
		else
			res = 15;
		
		return res;
	}
	
	
	// verifica si dado la cabecera de opciones, la opcion members
	// WebElement->driver.findElements(By.cssSelector("[class='tab-bar-plain-inner'] > a"))
	public boolean isMemberOptionEnable(List<WebElement> elements) {
		boolean res = false;
		for (WebElement web : elements) {
			if (web.getText().trim().equalsIgnoreCase(options.get(3)) && "btn btn-large ajax-page-load members".trim()
					.equalsIgnoreCase(web.getAttribute("class").trim())) {
				res = true;
				break;
			}

		}
		return res;
	}
	
	/*
	 * funcion que busca por texto y funcion css en una lista de Webelement
	 * @param : List<WebElement> options; lista donde hace busqueda
	 * @param : String texo, el texto a buscar
	 * @param : String css_class, el atributo css de Webelement
	 * return: WebElement 
	 */
	public WebElement fidnElementByTextAttValAtt(List<WebElement> options, String text, String css_att,String css_att_val)
	{
		WebElement res = null;
		for (WebElement web : options) {
			
			if( text.trim().equalsIgnoreCase( web.getText().trim() ) &&
				css_att_val.trim().equalsIgnoreCase( web.getAttribute(css_att) )	)
			{
				res = web; break;
			}
			
		}
		return res;
	}
	
	public WebElement findWebElementByAttribute(String attribute, String value,List<WebElement> elements)
	{
		WebElement res = null;
		try {
			for (WebElement web : elements) {
				if (web.getAttribute(attribute).trim().equalsIgnoreCase(value.trim())) {
					res = web;
					break;
				}
			}
			
		}catch (NoSuchElementException e) {
			System.err.println("findWebElementByAttribute : Verifique que el elemento tenga los atts enviados"+attribute+" = "+value);
			e.printStackTrace();
		}
		return res;
	}
	
	//funcion que dado la caja de about de profile busca la caja disciplines
	//se considera que el profile ya verifico que tiene la caja about != null
	public Map< String, List<String> > obtainProfileDisciplinesSkills(WebElement about_box)
	{
		Map<String, List<String> > res = new HashMap<>();
		
		List<String> disciplines_list = new ArrayList<>();
		List<String> skills_list = new ArrayList<>();
		
		List<WebElement> box_options = about_box.findElements(By.cssSelector("[class='nova-c-card nova-c-card--spacing-none nova-c-card--elevation-1-above'] > div"));																  
		WebElement box_body = findWebElementByAttribute("class", "nova-c-card__body nova-c-card__body--spacing-inherit", box_options);
		
		try {
		
			if( Objects.nonNull(box_body) ) {
				List<WebElement> div_items = box_body.findElements(By.xpath("./div/div[@class='nova-o-stack__item']/*"));
				
				if(div_items.size() > 0)//tiene disciplinas o expertise
				{
					for (WebElement div : div_items) {
						String about = div.findElement(By.tagName("strong")).getText();
						
						if(about.contains("Disciplines")) //existe disciplinas
						{
							List<WebElement> disciplines = div.findElements(By.xpath("./div[2]/div//div[@class='nova-l-flex__item']/*"));
							for (WebElement dis : disciplines) {
								
								if( dis.getTagName().equalsIgnoreCase("a") ){
									disciplines_list.add( dis.findElement(By.xpath(".//span")).getText() );
									//System.out.println(dis.findElement(By.tagName("span")).getText());
								}
							}
							
							System.out.println("DIS->"+disciplines.size());
							//System.out.println("DIS->"+div.getAttribute("class"));
						}else if(about.contains("Skills") ){//existe skills
							List<WebElement> skills = div.findElements(By.xpath("./div[2]/div//div[@class='nova-l-flex__item']/*"));
							
							for (WebElement ski : skills) {
								if(ski.getTagName().equalsIgnoreCase("a"))
									skills_list.add(ski.getText());
							}
							System.out.println("SKI->"+skills.size());
						}
					}
				}
		
			}else System.out.println("null box body");
			
			res.put("disciplines", disciplines_list);
			res.put("skills", skills_list);
			
		}catch (NoSuchElementException e) {
			System.err.println("Function: obtainProfileDisciplinesSkills(), "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	
	public List<ResearchGateProfileContributions> findContributionsResearchGate(WebElement contribution_item)
	{
		List<ResearchGateProfileContributions> res = new ArrayList<>();
		
		try {
			List<WebElement> divs = contribution_item.findElements(By.cssSelector("[class='nova-c-card nova-c-card--spacing-xl nova-c-card--elevation-1-above'] > div"));
			WebElement body = findWebElementByAttribute("class", "nova-c-card__body nova-c-card__body--spacing-inherit", divs);
		
			if(Objects.nonNull(body)) {
				
				List<WebElement> spinner = body.findElements(By.cssSelector("[class='nova-o-stack nova-o-stack--gutter-xl nova-o-stack--spacing-none nova-o-stack--no-gutter-outside'] > div"));
				if(spinner.size() > 1)
					throw new NoSuchElementException("Error al cargar el profile, verifique que toda la pagina haya cargado"+contribution_item.getAttribute("class"));
				
				List<WebElement> items = body.findElements(By.xpath("./div/div/div/div[@class='nova-o-stack__item']"));
				int cantidad = 1;
				
				for (WebElement web : items) 
				{
					List<WebElement> publication_items = web.findElements(By.xpath("./div/div[1]/div/div[@class='nova-v-publication-item__body']/div/div[@class='nova-v-publication-item__stack-item']"));
					ResearchGateProfileContributions contributions = new ResearchGateProfileContributions();
					
					for (WebElement pub : publication_items) 
					{
						WebElement type = pub.findElement(By.xpath("./node()"));
						String att = type.getAttribute("class");
						
						switch (att) 
						{
						//head
						case "nova-e-text nova-e-text--size-l nova-e-text--family-sans-serif nova-e-text--spacing-none nova-e-text--color-inherit nova-v-publication-item__title":
							WebElement publication_head = pub.findElement(By.xpath("./div/a"));
							System.out.println(cantidad+": "+publication_head.getText());
							
							contributions.setPub_title(publication_head.getText());
							contributions.setPub_url(publication_head.getAttribute("href"));
							break;
						//body
						case "nova-v-publication-item__meta":
							//List<WebElement> body_opts = pub.findElements(By.cssSelector("[class='nova-v-publication-item__meta'] div")); 
							List<WebElement> body_opts = pub.findElements(By.xpath("./div/div[*]"));
														
							for (WebElement bod : body_opts) {
								
								WebElement child = bod.findElement(By.cssSelector("*:first-child"));
								
								if( bod.getAttribute("class").trim()
										.equalsIgnoreCase("nova-v-publication-item__meta-left") 
										&& child.getTagName().equalsIgnoreCase("span")
								  ) 
								{
										WebElement left_opts = bod.findElement(By.xpath("./node()"));
										
										if(Objects.equals(left_opts.getTagName(), "span")) {
											//retorna tipo publicacion
											String tipo_public = left_opts.getText();
											System.out.println("Document Type:"+tipo_public);
											
											contributions.setPub_type(tipo_public);
										}						
								}	
								else if( bod.getAttribute("class").trim()
										.equalsIgnoreCase("nova-v-publication-item__meta-right") 
										&& child.getTagName().equalsIgnoreCase("ul")
										)
								{
									//List<WebElement> rigth_opts = bod.findElements(By.xpath("./ul/li[@class='nova-e-list__item nova-v-publication-item__meta-data-item']"));
									List<WebElement> rigth_opts = bod.findElements(By.xpath("./ul/li[*]"));
									String date = "";
									String event_name = "";
																		
									if(rigth_opts.size() == 1)
										date = rigth_opts.get(0).findElement(By.xpath("./* [last()]") ).getText();
									else if(rigth_opts.size() == 2 || rigth_opts.size() == 3) {
										date = rigth_opts.get(0).findElement(By.xpath("./* [last()]") ).getText();
										event_name= rigth_opts.get(1).findElement(By.xpath("./* [last()]") ).getText();
										
//										for (WebElement right : rigth_opts) {
//											//WebElement r = right.findElement(By.cssSelector("li *:last-child"));
//											WebElement r = right.findElement(By.xpath("./* [last()]") );
//											System.err.println("TagName ->"+r.getTagName());
//											if(r.getTagName().equalsIgnoreCase("time"))
//												System.out.println("TIME->"+r.getText() );
//											else if(r.getTagName().equalsIgnoreCase("span"))
//													System.out.println("SPAN->"+r.getText() );
//										}
									}
									System.out.println("Date: "+date);
									System.out.println("Event Name:"+event_name);
									
									contributions.setPub_date(date);
									contributions.setPub_event(event_name);
								}
							}
							break;
						//footer
						case "nova-e-list nova-e-list--size-m nova-e-list--type-inline nova-e-list--spacing-none nova-v-publication-item__person-list":
							List<WebElement> authors = pub.findElements(By.xpath("./ul/li[@class='nova-e-list__item']"));
							boolean isAuthorsComplete = true;
							List<String> author_names = new ArrayList<>(); 
							
							for (WebElement aut : authors) {
								WebElement parts = aut.findElement(By.xpath("./node()"));
								
								if( parts.getTagName().equalsIgnoreCase("span"))
									isAuthorsComplete = false;
								else if(parts.getTagName().equalsIgnoreCase("a"))
								{
									WebElement fullname = aut.findElement(By.xpath("./a/span[@class='nova-v-person-inline-item__fullname']"));
									System.out.println("Author->"+fullname.getText());
									author_names.add(fullname.getText());
								}
							}
							System.out.println("All authors: "+isAuthorsComplete);
							contributions.setHasAllAuthors(isAuthorsComplete);
							contributions.setAuthors(author_names);
							
							break;
						default:
							System.err.println("Error option : "+att);
							break;
						}
					}
					res.add(contributions);
					cantidad++;
					System.out.println("================================");
				}
			}
		}catch (NoSuchElementException e) {
			System.err.println("function :findContributionsResearchGate(): Error al buscar el elemento :"+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	//busqueda de la cantidad de contribuciones del researcher
	public int findContributionNumberResearchGate(WebElement menu_item)
	{
		String CONTRIBUTIONS = "All";
		int contribution_number = 0;
		
		try 
		{
			WebElement nav = menu_item.findElement(By.tagName("nav"));
			List<WebElement> buttoms = nav.findElements(By.xpath("./div/div//button"));
			
			for (WebElement web : buttoms) {
				if(web.getAttribute("class").trim()
						.equalsIgnoreCase("nova-c-nav__item profile-contributions-menu__tab-second-level".trim()) ) {
					
					String texto = web.findElement(By.xpath("./div/div")).getText();
					if(texto.indexOf(CONTRIBUTIONS) > -1 ){
						contribution_number = Integer.parseInt( texto.replaceAll("[^0-9?!\\.]","") );
					}
				}
			}
		}catch(NoSuchElementException e) {
			System.out.println("findContributionNumberResearchGate :Attribute exception "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Contribution Number: "+contribution_number);
		return contribution_number;
	}
	
	//solo busca en opciones de profile researcher
	public WebElement findInProfileOptions(List<WebElement> options, String text)
	{
		WebElement res = null;
		try {
			for (WebElement web : options) {
				if(profile_options.contains(text))
				{
					if(
							web.findElement(By.xpath("./div/div")).getText().toString().trim().equalsIgnoreCase( text)
							&& web.getAttribute("aria-disabled").equalsIgnoreCase("false")
					  ) 
					{
						res = web;
						break;
					}
				}	
			}
		}catch (NullPointerException n) {
			System.err.println("Function: findInProfileOptions(),"+"Verifique que el elemento tenga los atts enviados"+text);
		}
		return res;
	}
}
