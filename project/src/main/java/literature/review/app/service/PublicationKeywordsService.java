package literature.review.app.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Keywords;
import literature.review.app.model.PublicationKeywords;
import literature.review.app.model.Publications;
import literature.review.app.repository.PublicationKeywordsRepository;

@Service
public class PublicationKeywordsService {

	@Autowired
	private PublicationKeywordsRepository publication_keyword;
	@Autowired
	private KeywordsService keyword_service;
	
	public PublicationKeywords savePublicationKeyword(Publications publication,Keywords keyword)
	{
		PublicationKeywords pk = new PublicationKeywords();
		pk.setKeywords(keyword);
		pk.setPublications(publication);
		pk.setCreatedAt(new Date());
		
		return publication_keyword.save(pk);
	}
	
	public void registerPublicationsKeywords(List<String> keywords,Publications publication)
	{
		PublicationKeywords pk = new PublicationKeywords();
		for (String key : keywords) {
			Keywords keyword = this.keyword_service.registerKeyword(key);
			
			pk.setCreatedAt(new Date());
			pk.setPublications(publication);
			pk.setKeywords(keyword);
				
			this.publication_keyword.save(pk);
		}
	}
}
