package literature.review.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Departments;
import literature.review.app.repository.DepartmentsRepository;

@Service
public class DepartmentsService {
	@Autowired
	private DepartmentsRepository department;
	
	public Optional<Departments> findById(long id)
	{
		return department.findById(id);
	}
	
	public Departments getDefaultDepartment()
	{
		return department.findById((long)0).get();
	}
}