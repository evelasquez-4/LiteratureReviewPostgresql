package literature.review.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.model.Departments;
import literature.review.app.service.DepartmentsService;

@RestController
@RequestMapping("/departments")
public class DepartmentsController {
	
	@Autowired
	private DepartmentsService department;
	
	/*
	 * 1-> registro de departamentos de una institucion
	 * 2-> miembros del departamento registrados
	 */
	private List<String> department_state = 
			Arrays.asList("1.inserted","2.members_registered");
	
	/*
	 * curl --location --request GET 'http://localhost:8081/departments/list_institution_departments' --header 'Content-Type: application/json' --data-raw '{"country_code":"AR","limit":10,"state":"1.inserted" }'
	 */
	@GetMapping(value = "/list_institution_departments", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Departments> listDepartmentsByInsitution(
			@RequestBody(required=true) Map<String, String> values) throws Exception
	{

		String code = values.containsKey("country_code")? values.get("country_code") :"DEF";
		Integer limit = values.containsKey("limit")? Integer.parseInt( values.get("limit") ):0;
		String state = values.get("state");
		
		if(this.department_state.contains(state))
			return this.department.listDepartmentsByInsitution(code, limit, state);
		else
			throw new Exception("state no coincide con ninguno de los estado de department_state");
	}
}
