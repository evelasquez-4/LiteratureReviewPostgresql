package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import literature.review.app.helpers.ResearchGateInstitutions;
import literature.review.app.helpers.ResearchGateMemberProfile;
import literature.review.app.helpers.ResearchGateProfileContributions;
import literature.review.app.helpers.ResearchGateWebElements;
import literature.review.app.model.Departments;
import literature.review.app.model.Institutions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@Service 
public class ResearchGateService{
	
	@Autowired
	private InstitutionsService institution;
	@Autowired
	private DepartmentsService depatment;
	@Autowired
	private CountriesService country;
	
	
	public void testGoogleSearch(String url) throws InterruptedException {

//		WebDriver driver = startDriver("http://127.0.0.1:9515");
//		driver.get("http://google.com");
		
		WebDriver driver = startDriver("http://127.0.0.1:9515");
		
//		Prueba obtainResearchGateProfile
//		Departments dep = new Departments();
//		dep.setLinks(url);
//		List<ResearchGateMemberProfile> a = obtainResearchGateProfile(dep, driver);
//		System.out.println(a.size());
//		a.forEach(el->{
//			System.out.println(el.getNames());
//			System.out.println(el.getProfile_url());
//		});
		
		
//		
		/*
		//test#1 List<ResearchGateMemberProfile> obtainResearchGateProfile(Departments dep, WebDriver driver)
		loginOnResearchGate(driver);
		List<Departments> dep = this.depatment.listDepartmentsByInsitution("AR", 1, "1.inserted");
		List<ResearchGateMemberProfile> lista = obtainResearchGateProfile(dep.get(0), driver);
		
		lista.forEach(a->{
			System.out.println("NAMES->"+a.getNames());
			System.out.println("REF->"+a.getProfile_url());
		});
		*/
		
		//test#2 void obtainDataProfileContributionsResearchGate(String projects_url, WebDriver driver)
		//obtainDataProfileContributionsResearchGate(url, driver);
		
		
		
		Thread.sleep(2000);
		
		driver.close();
		driver.quit();
	}
	
	//3 parse institutiton departments researchgate
	/*
	 * Funcion que registra los departamento de una Institucion (universidad), dado el codigo de un pais
	 * @params: String countryCode -> codigo del pais
	 * 			Integer limit -> limita la catidad de partamentos a registrar
	 */
	public void registerInstitutionDepartmentsReseachGate(String countryCode, Integer limit){
		//lista instituciones que  registraran departamentos por codigo de pais,campo updated = false.
		List<Institutions> lista = this.institution.findResearchGateInstitutionsByCountryCode(countryCode, limit ,false);
		
		WebDriver driver = startDriver("http://127.0.0.1:9515");
		try {
			loginOnResearchGate(driver);
			
			for (Institutions ins : lista) {
				//lista instituciones no actualizadas, dado un CODE de pais
				List<ResearchGateInstitutions> institutions = obtainInstitutionDepartments( ins.getLink(), driver);
				
				if(institutions.size() > 0) {
					//this.depatment.saveResearchGateDepartments(ins, links);
					this.depatment.saveResearchGateDepartments(ins, institutions);
					System.out.println("Institution :"+ins.getDescription());
					
					ins.setUpdated(true);
					this.institution.saveInstitutions(ins);
				}
			}
			
		} catch (NoSuchElementException e) {
			System.out.println("Error exception chrone drive: "+e.getMessage());
		}finally {
			driver.close();
			driver.quit();
		}
	}
	
	//2 login on researchgate
	public void loginOnResearchGate(WebDriver driver)
	{
		try {
			driver.manage().window().maximize();
			driver.get("https://www.researchgate.net/login");
			Thread.sleep(2000);
			driver.findElement(By.id("input-login")).sendKeys("evelasqu@dcc.uchile.cl");
			driver.findElement(By.id("input-password")).sendKeys("vegetawc123");
			Thread.sleep(1000);
			driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[1]/div/div/div/div/form/div/div[4]/button")).click();
		}catch (NoSuchElementException | InterruptedException e) {
			System.err.println("Function : loginOnResearchGate(),"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	//1
	public WebDriver startDriver(String urlServer)
	{
		WebDriver res = null;
		try {
			ChromeOptions options = new ChromeOptions();
			//options.addArguments("--headless", "--disable-gpu", "--blink-settings=imagesEnabled=false");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			//WebDriver driver = new ChromeDriver(options);
			//res = new RemoteWebDriver(new URL(urlServer), new ChromeOptions());
			//res = new RemoteWebDriver(new URL(urlServer), new ChromeDriver(options));
			res = new RemoteWebDriver(new URL(urlServer), capabilities);

		} catch (MalformedURLException e) {
			System.err.println("Chrome urlserver OFF : "+e.getMessage());
		}
		return res;
	}
	
	public void parseInstitutionMembers(Institutions ins,WebDriver driver) throws InterruptedException
	{
		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.get(ins.getLink());
		//driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div[2]/div/a[4]")).click();
        //This will scroll the web page till end.		
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(3000);
        
        List<WebElement> pager = driver.findElements(By.cssSelector("[class='c-list-navi pager'] > a"));
        
        System.out.println("pager size ->"+pager.size());
        if(pager.size() > 1)
        {
	        for (int i = 1; i < pager.size(); i++) 
	        {
	        	if(i > 1)
	        		driver.findElement(By.cssSelector("[data-target-page="+i+"]")).click();
	        	
	        	String pagesearch = driver.getCurrentUrl();//inicio pagina
	        	
	        	//get list members
	        	processListMembers(driver);
	        	System.out.println("Page -> "+pagesearch);
			}
        }else {
        	processListMembers(driver);
        	
        }
        
	}
	
	public List<String> processListMembers(WebDriver driver)
	{
		List<String> perfiles = new ArrayList<String>();
		List<WebElement> members = driver.findElements(By.cssSelector("[class='list people-list-m'] > li"));
    	System.out.println("Members->"+members.size());
		
    	int contador = 1;
    	for (int j = 0; j < members.size(); j++) {
			
    		WebElement member= members.get(j).findElement(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div[4]/div/div/div[1]/div/ul/li["+contador+"]/div[3]/h5/a"));
    		perfiles.add(member.getAttribute("href"));
    		contador++;
		}
    	return perfiles;
	}

	/*
	 * Funcion que retorna un hashmap con los nombres y URLS de los departamentos de una Universidad
	 * @param : link ->link de la universidad en researchgate
	 */
	public List<ResearchGateInstitutions> obtainInstitutionDepartments(String link, WebDriver driver)
	{
		//HashMap<String, String> res = new HashMap<String,String>();
		List<ResearchGateInstitutions> res = new ArrayList<>();
		ResearchGateWebElements rg  = new ResearchGateWebElements();
		
		try{
			driver.get( link );//institution web page
			Thread.sleep(2000);
			List<WebElement> institutionOptions = driver.findElements(By.cssSelector("[class='tab-bar-plain-inner'] > a"));
			
			if(rg.isDepartmentOptionEnable(institutionOptions)){

				WebElement deptoOption = rg.getWebElementByName("Departments", institutionOptions);
				deptoOption.click();
				Thread.sleep(2000);
				
				scrollToFooter(driver, Long.valueOf(1000));
				
				//driver.findElements(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div[4]/div/div/div[1]/div[1]/*"));
				List<WebElement> deptos = driver.findElements(By.cssSelector("[class='list '] > div"));
																		
				for (int i = 0; i < deptos.size(); i++) 
				{
					if(rg.isDepartmentHasMembers(deptos.get(i)) )
					{
						WebElement depto = deptos.get(i).findElement(By.cssSelector("[class='name'] > a"));
						WebElement stats = deptos.get(i).findElement(By.cssSelector("[class='stats']"));
						
						res.add(new ResearchGateInstitutions(depto.findElement(By.tagName("span")).getText(),
							depto.getAttribute("href"), 
							Integer.parseInt(stats.findElement(By.className("number")).getText() ) ));
					}
				}				
			}
		
		} catch (NoSuchElementException | InterruptedException e) {
			System.err.println("Function :obtainInstitutionDepartments(), "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	/*
	 * 3paso, registro de los miembros de un departamento validado y estado 1.inserted
	 */
	public void registerDepartmentMembersResearchGate(String country_code, Integer limit)
	{
		//lista departamentos por codigo pais, a partir de los cuales se obtendran os miembros del departamento
		//1.inserted->departamento recien registrado AND validate=true
		List<Departments> deptos = this.depatment.listDepartmentsByInsitution(country_code, limit, "1.inserted");
//		List<Departments> deptos = new ArrayList<>(); deptos.add(this.depatment.findById(new Long(31)).get());
		
		WebDriver driver = startDriver("http://127.0.0.1:9515");
		
		try {
			loginOnResearchGate(driver);
			Integer country_id = (int) this.country.findCountryByCode(country_code).get().getId(); 
			
			for (Departments dep : deptos) {
				System.out.println("Department :"+dep.getDescription());
				//obtiene la lista de profiles con los nombres de miembreos y la url a su profile
				List<ResearchGateMemberProfile> profiles = obtainResearchGateProfile(dep, driver);
				
				System.out.println("Members :"+profiles.size());
				int contador = 0;
				List<ResearchGateMemberProfile> csv = new ArrayList<>();
				
				for (ResearchGateMemberProfile perfil : profiles) {
					System.out.println("RESEARCHER :"+contador+" "+perfil.getNames());
					//llamada a la función que obtiene skills, experience y proyectos de un perfil de reseachgate
					Map<String, List<String> > disciplines_skills = obtainDataDisciplineSkills(perfil.getProfile_url(), driver);
//					perfil.setDisciplines(disciplines_skills.get("disciplines"));
//					perfil.setSkillsExpertise(disciplines_skills.get("skills"));
					
					List<ResearchGateProfileContributions> conts = obtainDataProfileContributionsResearchGate(perfil.getProfile_url(), driver);
//					perfil.setContributions(conts);
					
//					System.out.println("*********"+perfil.getNames()+"******************");
//					System.out.println("DISCIPLINES:"+perfil.getDisciplines().size());
//					perfil.getDisciplines().forEach(d->{
//						System.out.println(d);
//					});
//					
//					System.out.println("EXPERTISE:"+perfil.getSkillsExpertise().size());
//					perfil.getSkillsExpertise().forEach(s->{
//						System.out.println(s);
//					});
//					System.out.println("***************************");	
//					
//					
//					perfil.getContributions().forEach(c->{
//						System.out.println("title:"+c.getPub_title());
//						System.out.println("date:"+c.getPub_date());
//						System.out.println("event:"+c.getPub_event());
//						System.out.println("type:"+c.getPub_type());
//						System.out.println("--------------authors"+c.getAuthors().size()+"-----------");
//						c.getAuthors().forEach(a->{
//							System.out.println(a);
//						});
//					});
//					System.out.println("----------------------------------------");
					
					csv.add(new ResearchGateMemberProfile(perfil.getNames(), 
							perfil.getProfile_url(),
							disciplines_skills.get("disciplines"),
							disciplines_skills.get("skills"), 
							conts ) );
							
					contador++;
					
				}
				
				parseToCSV(	country_id, country_code, 
							(int)dep.getInstitutions().getId(), dep.getInstitutions().getDescription(),
							(int) dep.getId(), dep.getDescription(),csv);
				System.out.println("PROFILES TO CSV: "+contador);
				
				dep.setPosition("2.members_registered");
				this.depatment.save(dep);
				
				System.out.println("Department updated :"+dep.getDescription());
				
			}
		}catch (NoSuchElementException | InterruptedException e) {
			System.err.println("function :registerDepartmentMembersResearchGate(),"+e.getMessage());
			e.printStackTrace();
		} 
		finally {
			driver.close();
			driver.quit();
		}
	}
	
	/*	2do paso posterior a registrar los departamentos de las instituciones.
		obtiene la informacion de los nombres y links de los perfiles de una persona en researchGate
	 */
	public List<ResearchGateMemberProfile> obtainResearchGateProfile(Departments dep, WebDriver driver) {
		List<ResearchGateMemberProfile> res = new ArrayList<ResearchGateMemberProfile>();
		ResearchGateWebElements rg = new ResearchGateWebElements();
		
		try {
			//pagina incio del depto
			driver.get(dep.getLinks());
			//research gate department options
			List<WebElement> options = driver.findElements(By.cssSelector("[class='tab-bar-plain-inner'] > a"));	
			WebElement member_option = rg.fidnElementByTextAttValAtt(options, "Members", "class", "btn btn-large ajax-page-load members");
			//WebElement member_option = rg.fidnElementByTextAttValAtt(options, "Members", "class", "btn btn-large ajax-page-load members selected");
			
			if( Objects.nonNull(member_option) )
			{
				member_option.click();
				
				Thread.sleep(2000);
				//1000 -> scroll time
				scrollToFooter(driver, Long.valueOf(1000));
				List<WebElement> members = driver.findElements(By.cssSelector("[class='list people-list-m'] > li"));
				
				for (WebElement web : members) {
//					WebElement data = web.findElement(By.cssSelector("[class='indent-content'] a"));
//					res.add( new ResearchGateMemberProfile( data.getText(), data.getAttribute("href") ));
					WebElement data = web.findElement(By.xpath("./div[@class='indent-content']/h5/a"));
					res.add( new ResearchGateMemberProfile( data.getText(), data.getAttribute("href") ));
				}
			}else 
			{
				System.out.println("INGRESA ELSE");
			}
		}catch (NoSuchElementException | InterruptedException e) {
			System.err.println("function : obtainResearchGateProfile() ,"+e.getMessage());
			e.printStackTrace();
		}
		
		return res;
	}
	
	/*
	 * 4to paso, funcion que obtiene los skills y la expertise de un profile en researchgate
	 * return -> {"disciplines":[disciplinas],"skills":[skills]}
	 */
	public Map< String,List<String> > obtainDataDisciplineSkills(String url, WebDriver driver) throws InterruptedException
	{
		Map< String, List<String> > res = new HashMap<>();
		ResearchGateWebElements rg = new ResearchGateWebElements();
		driver.get(url);
		Thread.sleep(3000);
		
		try {
			//scrollToFooter(driver, Long.valueOf(1000));
			WebElement menu = driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div/div[2]/nav"));
			List<WebElement> overview_options = menu.findElements(By.cssSelector("[class='nova-c-nav__items'] > a"));
			WebElement over_view = rg.findInProfileOptions(overview_options, "Overview");
			
			if( Objects.nonNull(over_view) ) {
			
				WebElement top_box = driver.findElement(By.cssSelector("[class='profile-overview__box-top'] *"));
			
				//lista box profile researcher
				List<WebElement> about = top_box.findElements(By.cssSelector("[class='nova-o-stack nova-o-stack--gutter-xl nova-o-stack--spacing-none nova-o-stack--no-gutter-outside'] > div"));
				WebElement about_box = rg.findWebElementByAttribute("class", "nova-o-stack__item", about);
				
				if(Objects.nonNull( about_box ) )//existe about
				{
					res = rg.obtainProfileDisciplinesSkills(about_box);
					
				}else //no existe about -> busca opcion research
				{
					System.out.println("Profile sin about");
					res.put("disciplines", new ArrayList<String>(0));
					res.put("skills", new ArrayList<String>(0));
				}
			}
			/*
			//inicio data profile projects
			WebElement research = rg.findInProfileOptions(overview_options, "Research");
			int contributions = 0;
			
			if( Objects.nonNull(research) ) {
				research.click();
				Thread.sleep(2000);
				scrollToFooter(driver, Long.valueOf(1000));
				Thread.sleep(3000);
				
				//List<WebElement> menu_contribution = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div/div[3]/div/div/div/div/div/div/*")); 
				WebElement profile_contirbutions = driver.findElement(By.cssSelector("[class='profile-contributions'] > *"));
				List<WebElement> options = profile_contirbutions.findElements(By.xpath("./div/div/*"));
				
				WebElement menu_item = rg.findWebElementByAttribute("class", "nova-l-flex__item nova-l-flex__item--shrink profile-contributions__menu", options);
				WebElement contribution_item = rg.findWebElementByAttribute("class", "nova-l-flex__item nova-l-flex__item--grow profile-contributions__content", options);
							      													  	
				// verifica si existen proyectos del researcher
				if(Objects.nonNull(menu_item))
				{
					contributions = rg.findContributionNumberResearchGate(menu_item); 
					
					if( contributions > 40 && contributions < 60)
						Thread.sleep(4000);
					else if( contributions > 60 && contributions < 100)
						Thread.sleep(5000);
					else if( contributions > 100)
						Thread.sleep(7000);
					
					if( contributions > 0 && Objects.nonNull(contribution_item)) {
						System.out.println("Researcher Contributions : ");
//						List<ResearchGateProfileContributions> contributions_list = rg.findContributionsResearchGate(contribution_item);
//						res.setContributions(contributions_list);
						
					}
				}
			}*/
			
		}catch (NoSuchElementException | NullPointerException e) {
			System.err.println("Function :obtainDataProfileResearchGate(),"+e.getMessage() );
			e.printStackTrace();
		}
		
		return res;
	}
	
	/*
	 * 5to paso, funcion que obtiene las contribuciones de un profile en researchgate
	 * return List<ResearchGateProfileContributions> 
	 */
	public List<ResearchGateProfileContributions> obtainDataProfileContributionsResearchGate(String projects_url, WebDriver driver)
	{
		List<ResearchGateProfileContributions> res = new ArrayList<>();
		ResearchGateWebElements rg = new ResearchGateWebElements();
		int contributions = 0;
			
		try {
			driver.get(projects_url);
			Thread.sleep(1000);
			//scrollToFooter(driver, Long.valueOf(1000));
						
			WebElement menu = driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div/div[2]/nav"));
			List<WebElement> project_options = menu.findElements(By.cssSelector("[class='nova-c-nav__items'] > a"));		
			
			//inicio data profile projects
			WebElement research = rg.findInProfileOptions(project_options, "Research");

			if (Objects.nonNull( research )) {
				research.click();
				Thread.sleep(2000);
				scrollToFooter(driver, Long.valueOf(1000));
				Thread.sleep(2000);
				
				//List<WebElement> menu_contribution = driver.findElements(By.xpath("/html/body/div[1]/div[3]/div[1]/div/div/div[3]/div/div/div/div/div/div/*")); 
				WebElement profile_contirbutions = driver.findElement(By.cssSelector("[class='profile-contributions'] > *"));
				List<WebElement> options = profile_contirbutions.findElements(By.xpath("./div/div/*"));
				
				WebElement menu_item = rg.findWebElementByAttribute("class", "nova-l-flex__item nova-l-flex__item--shrink profile-contributions__menu", options);
				WebElement contribution_item = rg.findWebElementByAttribute("class", "nova-l-flex__item nova-l-flex__item--grow profile-contributions__content", options);
							      													  	
				// verifica si existen proyectos del researcher
				if(Objects.nonNull(menu_item))
				{
					contributions = rg.findContributionNumberResearchGate(menu_item); 
					
					if( contributions > 19 && contributions < 60){
						scrollToFooter(driver, Long.valueOf(1000));
						Thread.sleep(5000);
						
					}
					else if( contributions > 61 && contributions < 100){
						scrollToFooter(driver, Long.valueOf(1000));
						Thread.sleep(8000);
					}
					else if( contributions > 100) {
						scrollToFooter(driver, Long.valueOf(2000));
						Thread.sleep(12000);
					}
					
					if( contributions > 0 && Objects.nonNull(contribution_item)) {
						System.out.println("Researcher Contributions :");
						res = rg.findContributionsResearchGate(contribution_item);
						
						if(res.size() != contributions)
							throw new NoSuchElementException("Error el numero de contribuciones del researcher:"+res.size()+"!="+contributions); 
					}
				}
	
			} else { System.out.println("opcion no encontrada");}
			
		}catch (NoSuchElementException | InterruptedException  e) {
			System.err.println("function: obtainDataProfileContributionsResearchGate(), "+e.getMessage() );
			e.printStackTrace();
		}
		
		return res;
	}
	
	public void scrollToFooter(WebDriver driver, Long time) throws InterruptedException
	{
		try {
			WebElement footer = driver.findElement(By.id("footer"));
			ResearchGateWebElements rg = new ResearchGateWebElements();
			int partes = rg.getPartes( footer.getLocation().getY() );
			System.out.println("Divisiones->"+partes);
			List<Integer> nums = rg.splitNumber( partes, footer.getLocation().getY());
			JavascriptExecutor js = (JavascriptExecutor) driver;
			
			int desde = 0;
			for (int i = 0; i < nums.size(); i++) {
				Thread.sleep(time);
				js.executeScript("scrollBy("+ desde +","+ nums.get(i) +")");
				desde = nums.get(i);
				Thread.sleep(time);
			}
			System.out.println("X->"+footer.getLocation().getX()+"\nY->"+footer.getLocation().getY());
		}catch(NoSuchElementException e) {
			System.err.println("Function : scrollToFooter(),"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Inicio CSV parsing
	 * 
	 */
	
	public void parseToCSV( int country_id,String country_code,
			int institution_id,String institution,
			int department_id, String department,List<ResearchGateMemberProfile> profiles) 
	{
			File file  = new File("/Users/raven/Documents/workspace-spring-tool-suite-4-4.3.2.RELEASE/LiteratureReviewApp/src/main/resources/static/csvFiles/"+country_code+".csv");
			if(!file.exists()){
				try {
					System.out.println("File Created:"+file.getName());
					
					String[] header = {"country_id","country_code","institution_id","institution",
							"department_id","department","author","contrib_title",
							"doc_type","doc_date","event_name","all_authors","authors","disciplines","skillsExpertise","author_url","contrib_url" };
					
			        CSVWriter csvWriter = new CSVWriter( new FileWriter(file.getPath(),true),
			        		'|', 
							CSVWriter.NO_QUOTE_CHARACTER,
							CSVWriter.DEFAULT_ESCAPE_CHARACTER,
							CSVWriter.RFC4180_LINE_END
							);
			            
			        csvWriter.writeNext(header);
			        csvWriter.close();
				}catch (IOException e) {
					System.out.println("Error al crear el archivo CSV"+e.getMessage());
					e.printStackTrace();
				}
				
			}
			
			try(FileWriter writer = new FileWriter(file.getPath(),true);){
				
				System.out.println("File Existed:"+file.getName());
				CSVWriter csvWriter = new CSVWriter(writer,
						'|', 
						CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER,
						CSVWriter.RFC4180_LINE_END );

				for (ResearchGateMemberProfile profile : profiles) {
					System.out.println("CONTRIBUTIONS->"+profile.getContributions().size());
					System.out.println("PROFILE->"+profile.getNames());
					System.out.println("PROFILE URL->"+profile.getProfile_url());
					
					String disciplines = Objects.nonNull( profile.getDisciplines() ) ? String.join("#", profile.getDisciplines().toArray(new String[0])) :"[ ]";
					String skills = profile.getSkillsExpertise().size() > 0 ? String.join("#", profile.getSkillsExpertise().toArray(new String[0])) : "[ ]";
					
					List<ResearchGateBeanToCSV> data = new ArrayList<>();
					
					if(profile.getContributions().size() > 0)
					{
						for (ResearchGateProfileContributions cont : profile.getContributions()) {
//							if(!Objects.equals(cont.getPub_title(), "")
//										&& Objects.nonNull(cont.getPub_title()))
//							{
								String authors = cont.getAuthors().size() > 0 ? String.join("#", cont.getAuthors().toArray(new String[0])):"";
								data.add(new ResearchGateBeanToCSV( 
										String.valueOf( country_id ), country_code, 
										String.valueOf(institution_id), institution,
										String.valueOf(department_id), department, 
										new String(profile.getNames().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8),
										//profile.getNames(),
										new String(profile.getProfile_url().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8),
										new String(cont.getPub_title().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8),
										new String(cont.getPub_url().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8), 
										cont.getPub_type(),
										cont.getPub_date(),
										cont.getPub_event(), 
										String.valueOf(cont.isHasAllAuthors()), 
										"[ "+authors+" ]", 
										"[ "+disciplines+" ]",
										"[ "+skills+" ]"));
//							}
						}
					}else {
						System.out.println("0 CONTRIBUTIONS PROFILE");
						data.add(new ResearchGateBeanToCSV( 
								String.valueOf( country_id ), country_code, 
								String.valueOf(institution_id), institution,
								String.valueOf(department_id), department, 
								profile.getNames().length()>0 ? new String(profile.getNames().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8): " ",
								profile.getProfile_url().length()>0 ? new String(profile.getProfile_url().getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8):" ",
								" ",
								" ", 
								" ",
								" ",
								" ", 
								String.valueOf(false), 
								"[  ]", 
								disciplines,
								skills));
						
					}
					
					for (int i = 0; i < data.size(); i++) {
						csvWriter.writeNext(data.get(i).parse());	
					}
				}					
				csvWriter.close();
				writer.close();
			}catch (IOException | NullPointerException e) {
				System.err.println("CSV file error :"+e.getMessage());
				e.printStackTrace();
			}
	}
	
	
	public static class ResearchGateBeanToCSV{
		
		@CsvBindByName(column = "country_id")
		@CsvBindByPosition(position = 0)
		public String country_id;
		@CsvBindByName(column = "country_code")
		@CsvBindByPosition(position = 1)
		public String country_code;
		@CsvBindByName(column = "institution_id")
		@CsvBindByPosition(position = 2)
		public String institution_id;
		@CsvBindByName(column = "institution")
		@CsvBindByPosition(position = 3)
		public String institution;
		@CsvBindByName(column = "department_id")
		@CsvBindByPosition(position = 4)
		public String department_id;
		@CsvBindByName(column = "department")
		@CsvBindByPosition(position = 5)
		public String department;
		@CsvBindByName(column = "author")
		@CsvBindByPosition(position = 6)
		public String author;
		@CsvBindByName(column = "contrib_title")
		@CsvBindByPosition(position = 7)
		public String contrib_title;
		@CsvBindByName(column = "doc_type")
		@CsvBindByPosition(position = 8)
		public String doc_type;
		@CsvBindByName(column = "doc_date")
		@CsvBindByPosition(position = 9)
		public String doc_date;
		@CsvBindByName(column = "event_name")
		@CsvBindByPosition(position = 10)
		public String event_name;
		@CsvBindByName(column = "all_authors")
		@CsvBindByPosition(position = 11)
		public String all_authors;
		@CsvBindByName(column = "authors")
		@CsvBindByPosition(position = 12)
		public String authors;
		@CsvBindByName(column = "disciplines")
		@CsvBindByPosition(position = 13)
		public String disciplines;
		@CsvBindByName(column = "skillsExpertise")
		@CsvBindByPosition(position = 14)
		public String skillsExpertise;
		@CsvBindByName(column = "author_url")
		@CsvBindByPosition(position = 15)
		public String author_url;
		@CsvBindByName(column = "contrib_url")
		@CsvBindByPosition(position = 16)
		public String contrib_url;
		
		
		public ResearchGateBeanToCSV() {
			this.country_id = "";
			this.country_code = "";
			this.institution_id = "";
			this.institution = "";
			this.department_id = "";
			this.department = "";
			this.author = "";
			this.author_url = "";
			this.contrib_title = "";
			this.contrib_url = "";
			this.doc_type = "";
			this.doc_date = "";
			this.event_name = "";
			this.all_authors = "";
			this.authors = "";
			this.disciplines = "";
			this.skillsExpertise = "";
		}

		public ResearchGateBeanToCSV(String country_id, String country_code, String institution_id, String institution,
				String department_id, String department, String author, String author_url, String contrib_title,
				String contrib_url, String doc_type, String doc_date, String event_name, String all_authors,
				String authors, String disciplines, String skillsExpertise) {
			this.country_id = country_id;
			this.country_code = country_code;
			this.institution_id = institution_id;
			this.institution = institution;
			this.department_id = department_id;
			this.department = department;
			this.author = author;
			this.author_url = author_url;
			this.contrib_title = contrib_title;
			this.contrib_url = contrib_url;
			this.doc_type = doc_type;
			this.doc_date = doc_date;
			this.event_name = event_name;
			this.all_authors = all_authors;
			this.authors = authors;
			this.disciplines = disciplines;
			this.skillsExpertise = skillsExpertise;
		}

		public String[] parse()
		{
			String[] res = {
					this.country_id,
					this.country_code,
					this.institution_id,
					this.institution,
					this.department_id,
					this.department,
					this.author,
					this.contrib_title,
					this.doc_type,
					this.doc_date,
					this.event_name,
					this.all_authors,
					this.authors,
					this.disciplines,
					this.skillsExpertise,
					this.author_url,
					this.contrib_url
			};	
			return res;
		}
		public String getCountry_id() {
			return country_id;
		}

		public void setCountry_id(String country_id) {
			this.country_id = country_id;
		}

		public String getCountry_code() {
			return country_code;
		}

		public void setCountry_code(String country_code) {
			this.country_code = country_code;
		}

		public String getInstitution_id() {
			return institution_id;
		}

		public void setInstitution_id(String institution_id) {
			this.institution_id = institution_id;
		}

		public String getInstitution() {
			return institution;
		}

		public void setInstitution(String institution) {
			this.institution = institution;
		}

		public String getDepartment_id() {
			return department_id;
		}

		public void setDepartment_id(String department_id) {
			this.department_id = department_id;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getAuthor_url() {
			return author_url;
		}

		public void setAuthor_url(String author_url) {
			this.author_url = author_url;
		}

		public String getContrib_title() {
			return contrib_title;
		}

		public void setContrib_title(String contrib_title) {
			this.contrib_title = contrib_title;
		}

		public String getContrib_url() {
			return contrib_url;
		}

		public void setContrib_url(String contrib_url) {
			this.contrib_url = contrib_url;
		}

		public String getDoc_type() {
			return doc_type;
		}

		public void setDoc_type(String doc_type) {
			this.doc_type = doc_type;
		}

		public String getDoc_date() {
			return doc_date;
		}

		public void setDoc_date(String doc_date) {
			this.doc_date = doc_date;
		}

		public String getEvent_name() {
			return event_name;
		}

		public void setEvent_name(String event_name) {
			this.event_name = event_name;
		}

		public String getAll_authors() {
			return all_authors;
		}

		public void setAll_authors(String all_authors) {
			this.all_authors = all_authors;
		}

		public String getAuthors() {
			return authors;
		}

		public void setAuthors(String authors) {
			this.authors = authors;
		}

		public String getDisciplines() {
			return disciplines;
		}

		public void setDisciplines(String disciplines) {
			this.disciplines = disciplines;
		}

		public String getSkillsExpertise() {
			return skillsExpertise;
		}

		public void setSkillsExpertise(String skillsExpertise) {
			this.skillsExpertise = skillsExpertise;
		}
		
	}


		
}
