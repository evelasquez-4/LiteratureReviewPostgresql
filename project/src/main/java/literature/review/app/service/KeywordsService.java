package literature.review.app.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Keywords;
import literature.review.app.repository.KeywordsRepository;


@Service
public class KeywordsService{
	@Autowired
	private KeywordsRepository keyword;
	
	public Keywords getKeywordByDescription(String desc)
	{
		Optional<Keywords> key = keyword.findByDescription(desc);
		
		return key.isPresent() ? key.get() : keyword.findById((long) 0).get();
	}
	
	
	public Keywords registerKeyword(String desc)
	{
		Keywords res = new Keywords();
		Optional<Keywords> key = keyword.findByDescription(desc);
		
		if(key.isPresent())
			res = key.get();
		else
		{
			res.setDecription(desc);
			res.setCreatedAt(new Date());
			res = keyword.saveAndFlush(res);
		}
		return res;
	}
	
	

}
